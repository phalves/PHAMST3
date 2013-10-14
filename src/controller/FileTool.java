package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileTool {

	public static byte[] readBytesFromFile(String pathToFile) {
		File file = new File(pathToFile);
		byte[] bytes = new byte[(int) file.length()];
		try {
			
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytes, 0, bytes.length);
			fileInputStream.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return bytes;
	}
	
}
