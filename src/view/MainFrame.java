package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import controller.MainController;
import controller.authentication.AuthenticationContext;
import controller.dbutils.DBUtils;


public class MainFrame extends JFrame {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	
	
	public MainFrame(String title) {
		super(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.setResizable(false);
		
		MainController controller = MainController.getMainControllerInstace();
		controller.setMainFrame(this);
		controller.setContentPane("");
		
		this.setVisible(true);
	}
	
	/*
	* Grupo 7:
	* Gabriel Lima
	* Luiz Henrique Cobucci
	*/
	public static void main (String args[]) {
		DBUtils dbUtils = DBUtils.getDBUtilsInstace();
		dbUtils.connect();
		dbUtils.logMessage(1001);
		dbUtils.disconnect();
		new MainFrame("[INF1416] - P1");
	}
	
}
