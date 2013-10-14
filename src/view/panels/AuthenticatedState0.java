package view.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.User;
import view.MainFrame;
import controller.MainController;
import controller.authentication.AuthenticationState;
import controller.dbutils.DBUtils;

public class AuthenticatedState0 extends JPanel implements AuthenticationState {

	private JTextField loginTextField;
	private JLabel warning;
	
	DBUtils dbUtils = DBUtils.getDBUtilsInstace();

	public AuthenticatedState0() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		
		dbUtils.connect();
		dbUtils.logMessage(2001);
		dbUtils.disconnect();
		
		JLabel label = new JLabel("Login:");
		label.setBounds(10, 10, 100, 20);
		label.setVisible(true);

		loginTextField = new JTextField();
		loginTextField.setBounds(10, 35, 200, 25);
		loginTextField.setVisible(true);

		JButton button = new JButton("Submit");
		button.setBounds(10, 65, 100, 30);
		button.addActionListener(new SubmitLoginActionListener());
		button.setVisible(true);

		warning = new JLabel("");
		warning.setBounds(10, 100, 300, 30);
		warning.setFont(new Font("Serif", Font.PLAIN, 15));
		warning.setForeground(Color.RED);
		this.add(warning);

		this.add(label);
		this.add(loginTextField);
		this.add(button);

		this.setVisible(true);
	}

	private class SubmitLoginActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			String foundName = dbUtils.selectName(loginTextField.getText());
			dbUtils.disconnect();
			
			if (!foundName.equals("")) { //GO TO NEXT AUTH PHASE
				warning.setText("");
				dbUtils.connect();
				if( dbUtils.isUserBlocked(foundName) ) {
					warning.setText("You can't access it. Reason: BLOCKED");
					dbUtils.logMessage(2004, foundName);
				} else {
					MainController controller = MainController.getMainControllerInstace();
					User user = new User();
					user.setName(foundName);
					
					user.setSALT(dbUtils.selectSALT(foundName));
					user.setPasswd(dbUtils.selectPasswd(foundName));
					
					dbUtils.connect();
					dbUtils.logMessage(2003, foundName);
					dbUtils.logMessage(2002);
					dbUtils.disconnect();

					controller.setCurrentUser(user);
					controller.setContentPane("next");
				}
			} else {
				dbUtils.connect();
				dbUtils.logMessage(2005, loginTextField.getText());
				warning.setText("User not found. Please try again.");
			}
			dbUtils.disconnect();
		}
	}
}
