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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.User;
import view.MainFrame;
import controller.Conversor;
import controller.MainController;
import controller.dbutils.DBUtils;

public class Register extends JPanel {

	DBUtils dbUtils = DBUtils.getDBUtilsInstace();	
	ArrayList<String> fonemas = new ArrayList<String>();
	ArrayList<JButton> buttonsSenha1 = new ArrayList<JButton>();
	ArrayList<JButton> buttonsSenha2 = new ArrayList<JButton>();
	JPasswordField senhaFonemas;
	JPasswordField senhaConfirmacao;
	private JLabel pathLabel;
	private String caminhoArquivo = "";
	File file;

	JTextField nomeProprioTextField;
	JTextField loginTextField;
	JComboBox<String> grupoComboBox;

	MainController controller = MainController.getMainControllerInstace();
	User user = controller.getCurrentUser();

	public Register() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);

		dbUtils.connect();
		dbUtils.logMessage(6001, user.getName());
		
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
		JLabel numberUsers = new JLabel("Total de usuários do sistema: " +
				dbUtils.totalUsers());
		numberUsers.setBounds(20, 90, 300, 30);
		this.add(numberUsers);

		//Corpo 2
		JLabel formulario = new JLabel("Formulário de Cadastro:");
		formulario.setBounds(20, 130, 350, 40);
		this.add(formulario);

		JLabel nomeProprioLabel = new JLabel("Nome do usuário: ");
		nomeProprioLabel.setBounds(20, 180, 200, 30);
		this.add(nomeProprioLabel);

		nomeProprioTextField = new JTextField();
		nomeProprioTextField.setBounds(240, 180, 200, 30);
		this.add(nomeProprioTextField);

		JLabel loginLabel = new JLabel("Login name: ");
		loginLabel.setBounds(20, 210, 200, 30);
		this.add(loginLabel);

		loginTextField = new JTextField();
		loginTextField.setBounds(240, 210, 200, 30);
		this.add(loginTextField);

		JLabel grupoLabel = new JLabel("Grupo: ");
		grupoLabel.setBounds(20, 240, 200, 30);
		this.add(grupoLabel);

		String[] roles = { "Administrador", "Usuario" };
		grupoComboBox = new JComboBox<String>(roles);
		grupoComboBox.setBounds(240, 240, 200, 30);
		this.add(grupoComboBox);

		JLabel senhaFonemasLabel = new JLabel("Senha pessoal:");
		senhaFonemasLabel.setBounds(20, 270, 200, 30);
		this.add(senhaFonemasLabel);

		senhaFonemas = new JPasswordField();
		senhaFonemas.setBounds(240, 270, 200, 30);
		senhaFonemas.setEnabled(false);
		this.add(senhaFonemas);

		//Inicia vetor de fonemas
		ArrayList<String> fonemas = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("src/fonemas.txt"));
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
					fonemaBtn.setBounds(450 + x*25, 270, 25, 15);
					fonemaBtn.setFont(new Font("Arial",Font.BOLD,8));
					fonemaBtn.setMargin(new java.awt.Insets(1, 2, 1, 2));
				} else {
					fonemaBtn.setBounds(450 + y*25, 285, 25, 15);
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
		senhaConfirmacaoLabel.setBounds(20, 310, 200, 30);
		this.add(senhaConfirmacaoLabel);

		senhaConfirmacao = new JPasswordField();
		senhaConfirmacao.setBounds(240, 310, 200, 30);
		senhaConfirmacao.setEnabled(false);
		this.add(senhaConfirmacao);

		int x = 0;
		int y = 0;
		for(String f : fonemas) {
			JButton confirmBtn = new JButton(f);
			if( x < 12 ) {
				confirmBtn.setBounds(450 + x*25, 310, 25, 15);
				confirmBtn.setFont(new Font("Arial",Font.BOLD,8));
				confirmBtn.setMargin(new java.awt.Insets(1, 2, 1, 2));
			} else {
				confirmBtn.setBounds(450 + y*25, 325, 25, 15);
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
		caminhoPublicKeyLabel.setBounds(20, 350, 200, 30);
		this.add(caminhoPublicKeyLabel);

		JButton fileButton = new JButton("Selecionar");
		fileButton.setBounds(240, 350, 100, 20);
		fileButton.addActionListener(new FileButtonActionListener());
		this.add(fileButton);

		pathLabel = new JLabel("Nenhum arquivo selecionado.");
		pathLabel.setBounds(350, 340, 400, 40);
		pathLabel.setFont(new Font("Serif", Font.PLAIN, 12));
		this.add(pathLabel);

		JButton cadastrarBtn = new JButton("Cadastrar");
		cadastrarBtn.setBounds(20, 400, 100, 30);
		cadastrarBtn.addActionListener(new CadastrarListener());
		this.add(cadastrarBtn);

		JButton voltarBtn = new JButton("Voltar");
		voltarBtn.setBounds(130, 400, 100, 30);
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

	private class CadastrarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean sucesso = true;
			dbUtils.connect();
			dbUtils.logMessage(6002, user.getName());
			
			String nome = nomeProprioTextField.getText();
			if(nome != null){
				if(nome.isEmpty() || nome.length() > 50) {
					sucesso = false;
				}
			}
			else {
				sucesso = false;
			}

			String login = loginTextField.getText();
			if(login != null){
				if(login.isEmpty() || login.length() > 20) {
					sucesso = false;
				} else {
					String userLogin = dbUtils.selectName(login);
					if(!userLogin.isEmpty()) {
						sucesso = false;
					}
				}
			} else {
				sucesso = false;
			}

			int role_Id = grupoComboBox.getSelectedIndex() + 1;
			
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
				User userToSave = new User();
				
				userToSave.setNomeProprio(nome);
				userToSave.setName(login);
				userToSave.setPasswd(passwd);
				userToSave.setSALT(salt);
				userToSave.setPublicKey(publicKey);
				
				dbUtils.saveUser(userToSave, role_Id );
				
				nomeProprioTextField.setText("");
				loginTextField.setText("");
				senhaFonemas.setText("");
				senhaConfirmacao.setText("");
				pathLabel.setText("Nenhum arquivo selecionado.");
				file = null;
				JOptionPane.showMessageDialog(null,"Usuário salvo com sucesso",
						"Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
			dbUtils.logMessage(6003, user.getName());
			dbUtils.disconnect();
			controller.changePanel(new AuthenticatedState3());
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
}