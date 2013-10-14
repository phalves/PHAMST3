package view.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;
import model.authentication.PasswordTree;
import model.authentication.PasswordTreeBrowser;
import view.MainFrame;
import controller.Conversor;
import controller.MainController;
import controller.authentication.AuthenticationState;
import controller.dbutils.DBUtils;

public class AuthenticatedState1 extends JPanel implements AuthenticationState {

	private ArrayList<JButton> buttons;
	private ArrayList<String> numbers;	
	private int BTN_WIDTH = 130;
	private int BTN_HEIGHT = 30;
	private JLabel label;
	private JLabel warning;
	
	private int numberOfClicks = 0;
	private PasswordTree passwordTree = new PasswordTree("");
	
	MainController controller = MainController.getMainControllerInstace();
	User user = controller.getCurrentUser();	
	DBUtils dbUtils = DBUtils.getDBUtilsInstace();
	
	public AuthenticatedState1() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		
		dbUtils.connect();
		dbUtils.logMessage(3001, user.getName());
		
		label = new JLabel("Insira sua senha baseado nos fonemas:");
		label.setBounds(20, 20, 300, 30);
		this.add(label);
		
		warning = new JLabel("");
		warning.setBounds(20, 160, 300, 30);
		warning.setFont(new Font("Serif", Font.PLAIN, 15));
		warning.setForeground(Color.RED);
		this.add(warning);
		
		//Inicia vetor de fonemas
		numbers = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("src/numeros.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		try {
			while((line = in.readLine()) != null)
			{
				//preenche vetor de fonemas do arquivo
				numbers.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//inicia os 6 JButton
		buttons = new ArrayList<JButton>();
		for(int i=0;i<5;i++)
		{
			JButton b = new JButton();
			b.addActionListener(new KeyPassActionListener(b));
			if(i<3)
				b.setBounds(20 + i*BTN_WIDTH, 80, BTN_WIDTH, BTN_HEIGHT);
			else
				b.setBounds(20 + (i-3)*BTN_WIDTH, 110, BTN_WIDTH, BTN_HEIGHT);
			this.add(b);
			this.buttons.add(b);
		}
		refreshButtons();
		
		dbUtils.disconnect();
		this.setVisible(true);
	}
	
	/*
	 * Retorna uma String do tipo "X - X" baseado na lista de
	 * numeros. Deve-se passar por parâmetro a partir de qual index os
	 * numeros devem ser retornados. Serão retornados até index + 2.
	 */
	private String getButtonText(int index) {
		index *= 2;
		StringBuilder builder = new StringBuilder();
		for(int i=0+index; i<2+index; i++)
		{
			if(i%2==0){
				builder.append(numbers.get(i)).append(" - ");
			}
			else{
				builder.append(numbers.get(i)).append(" ");
			}
			
		}
		return builder.toString();
	}
	
	/*
	 * Embaralha a lista de fonemas e distribui pelos botões.
	 */
	private void refreshButtons()
	{
		Collections.shuffle(numbers);
		for(int i=0;i<5;i++)
			buttons.get(i).setText(getButtonText(i));
	}
	
	private class KeyPassActionListener implements ActionListener {
		
		private JButton btn;
		
		public KeyPassActionListener(JButton button) {
			this.btn = button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] phonems = btn.getText().split(" ");
			refreshButtons();
			boolean wentToNextStep = false;
			
			if (numberOfClicks >= 3) {
				System.out.println("NUMERO DE TENTATIVAS++");
			} else {
				passwordTree.appendChildren(phonems);
				
				if (numberOfClicks == 2) {
					PasswordTreeBrowser passwdTBrowser = new PasswordTreeBrowser();
					passwdTBrowser.browse("", passwordTree);
					
					List<String> possiblePasswords = passwdTBrowser.getPossiblePasswds();
										
					String salt = user.getSALT();
					String passwd = user.getPasswd();
					
					for (String s : possiblePasswords) {

						String utf8_plainText = s + salt;
						
						try {
							MessageDigest messageDigest = MessageDigest.getInstance("MD5");
							messageDigest.update(utf8_plainText.getBytes());
							
							byte[] digest = messageDigest.digest();
							
						    if (Conversor.byteArrayToHexString(digest).equals(passwd)) {
						    	controller = MainController.getMainControllerInstace();
								String userName = controller.getCurrentUser().getName();
								dbUtils.connect();
								dbUtils.logMessage(3003, user.getName());
								dbUtils.logMessage(3002, user.getName());
								dbUtils.setNumberOfAttempts(Integer.valueOf(0), userName, DBUtils.STATE_1);
								dbUtils.disconnect();
						    	controller.setContentPane("next");
						    	wentToNextStep = true;
						    	break;
						    }
							
						} catch (NoSuchAlgorithmException exception) {
							exception.printStackTrace();
						}
					}
					if(!wentToNextStep) {
						dbUtils.connect();						
						dbUtils.logMessage(3004, user.getName());
						
						controller = MainController.getMainControllerInstace();
						String userName = controller.getCurrentUser().getName();
						int numberOfAttempts = dbUtils.getNumberOfAttempts(userName, DBUtils.STATE_1);
						numberOfAttempts++;
						
						if(numberOfAttempts == 1) dbUtils.logMessage(3005, userName);
						else if(numberOfAttempts == 2) dbUtils.logMessage(3006, userName);
						else if(numberOfAttempts == 3) dbUtils.logMessage(3007, userName);
						
						if(numberOfAttempts < 3) { //Se ainda não errou pela 3a vez
							dbUtils.setNumberOfAttempts(numberOfAttempts, userName, DBUtils.STATE_1);
							warning.setText("Senha errada. Número de tentativas: " + numberOfAttempts);
							numberOfClicks = -1;
							passwordTree = new PasswordTree("");
						} else {
							dbUtils.logMessage(3008, userName);
							dbUtils.logMessage(3002, userName);
							dbUtils.setNumberOfAttempts(0, userName, DBUtils.STATE_1);
							dbUtils.blockUser(userName);
							controller.restartAuthenticationContext();
						}
						
						dbUtils.disconnect();
					}
				}
				numberOfClicks++;
			}
		}
	}
}
