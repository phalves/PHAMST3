package view.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import model.User;
import view.MainFrame;
import controller.Conversor;
import controller.MainController;
import controller.dbutils.DBUtils;

/* PODEMOS EXCLIR, NAO PRECISAREMOS DO EDIT */
public class Edit extends JPanel {

	DBUtils dbUtils = DBUtils.getDBUtilsInstace();	
	MainController controller = MainController.getMainControllerInstace();
	User user = controller.getCurrentUser();

	ArrayList<JButton> buttonsSenha1 = new ArrayList<JButton>();
	ArrayList<JButton> buttonsSenha2 = new ArrayList<JButton>();
	ArrayList<String> fonemas = new ArrayList<String>();
	JPasswordField senhaFonemas;
	JPasswordField senhaConfirmacao;
	private JLabel pathLabel;
	private String caminhoArquivo = "";
	File file;

	public Edit() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);

		dbUtils.connect();
		dbUtils.logMessage(7001, user.getName());

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
		JLabel numberUsers = new JLabel("Total de alterações feitas pelo usuário: " +
				dbUtils.getNumOfEdits(user.getName()));
		numberUsers.setBounds(20, 90, 300, 30);
		this.add(numberUsers);

		//Corpo 2
		JLabel formulario = new JLabel("Formulário de Alteração:");
		formulario.setBounds(20, 130, 350, 40);
		this.add(formulario);

		JLabel senhaPessoalLabel = new JLabel("Senha pessoal: ");
		senhaPessoalLabel.setBounds(20, 180, 200, 30);
		this.add(senhaPessoalLabel);

		senhaFonemas = new JPasswordField();
		senhaFonemas.setBounds(240, 180, 200, 30);
		senhaFonemas.setEnabled(false);
		this.add(senhaFonemas);

		//Inicia vetor de fonemas
		ArrayList<String> fonemas = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("src/numeros.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		try {
			int x = 0;
			int y = 0;
			while((line = in.readLine()) != null)
			{
				fonemas.add(line);
				//preenche vetor de fonemas do arquivo
				JButton fonemaBtn = new JButton(line);
				if( x < 12 ) {
					fonemaBtn.setBounds(450 + x*25, 180, 25, 15);
					fonemaBtn.setFont(new Font("Arial",Font.BOLD,8));
					fonemaBtn.setMargin(new java.awt.Insets(1, 2, 1, 2));
				} else {
					fonemaBtn.setBounds(450 + y*25, 195, 25, 15);
					fonemaBtn.setFont(new Font("Arial",Font.BOLD,8));
					fonemaBtn.setMargin(new java.awt.Insets(1, 2, 1, 2));
					y++;
				}
				fonemaBtn.addActionListener(new FonemaListener(fonemaBtn));
				x++;
				this.add(fonemaBtn);
				buttonsSenha1.add(fonemaBtn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		JLabel senhaConfirmacaoLabel = new JLabel("Confirmação senha pessoal:");
		senhaConfirmacaoLabel.setBounds(20, 220, 200, 30);
		this.add(senhaConfirmacaoLabel);

		senhaConfirmacao = new JPasswordField();
		senhaConfirmacao.setBounds(240, 220, 200, 30);
		senhaConfirmacao.setEnabled(false);
		this.add(senhaConfirmacao);

		int x = 0;
		int y = 0;
		for(String f : fonemas) {
			JButton confirmBtn = new JButton(f);
			if( x < 12 ) {
				confirmBtn.setBounds(450 + x*25, 220, 25, 15);
				confirmBtn.setFont(new Font("Arial",Font.BOLD,8));
				confirmBtn.setMargin(new java.awt.Insets(1, 2, 1, 2));
			} else {
				confirmBtn.setBounds(450 + y*25, 235, 25, 15);
				confirmBtn.setFont(new Font("Arial",Font.BOLD,8));
				confirmBtn.setMargin(new java.awt.Insets(1, 2, 1, 2));
				y++;
			}
			confirmBtn.addActionListener(new ConfirmaListener(confirmBtn));
			x++;
			this.add(confirmBtn);
			buttonsSenha2.add(confirmBtn);
		}

		JLabel caminhoPublicKeyLabel = new JLabel("Arquivo chave pública:");
		caminhoPublicKeyLabel.setBounds(20, 255, 200, 30);
		this.add(caminhoPublicKeyLabel);

		JButton fileButton = new JButton("Selecionar");
		fileButton.setBounds(240, 260, 100, 20);
		fileButton.addActionListener(new FileButtonActionListener());
		this.add(fileButton);

		pathLabel = new JLabel("Nenhum arquivo selecionado.");
		pathLabel.setBounds(350, 250, 400, 40);
		pathLabel.setFont(new Font("Serif", Font.PLAIN, 12));
		this.add(pathLabel);

		JButton alterarBtn = new JButton("Alterar");
		alterarBtn.setBounds(20, 300, 100, 30);
		alterarBtn.addActionListener(new AlterarListener());
		this.add(alterarBtn);

		JButton voltarBtn = new JButton("Voltar");
		voltarBtn.setBounds(130, 300, 100, 30);
		voltarBtn.addActionListener(new VoltarListener());
		this.add(voltarBtn);

		dbUtils.disconnect();
		this.setVisible(true);
	}

	private class FileButtonActionListener implements ActionListener {
		@Override
		/*
		 * Inicia o atributo da classe do tipo File
		 *  passando o path do arquivo selecionado.
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser arquivo = new JFileChooser();
			arquivo.setDialogTitle("Selecione o arquivo de chave publica");
			int retorno = arquivo.showOpenDialog(null);
			if(retorno == JFileChooser.APPROVE_OPTION){
				caminhoArquivo = arquivo.getSelectedFile().getAbsolutePath();
				pathLabel.setText(caminhoArquivo);
				file = new File(caminhoArquivo);
			}
		}
	}

	private class FonemaListener implements ActionListener {

		private JButton btn;

		public FonemaListener(JButton button) {
			this.btn = button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			btn.setEnabled(false);
			String fonema = btn.getText();
			String senhaAteEntao = senhaFonemas.getText();
			if(senhaAteEntao.isEmpty() || senhaAteEntao.length() == 6 ) {
				senhaFonemas.setText(fonema);
			} else {
				senhaFonemas.setText(senhaAteEntao.concat(fonema));
			}

			if(senhaFonemas.getText().length() == 6)
			{
				for(JButton b : buttonsSenha1) {
					b.setEnabled(true);
				}
			}
		}
	}

	private class ConfirmaListener implements ActionListener {

		private JButton btn;

		public ConfirmaListener(JButton button) {
			this.btn = button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			btn.setEnabled(false);
			String fonema = btn.getText();
			String senhaAteEntao = senhaConfirmacao.getText();
			if(senhaAteEntao.isEmpty() || senhaAteEntao.length() == 6 ) {
				senhaConfirmacao.setText(fonema);
			} else {
				senhaConfirmacao.setText(senhaAteEntao.concat(fonema));
			}

			if(senhaConfirmacao.getText().length() == 6)
			{
				for(JButton b : buttonsSenha2) {
					b.setEnabled(true);
				}
			}
		}
	}

	private class AlterarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean sucesso = true;
			dbUtils.connect();
			dbUtils.logMessage(7002, user.getName());
			
			String salt = null;
			String passwd = null;
			String senha1 = senhaFonemas.getText();
			if(senha1.length()!=6) sucesso = false;
			if(senha1.equals(senhaConfirmacao.getText())) {
				//TODO: Appendar SALT e fazer HASH
				salt = String.valueOf((int)( 999999999*Math.random() ));
				String utf8_plainText = senha1 + salt;

				try {
					MessageDigest messageDigest = MessageDigest.getInstance("MD5");
					messageDigest.update(utf8_plainText.getBytes());

					byte[] digest = messageDigest.digest();

					passwd = Conversor.byteArrayToHexString(digest);
				} catch (NoSuchAlgorithmException exception) {
					sucesso = false;
					exception.printStackTrace();
				}
			} else {
				sucesso = false;
			}
			
			String publicKey = "";
			if(file != null) {
				try {
					FileInputStream outStream = new FileInputStream(file);
					BufferedInputStream bufStream = new BufferedInputStream(outStream);
					byte buffer[] = new byte[(int) file.length()];
					bufStream.read(buffer);
					publicKey = Conversor.byteArrayToHexString(buffer);
				} 
				catch (Exception ee) { 
					sucesso = false;
					ee.printStackTrace(); 
				}
			} else {
				sucesso = false;
			}
			
			if(sucesso) {
				User userToUpdate = new User();
				userToUpdate.setName(user.getName());
				userToUpdate.setSALT(salt);
				userToUpdate.setPasswd(passwd);
				userToUpdate.setPublicKey(publicKey);
				
				dbUtils.updateUserPasswdAndPublicKey(userToUpdate);
				dbUtils.incrementNumOfEdits(user.getName());
				
				user.setChangedPasswd(true);
				
				controller.changePanel(new AuthenticatedState3());
			} else {
				JOptionPane.showMessageDialog(null,"Alguns dados não foram inseridos" +
						" corretamente",
						"Erro", JOptionPane.ERROR_MESSAGE);
			}
			
			dbUtils.disconnect();
		}
	}

	private class VoltarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(7003, user.getName());
			dbUtils.disconnect();
			controller.changePanel(new AuthenticatedState3());
		}
	}

}
