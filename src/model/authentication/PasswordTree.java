package model.authentication;

public class PasswordTree {

	private String phoneme = "";
	private PasswordTree[] children;
	
	public PasswordTree(String phoneme) {
		this.phoneme = phoneme;
	}
	
	public String getPhoneme() {
		return this.phoneme;
	}
	
	public PasswordTree[] getChildren() {
		return this.children;
	}
	
	public void appendChildren(String[] phonemes) {
		
		if (children == null) {
			children = new PasswordTree[4];
			
			for (int i = 0; i < 4; i++) {
				children[i] = new PasswordTree(phonemes[i]);
			}
		} else {
			for (int i = 0; i < 4; i++) {
				children[i].appendChildren(phonemes);
			}
		}
	}
}
