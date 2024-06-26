package utils.dir;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Os;


public class DirLib {	
	
	private int maxNestLevel = 5;
	private int nestLevel;

	public DirLib() {
		nestLevel = 0;
	}
			
	public DirLib(int maxNestLevel) {
		this.maxNestLevel = maxNestLevel;
		nestLevel = 0;
	}
			
	public List<String> getDirByExt(String ext, String path, List<String> listOfNames) {
		if (listOfNames == null) listOfNames = new LinkedList<String>();
		return iterateOverDir(new FileMatchByExt(ext), path, listOfNames);
	}
	
	public List<String> getDirByRegExp(String pattern, String path, List<String> listOfNames) {
		if (listOfNames == null) listOfNames = new LinkedList<String>();		
		return iterateOverDir(new FileMatchByRegExp(pattern), path, listOfNames);
	}

	public List<String> getDir(String path, List<String> listOfNames) {
		if (listOfNames == null) listOfNames = new LinkedList<String>();		
		return iterateOverDir(new FileMatchAllFiles(), path, listOfNames);
	}
	
	private List<String> iterateOverDir(FileChecker fc, String path, List<String> listOfNames) {		
		File[] lf = (new File(path)).listFiles();	
		
		if (lf == null)
			return listOfNames;
		
		if (nestLevel++ > maxNestLevel)
			return listOfNames;
		
	    for (int i = 0; i < lf.length; i++)
	    	if (lf[i].isFile()) {	    		
	    		String name = lf[i].getName();
	    		
	    		if (fc.isMyFile(name)) 
	    		    listOfNames.add(path + name);
	    	} 
	    	else if (lf[i].isDirectory())	    		
	    		iterateOverDir(fc, lf[i].getAbsolutePath(), listOfNames);
	    	
	    nestLevel--;
		return listOfNames;
	}
	
	// match by a regular expression file matcher
	private class FileMatchByRegExp extends FileChecker {
		private Pattern searchPattern = null;
		
		FileMatchByRegExp(String pattern) {
			searchPattern = Pattern.compile(pattern);
		}

		public boolean isMyFile(String fName) {			
			Matcher searchMatcher = searchPattern.matcher(fName);
			return searchMatcher.matches();
		}
	}
	
	// match by an extension file matcher
	private class FileMatchByExt extends FileChecker {
				
		FileMatchByExt(String pattern) {
			this.pattern = pattern;
		}

		public boolean isMyFile(String fName) {
    		int j = fName.lastIndexOf('.');
    		
    		if (j > 0) {
    		    String ext = fName.substring(j+1);
    		    if (ext.toUpperCase().startsWith(pattern))
    		    	return true;
    		}
    		
			return false;
		}
	}
	
	// match all files
	private class FileMatchAllFiles extends FileChecker {				
		public boolean isMyFile(String fName) {
			return true;
		}
	}
	
	
	private abstract class FileChecker {
		protected String pattern;
		public abstract boolean isMyFile(String fName);
	}

	public static String getCurrentPath() {
		String r = "";		
		try {
			r = new File(".").getCanonicalPath();		
			
			// Linux path ends with '/', in Windows there is nothing at the end
			if (r.charAt(r.length() - 1) != '/' && r.charAt(r.length() - 1) != '\\')
				r += Os.getOsPathSeparator();
		}
		catch (IOException e) {
			System.out.println("Can not retrieve the download files path: " + r);
		}
		return r;
	}
		
}
