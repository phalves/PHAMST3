package controller;

import javax.swing.JPanel;

import model.User;
import view.MainFrame;
import view.panels.AuthenticatedState0;
import controller.authentication.AuthenticationContext;

public class MainController {
	
	// Singleton
	private static MainController mainControlletInstance = null;

	public static MainController getMainControllerInstace() {
		if (mainControlletInstance == null) {
			mainControlletInstance = new MainController();
		}
		return mainControlletInstance;
	}
	
	// Authentication Layout Flow Controller
	private AuthenticationContext authenticationContext = new AuthenticationContext();
	
	// MainFrame pointer
	private MainFrame mainFrame;
	
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public void setContentPane(String msg){
		if (msg.equals("next")) {
			authenticationContext.nextState();
		}
		
		mainFrame.setContentPane((JPanel) authenticationContext.getAuthenticationState());
		mainFrame.invalidate();
		mainFrame.validate();
	}
	
	/*
	 * AVISO: Usar diretamente esse método apenas 
	 * quando o usuario ja estiver autenticado 
	 * */
	public void changePanel(JPanel panel) {
		mainFrame.setContentPane(panel);
		mainFrame.invalidate();
		mainFrame.validate();
	}
	
	private User currentUser;
	
	public void setCurrentUser(User user) {
		this.currentUser = user;
	}
	
	public User getCurrentUser() {
		return this.currentUser;
	}
	
	public void restartAuthenticationContext() {
		this.authenticationContext = new AuthenticationContext();
		this.changePanel((JPanel)this.authenticationContext.getAuthenticationState());
	}
}
