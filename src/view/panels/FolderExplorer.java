package view.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.FileEntry;
import model.User;
import view.MainFrame;
import controller.FileTool;
import controller.MainController;
import controller.dbutils.DBUtils;

public class FolderExplorer extends JPanel {

	private JLabel pathLabel;
	private String caminhoPasta;
	private JScrollPane filePane;

	DBUtils dbUtils = DBUtils.getDBUtilsInstace();	
	MainController controller = MainController.getMainControllerInstace();
	User user = controller.getCurrentUser();

	JLabel msgSaida = new JLabel("Pressione o botão Sair para confirmar.");

	public FolderExplorer() {
		super(null);
		this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		dbUtils.connect();
		dbUtils.logMessage(8001, user.getName());

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
		//Total de consultas do usuário:
		JLabel numberQueries = new JLabel("Total de consultas do usuário: " +
				dbUtils.getNumOfQueries(user.getName()));
		numberQueries.setBounds(20, 90, 300, 30);
		this.add(numberQueries);

		//Corpo 2
		JLabel fileLabel = new JLabel("Selecione a pasta com os arquivos secretos: ");
		fileLabel.setBounds(20, 130, 400, 40);
		this.add(fileLabel);

		pathLabel = new JLabel("Nenhuma pasta selecionada.");
		pathLabel.setBounds(130, 155, 400, 40);
		pathLabel.setFont(new Font("Serif", Font.PLAIN, 12));
		this.add(pathLabel);

		JButton fileButton = new JButton("Listar");
		fileButton.setBounds(20, 165, 100, 20);
		fileButton.addActionListener(new FileButtonActionListener());
		this.add(fileButton);

		filePane = new JScrollPane();
		filePane.setBounds(20, 190, 750, 300);
		filePane.setEnabled(false);
		this.add(filePane);


		JButton voltarBtn = new JButton("Voltar");
		voltarBtn.setBounds(20, 500, 100, 30);
		voltarBtn.addActionListener(new VoltarListener());
		this.add(voltarBtn);

		dbUtils.disconnect();
		this.setVisible(true);
	}

	private class VoltarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dbUtils.connect();
			dbUtils.logMessage(8002, user.getName());
			dbUtils.disconnect();
			controller.changePanel(new AuthenticatedState3());
		}
	}

	private class FileButtonActionListener implements ActionListener {
		@Override
		/*
		 * Inicia o atributo da classe do tipo File
		 *  passando o path do arquivo selecionado.
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser arquivo = new JFileChooser();
			arquivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			arquivo.setDialogTitle("Selecione o diretório com os arquivos secretos");
			int retorno = arquivo.showOpenDialog(null);
			if(retorno == JFileChooser.APPROVE_OPTION){
				dbUtils.connect();
				dbUtils.incrementNumOfQueries(user.getName());
				dbUtils.disconnect();
				caminhoPasta = arquivo.getSelectedFile().getAbsolutePath();
				pathLabel.setText(caminhoPasta);

				File encryptedIndex = new File(caminhoPasta, "index.enc");
				File digitalEnvelopeIndex = new File(caminhoPasta, "index.env");
				File digitalSignatureIndex = new File(caminhoPasta, "index.asd");

				List<FileEntry> fileList = new ArrayList<FileEntry>();
				boolean flag = true;
				if (encryptedIndex.exists() && digitalEnvelopeIndex.exists() && digitalSignatureIndex.exists()) {

					byte[] encryptedIndexBytes = FileTool.readBytesFromFile(encryptedIndex.getAbsolutePath());
					byte[] envelopeBytes = FileTool.readBytesFromFile(digitalEnvelopeIndex.getAbsolutePath());
					byte[] digitalSignatureBytes = FileTool.readBytesFromFile(digitalSignatureIndex.getAbsolutePath());		

					

					try {
						// recupera chave simétrica contida em index.env												
						Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
						cipher.init(Cipher.DECRYPT_MODE, user.getPrivateKey());
						byte[] newPlainText = cipher.doFinal(envelopeBytes);

						KeyGenerator keyGen = KeyGenerator.getInstance("DES");
						keyGen.init(56, new SecureRandom(newPlainText));
						Key encryptedIndexKey = keyGen.generateKey();

						// utiliza a chave recuperada para acessar o arquivo index.enc
						cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
						cipher.init(Cipher.DECRYPT_MODE, encryptedIndexKey);
						byte[] IndexBytes = cipher.doFinal(encryptedIndexBytes);

						//DEBUG
						//System.out.println(new String(IndexBytes, "UTF8"));

						// verifica assinatura digital
						Signature signature = Signature.getInstance("MD5WithRSA");
						signature.initSign(user.getPrivateKey());
						signature.update(IndexBytes);

						byte[] signedBytes = signature.sign();

						if (signedBytes.length == digitalSignatureBytes.length) {
							boolean authenticIndex = true;

							for (int i = 0; i < signedBytes.length; i++) {
								if (signedBytes[i] != digitalSignatureBytes[i]) {
									authenticIndex = false;
								}
							}

							//DEBUG
							//System.out.println(authenticIndex ? "Signature Verified" : "Signature not verified");

							if (authenticIndex) {
								try {
									String[] files = new String(IndexBytes, "UTF8").split("\n");

									FileChecker fileChecker = new FileChecker();
									for (String s : files) {
										//DEBUG
										//System.out.println(s);

										String[] fileInfo = s.split(" ");

										FileEntry fileEntry = new FileEntry();
										fileEntry.setSecretName(fileInfo[0]);
										fileEntry.setFileCode(fileInfo[1]);

										String status = fileChecker.checkFile(fileEntry.getFileCode());
										if(status.equals("OK")){
											dbUtils.connect();
											dbUtils.logMessage(8005, user.getName(), fileEntry.getSecretName());
											dbUtils.disconnect();
										} else {
											dbUtils.connect();
											dbUtils.logMessage(8007, user.getName(), fileEntry.getSecretName());
											dbUtils.disconnect();
										}

										fileEntry.setStatus(status);
										fileEntry.setPath(caminhoPasta);

										fileList.add(fileEntry);
									}

								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					} catch (NoSuchAlgorithmException e1) {
						flag = false;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					} catch (NoSuchPaddingException e1) {
						flag = false;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					} catch (InvalidKeyException e1) {
						flag = false;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					} catch (IllegalBlockSizeException e1) {
						flag = false;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					} catch (BadPaddingException e1) {
						flag = false;
						// TODO Auto-generated catch block
						flag = false;
						//e1.printStackTrace();
					} catch (SignatureException e1) {
						flag = false;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}	

				}

				if(flag) {

					String[] columnNames = {"Nome Secreto", "Nome Código", "Status"};

					Object[][] data = new Object[fileList.size()][3];				
					for (int i = 0; i < fileList.size(); i++) {
						String[] row = {fileList.get(i).getSecretName(), fileList.get(i).getFileCode(), fileList.get(i).getStatus()};
						data[i] = row;
					}

					JTable fileTable = new JTable(data, columnNames) {
						public boolean isCellEditable(int row, int column) {
							return false;
						}
					};
					fileTable.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent e) {
							if (e.getClickCount() == 2) {
								JTable target = (JTable)e.getSource();
								int row = target.getSelectedRow();

								String s = (String)target.getModel().getValueAt(row, 2);
								dbUtils.connect();
								dbUtils.logMessage(8003, user.getName(), (String)target.getModel().getValueAt(row, 0));
								dbUtils.disconnect();

								//DEBUG
								//System.out.println(s);

								if (s.equals("OK")) {
									byte[] encryptedIndexBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + (String)target.getModel().getValueAt(row, 1) + ".enc");
									byte[] envelopeBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + (String)target.getModel().getValueAt(row, 1) + ".env");

									try {
										Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
										cipher.init(Cipher.DECRYPT_MODE, user.getPrivateKey());
										byte[] newPlainText = cipher.doFinal(envelopeBytes);

										KeyGenerator keyGen = KeyGenerator.getInstance("DES");
										keyGen.init(56, new SecureRandom(newPlainText));
										Key encryptedIndexKey = keyGen.generateKey();

										cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
										cipher.init(Cipher.DECRYPT_MODE, encryptedIndexKey);
										byte[] IndexBytes = cipher.doFinal(encryptedIndexBytes);

										String originalContent = new String(IndexBytes, "UTF8");

										//target.getModel.... = nome secreto do arquivo
										File output = new File(caminhoPasta + "\\" + (String)target.getModel().getValueAt(row, 0));
										if (!output.exists()) {
											FileOutputStream fos = new FileOutputStream(output);
											BufferedOutputStream bos = new BufferedOutputStream(fos);
											try {
												bos.write(IndexBytes);
											} finally {
												if (bos != null) {
													try {
														bos.flush();
														bos.close();
													} catch (Exception e4) {
														
													}
												}
											}
										}
										dbUtils.connect();
										dbUtils.logMessage(8004, user.getName(), (String)target.getModel().getValueAt(row, 0));
										dbUtils.disconnect();

									} catch (Exception e3) {
										dbUtils.connect();
										dbUtils.logMessage(8006, user.getName(), (String)target.getModel().getValueAt(row, 0));
										dbUtils.disconnect();
										e3.printStackTrace();
									}

									//DEBUG
									//System.out.println(new String(IndexBytes, "UTF8"));
								} else {
									dbUtils.connect();
									dbUtils.logMessage(8006, user.getName(), (String)target.getModel().getValueAt(row, 0));
									dbUtils.disconnect();
								}

							}
						}
					});

					filePane.setEnabled(true);
					filePane.getViewport().setView(fileTable);
				}
			}
		}	
	}

	private class FileChecker {

		private String checkFile(String fileName) {

			byte[] encryptedFileBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + fileName + ".enc");
			byte[] envelopeBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + fileName + ".env");
			byte[] digitalSignatureBytes = FileTool.readBytesFromFile(caminhoPasta + "\\" + fileName + ".asd");

			try {
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.DECRYPT_MODE, user.getPrivateKey());
				byte[] newPlainText = cipher.doFinal(envelopeBytes);

				//newplainText é a seed usada pra gerar chave simetrica no momento da criptografia do .enc
				//gera de novo a chave simetrica
				KeyGenerator keyGen = KeyGenerator.getInstance("DES");
				keyGen.init(56, new SecureRandom(newPlainText));
				Key encryptedFileKey = keyGen.generateKey();

				cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, encryptedFileKey);
				byte[] IndexBytes = cipher.doFinal(encryptedFileBytes);

				//DEBUG
				//System.out.println(new String(IndexBytes, "UTF8"));

				//assinando o conteudo do arquivo .enc ja decriptado
				Signature signature = Signature.getInstance("MD5WithRSA");
				signature.initSign(user.getPrivateKey());
				signature.update(IndexBytes);

				byte[] signedBytes = signature.sign();

				if (signedBytes.length == digitalSignatureBytes.length) {
					boolean authenticFile = true;

					for (int i = 0; i < signedBytes.length; i++) {
						if (signedBytes[i] != digitalSignatureBytes[i]) {
							authenticFile = false;
						}

						return authenticFile ? "OK" : "NOT OK";
					}
				} else {
					return "NOT OK";
				}
			} catch (Exception e) {
				return "NOT OK";
			}
			
			return "NOT OK";
		}
	}
}