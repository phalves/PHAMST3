package view.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import model.User;
import model.authentication.PasswordTree;
import view.MainFrame;
import controller.Conversor;
import controller.FileTool;
import controller.MainController;
import controller.authentication.AuthenticationState;
import controller.dbutils.DBUtils;

public class AuthenticatedState2 extends JPanel implements AuthenticationState {

	private JLabel pathLabel;
	private String caminhoArquivo = "";
	private JPasswordField secretText;
	private JLabel warning;

	DBUtils dbUtils = DBUtils.getDBUtilsInstace();
	MainController controller = MainController.getMainControllerInstace();
	User currentUser = controller.getCurrentUser();

	public AuthenticatedState2() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);

		dbUtils.connect();
		dbUtils.logMessage(4001, currentUser.getName());
		dbUtils.disconnect();

		JLabel fileLabel = new JLabel("Selecione o arquivo de chave privada: ");
		fileLabel.setBounds(20, 20, 230, 40);
		this.add(fileLabel);

		pathLabel = new JLabel("Nenhum arquivo selecionado.");
		pathLabel.setBounds(130, 50, 400, 40);
		pathLabel.setFont(new Font("Serif", Font.PLAIN, 12));
		this.add(pathLabel);

		JLabel secretLabel = new JLabel("Frase secreta:");
		secretLabel.setBounds(20, 100, 100, 20);
		this.add(secretLabel);

		secretText = new JPasswordField();
		secretText.setBounds(20, 120, 250, 25);
		this.add(secretText);

		JButton fileButton = new JButton("Selecionar");
		fileButton.setBounds(20, 60, 100, 20);
		fileButton.addActionListener(new FileButtonActionListener());
		this.add(fileButton);

		JButton btnConfirma = new JButton("Confirmar");
		btnConfirma.setBounds(20, 175, 250, 30);
		btnConfirma.addActionListener(new ConfirmButtonActionListener());
		this.add(btnConfirma);

		warning = new JLabel("");
		warning.setBounds(20, 220, 300, 30);
		warning.setFont(new Font("Serif", Font.PLAIN, 15));
		warning.setForeground(Color.RED);
		this.add(warning);
	}

	private class FileButtonActionListener implements ActionListener {
		@Override
		/*
		 * Inicia o atributo da classe do tipo File
		 *  passando o path do arquivo selecionado.
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser arquivo = new JFileChooser();
			arquivo.setDialogTitle("Selecione o arquivo de chave privada");
			int retorno = arquivo.showOpenDialog(null);
			if(retorno == JFileChooser.APPROVE_OPTION){
				caminhoArquivo = arquivo.getSelectedFile().getAbsolutePath();
				pathLabel.setText(caminhoArquivo);
			}
		}
	}

	private class ConfirmButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// UNCOMMENT TO GENERATE PUB/PRV KEY
			/*
			try {
					KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
					keyPairGenerator.initialize(1024);
					KeyPair keyPair = keyPairGenerator.generateKeyPair();

					System.out.println(Conversor.byteArrayToHexString(keyPair.getPublic().getEncoded()));

					byte[] test = "lhlhlh".getBytes("UTF8");					
					SecureRandom secureRandom = new SecureRandom(test);

					KeyGenerator keyGen = KeyGenerator.getInstance("DES");
					keyGen.init(56, secureRandom);
					Key key = keyGen.generateKey();

					Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.ENCRYPT_MODE, key);

					byte[] cipherText = cipher.doFinal(keyPair.getPrivate().getEncoded());

					FileOutputStream fos = new FileOutputStream("C:\\Users\\Paulo\\workspace\\INF1416-G7-SecurityMaster\\LHTC.pvtkey");
					fos.write(cipherText, 0, cipherText.length);
					fos.flush();
					fos.close();

				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchPaddingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidKeyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalBlockSizeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BadPaddingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				*/
			 

			// Reads binary file containing privateKey data - GOAL: get pvtKey bytes into memory
			if(!caminhoArquivo.equals("")) {
				byte[] pvtKeyEncryptedBytes = FileTool.readBytesFromFile(caminhoArquivo);
				String passphrase = new String(secretText.getPassword());

				try {
					byte[] secureRandomSeed = passphrase.getBytes("UTF8");

					SecureRandom secureRandom = new SecureRandom(secureRandomSeed);

					KeyGenerator keyGen = KeyGenerator.getInstance("DES");
					keyGen.init(56, secureRandom);
					Key key = keyGen.generateKey();

					Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, key);

					byte[] pvtKeyBytes = cipher.doFinal(pvtKeyEncryptedBytes);

					// Reads hexString of publicKey from DB - GOAL: get pblKey bytes into memory
					dbUtils.connect();
					String hexPublicKeyString = dbUtils.selectPublicKey(currentUser.getName());
					dbUtils.disconnect();

					byte[] pblKeyBytes = Conversor.hexStringToByteArray(hexPublicKeyString);

					// Generates array of 512 random bytes
					byte[] randomBytes = new byte[512];
					new Random().nextBytes(randomBytes);

					// Calls the Spec classes to create the keys' specs			    
					PKCS8EncodedKeySpec pvtKeySpec = new PKCS8EncodedKeySpec(pvtKeyBytes);
					X509EncodedKeySpec pblKeySpec = new X509EncodedKeySpec(pblKeyBytes);

					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					PrivateKey pvtKey = keyFactory.generatePrivate(pvtKeySpec);
					PublicKey pblKey = keyFactory.generatePublic(pblKeySpec);

					currentUser.setPrivateKey(pvtKey);

					// Sign the array of random bytes
					Signature signature = Signature.getInstance("MD5WithRSA");
					signature.initSign(pvtKey);
					signature.update(randomBytes);
					byte[] signedBytes = signature.sign();

					// Verify the signature
					signature.initVerify(pblKey);
					signature.update(randomBytes);

					controller = MainController.getMainControllerInstace();
					String userName = currentUser.getName();
					dbUtils.connect();
					if (signature.verify(signedBytes)) {
						dbUtils.logMessage(4003, userName);
						dbUtils.logMessage(4002, userName);
						dbUtils.setNumberOfAttempts(Integer.valueOf(0), userName, DBUtils.STATE_2);
						dbUtils.incrementAccess(userName);
						dbUtils.disconnect();
						controller = MainController.getMainControllerInstace();
						controller.setContentPane("next");

					} else {
						int numberOfAttempts = dbUtils.getNumberOfAttempts(userName, DBUtils.STATE_2);
						numberOfAttempts++;

						if(numberOfAttempts == 1) dbUtils.logMessage(4004, userName);
						else if(numberOfAttempts == 2) dbUtils.logMessage(4005, userName);
						else if(numberOfAttempts == 3) dbUtils.logMessage(4006, userName);

						if(numberOfAttempts < 3) { //Se ainda não errou pela 3a vez
							dbUtils.setNumberOfAttempts(numberOfAttempts, userName, DBUtils.STATE_2);
							warning.setText("Senha errada. Número de tentativas: " + numberOfAttempts);
						} else {
							dbUtils.logMessage(4007, userName);
							dbUtils.logMessage(4002, userName);
							dbUtils.setNumberOfAttempts(0, userName, DBUtils.STATE_2);
							dbUtils.blockUser(userName);
							controller.restartAuthenticationContext();
						}
					}
					dbUtils.disconnect();

				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (InvalidKeySpecException e1) {
					e1.printStackTrace();
				} catch (InvalidKeyException e1) {
					e1.printStackTrace();
				} catch (SignatureException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
				} catch (NoSuchPaddingException e1) {
					e1.printStackTrace();
				} catch (IllegalBlockSizeException e1) {
					e1.printStackTrace();
				} catch (BadPaddingException e1) {
					MainController controller = MainController.getMainControllerInstace();
					User currentUser = controller.getCurrentUser();
					String userName = currentUser.getName();
					dbUtils.connect();
					int numberOfAttempts = dbUtils.getNumberOfAttempts(userName, DBUtils.STATE_2);
					numberOfAttempts++;

					if(numberOfAttempts == 1) dbUtils.logMessage(4004, userName);
					else if(numberOfAttempts == 2) dbUtils.logMessage(4005, userName);
					else if(numberOfAttempts == 3) dbUtils.logMessage(4006, userName);

					if(numberOfAttempts < 3) { //Se ainda não errou pela 3a vez
						dbUtils.setNumberOfAttempts(numberOfAttempts, userName, DBUtils.STATE_2);
						warning.setText("Senha errada. Número de tentativas: " + numberOfAttempts);
					} else {
						dbUtils.logMessage(4007, userName);
						dbUtils.logMessage(4002, userName);
						dbUtils.setNumberOfAttempts(0, userName, DBUtils.STATE_2);
						dbUtils.blockUser(userName);
						controller.restartAuthenticationContext();
					}
				}					
			}
		}
	}
}
