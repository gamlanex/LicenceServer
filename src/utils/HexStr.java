package utils;

import java.util.StringTokenizer;

public class HexStr {
	
	private static int	LINE_LEN = 16;
	
	static public TransArr parse(String sIn) {
		TransArr t = new TransArr();
		
		try {			
			String s = sIn.replaceAll("0x", "x");			
			s = s.replaceAll(" ([A-Fa-f0-9]{2})", " x$1");			
			s = s.replace("\n", " ").replace("\r", " ");		
			
			StringTokenizer st = new StringTokenizer(s, " x");			
			while (st.hasMoreElements())
				t.add_1(Short.valueOf(st.nextToken(), 16));			
		}
		catch (Exception e) {
			System.out.println("Exception while parsing !!!");
			t = null;
		}

		return t;		
	}
	
	private static String formatHexBuf(TransArr iM) {
		String mS = "";
		int pos = iM.getPosition();
		iM.seek(0);

		while (iM.getPosition() < iM.length()) {
			String s1 = "", s2 = "";
			short bt;
			int j;

			for (j = 0; j < LINE_LEN && (iM.getPosition() < iM.length()); j++) {
				bt = iM.read_1();
				s1 += String.format("x%02X ", bt, bt);
				s2 += bin2View(bt);
			}

			while (++j <= LINE_LEN)
				s1 += "    ";

			mS += s1;			
			mS += " # " + s2 + "\n";
			
		}

		iM.seek(pos);
		return mS;
	}

	public static String bin2View(short b) {
		return (b >= 0x20 && b <= 0x7E) ? ("" + (char)b) : ".";
	}
	
}
