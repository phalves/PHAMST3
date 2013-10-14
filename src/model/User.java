package model;

import java.security.PrivateKey;

public class User {
	private String name;
	private String nomeProprio;
	private String SALT;
	private String passwd;
	private String role;
	private int access;
	private String publicKey;
	private int queries;
	
	private PrivateKey privateKey;

	private boolean changedPasswd = false;
	
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public int getAccess() {
		return access;
	}
	
	public String getNomeProprio() {
		return nomeProprio;
	}

	public void setNomeProprio(String nomeProprio) {
		this.nomeProprio = nomeProprio;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	private boolean blocked;
	private int numberOfAttemptsToLogin = 0;
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSALT() {
		return this.SALT;
	}
	
	public void setSALT(String SALT) {
		this.SALT = SALT;
	}
	
	public String getPasswd() {
		return this.passwd;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public boolean isBlocked() {
		return this.blocked;
	}
	
	public void setBlocked(boolean isBlocked) {
		this.blocked = isBlocked;
	}
	
	public int getNumberOfAttemptsToLogin() {
		return this.numberOfAttemptsToLogin;
	}
	
	public void increaseNumberOfAttemptsToLogin() {
		numberOfAttemptsToLogin++;
		
		if (numberOfAttemptsToLogin >= 3) {
			blocked = true;
		}
	}

	public boolean changedPasswd() {
		return changedPasswd;
	}

	public void setChangedPasswd(boolean changedPasswd) {
		this.changedPasswd = changedPasswd;
	}

	public int getQueries() {
		return queries;
	}

	public void setQueries(int queries) {
		this.queries = queries;
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
}
