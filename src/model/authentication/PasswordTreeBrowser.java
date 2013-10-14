package model.authentication;

import java.util.ArrayList;
import java.util.List;


public class PasswordTreeBrowser {

	private List<String> possiblePasswds = new ArrayList<String>();
	
	public List<String> getPossiblePasswds() {
		return this.possiblePasswds;
	}

	// at the first call <param>possiblePasswd should always be "" (Empty String).
	public void browse(String possiblePasswd, PasswordTree passwdTree) {
		
		if (passwdTree.getChildren() == null) {
			possiblePasswds.add(possiblePasswd + passwdTree.getPhoneme());
		} else {
			for (int i = 0; i < 2; i++) {
				browse(possiblePasswd + passwdTree.getPhoneme(), passwdTree.getChildren()[i]);
			}
		}
	}
}
