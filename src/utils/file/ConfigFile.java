package utils.file;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class ConfigFile {	
	String filePath;
	FileWriter fileWriter;
	PrintWriter writer;
	
	public ConfigFile(String filePath) {		
		this.filePath = filePath;
	}
	
	public void writeFile(String[] content) {
		_openFile();		
		for (String line : content) 
			if (line != null)
				writer.println(line);		
		writer.flush();		
		_closeFile();
	}

	public void writeFile(String content) {
		_openFile();
		writer.println(content);
		writer.flush();
		System.out.println("Writting to file name : " + filePath + "  Content: " + content);
		_closeFile();
	}
	
	private void _openFile() {
		try {			
			fileWriter = new FileWriter(this.filePath, true);
			writer = new PrintWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}		
	}
	
	private void _closeFile() {
		try {		
			if (fileWriter != null) {
				fileWriter.close();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
