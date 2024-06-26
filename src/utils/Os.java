package utils;

public class Os {
	
	public static char getOsPathSeparator() {
		
		String osName = System.getProperty("os.name").toLowerCase();
		char c = '/';

		if (osName.contains("windows")) {
			c = '\\';
		} else if (osName.contains("mac")) {
			;
		} else if (osName.contains("linux")) {
			;
		} else if (osName.contains("unix")) {
			;
		} else {
			c = '?';
		}

		return c;
	}

}
