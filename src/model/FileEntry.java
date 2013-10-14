package model;

public class FileEntry {

	private String secretName;
	private String fileCode;
	private String status;
	private String path;
	
	public String getSecretName() {
		return secretName;
	}
	public void setSecretName(String secretName) {
		this.secretName = secretName;
	}
	public String getFileCode() {
		return fileCode;
	}
	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
