package view.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;
import view.MainFrame;
import controller.MainController;
import controller.authentication.AuthenticationState;
import controller.dbutils.DBUtils;

public class AuthenticatedState3 extends JPanel implements AuthenticationState {

	//	@Override
	//	public void nextState(AuthenticationContext authenticationContext) {
	//		// TODO Auto-generated method stub
	//		
	//	}
	DBUtils dbUtils = DBUtils.getDBUtilsInstace();	
	MainController controller = MainController.getMainControllerInstace();
	User user = controller.getCurrentUser();

	public AuthenticatedState3() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);

		dbUtils.connect();
		user.setRole(dbUtils.getUserRole(user.getName()));
		user.setAccess(dbUtils.getNumberOfAccess(user.getName()));
		user.setNomeProprio(dbUtils.selectNomeProprio(user.getName()));

		dbUtils.logMessage(5001, user.getName());

		//Cabeçalho
		JLabel loginName = new JLabel("Login: " + user.getName());
		loginName.setBounds(20, 10, 300, 30);
		this.add(loginName);

		JLabel role = new JLabel("Grupo: " + user.getRole());
		role.setBounds(20, 30, 300, 30);
		this.add(role);

		JLabel nomeProprio = new JLabel("Nome: " + user.getNomeProprio());
		nomeProprio.setBounds(20, 50, 300, 30);
		this.add(nomeProprio);

		//Corpo 1
		JLabel numberAccess = new JLabel("Total de acessos do usuário: " +
				user.getAccess());
		numberAccess.setBounds(20, 90, 300, 30);
		this.add(numberAccess);

		//Corpo 2
		if(user.getRole().equals("Administrador")) {
			JButton btn1 = new JButton("Cadastrar um novo usuário");
			btn1.setBounds(20, 130, 350, 40);
			btn1.addActionListener(new Btn1Listener());
			this.add(btn1);
		}

		JButton btn2 = new JButton("Alterar atributos de um usuário");
		btn2.setBounds(400, 130, 350, 40);
		btn2.addActionListener(new Btn2Listener());
		this.add(btn2);

		JButton btn3 = new JButton("Consultar pasta de arquivos secretos de um usuário");
		btn3.setBounds(20, 180, 350, 40);
		btn3.addActionListener(new Btn3Listener());
		this.add(btn3);

		JButton btn4 = new JButton("Sair do Sistema");
		btn4.setBounds(400, 180, 350, 40);
		btn4.addActionListener(new Btn4Listener());
		this.add(btn4);

		dbUtils.disconnect();

		this.setVisible(true);
	}

	private class Btn1Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(5002, user.getName());
			dbUtils.disconnect();
			MainController.getMainControllerInstace().changePanel(new Register());
		}
	}

	private class Btn2Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(5003, user.getName());
			dbUtils.disconnect();
			MainController.getMainControllerInstace().changePanel(new Edit());
		}
	}

	private class Btn3Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(5004, user.getName());
			dbUtils.disconnect();
			MainController.getMainControllerInstace().changePanel(new FolderExplorer());
		}
	}

	private class Btn4Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(5005, user.getName());
			dbUtils.disconnect();
			MainController.getMainControllerInstace().changePanel(new Exit());
		}
	}

}
