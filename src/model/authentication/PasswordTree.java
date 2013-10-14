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
	
	public void appendChildren(String[] numbers) {
		
		if (children == null) {
			children = new PasswordTree[4];
			
			for (int i = 0; i < 2; i++) {
				children[i] = new PasswordTree(numbers[i]);
			}
		} else {
			for (int i = 0; i < 2; i++) {
				children[i].appendChildren(numbers);
			}
		}
	}
}
