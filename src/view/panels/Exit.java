package view.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;
import view.MainFrame;
import controller.MainController;
import controller.dbutils.DBUtils;

public class Exit extends JPanel {
	DBUtils dbUtils = DBUtils.getDBUtilsInstace();	
	MainController controller = MainController.getMainControllerInstace();
	User user = controller.getCurrentUser();

	JLabel msgSaida = new JLabel("Pressione o botão Sair para confirmar.");

	public Exit() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		dbUtils.connect();
		dbUtils.logMessage(9001, user.getName());

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
		JLabel saida = new JLabel("Saída do sistema:");
		saida.setBounds(20, 130, 400, 40);
		this.add(saida);

		/* DEVEMOS APAGAR OS ARQUIVOS DECRIPTADOS E ENCERRAR O SISTEMA*/
		/*if(!canExit()) {
			msgSaida.setText("Sua senha está vencida. " +
					"Para sair, é necessário ter uma senha pessoal" +
					" válida para o próximo acesso. Pressione o botão " +
					"Voltar.”");
			msgSaida.setFont(new Font("Serif", Font.PLAIN, 15));
			msgSaida.setForeground(Color.RED);
		}
		msgSaida.setBounds(20, 160, 600, 30);
		this.add(msgSaida);

		if( canExit() ) {
			JButton sairBtn = new JButton("Sair");
			sairBtn.setBounds(130, 280, 100, 30);
			sairBtn.addActionListener(new SairListener());
			this.add(sairBtn);
		}*/

		JButton voltarBtn = new JButton("Voltar");
		voltarBtn.setBounds(20, 280, 100, 30);
		voltarBtn.addActionListener(new VoltarListener());
		this.add(voltarBtn);

		dbUtils.disconnect();
		this.setVisible(true);
	}

	private boolean canExit() {
		if(user.getAccess() % 3 == 0 && !user.changedPasswd())
			return false;
		return true;
	}

	private class SairListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(9002, user.getName());
			dbUtils.logMessage(1002);
			dbUtils.disconnect();
			System.exit(0);
		}
	}	

	private class VoltarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(9003, user.getName());
			dbUtils.disconnect();
			controller.changePanel(new AuthenticatedState3());
		}
	}
}
