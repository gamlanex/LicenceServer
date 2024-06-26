package utils;

import java.util.concurrent.TimeUnit;

public class Pause {
	public static void p(long t) {		
		try {
			TimeUnit.MILLISECONDS.sleep(t);
		}
		catch (InterruptedException e) {
		}
		
	}
}
