package utils.dir;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSon {
	
	public static String getJsonOnly(String in) {
		String regex = "\\{(.*)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);
        if (matcher.find()) {
            String match = matcher.group(0); 
            return match;
        }
        return null;
	}
}
