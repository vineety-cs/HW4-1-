package application;
import java.util.regex.Pattern;

public class EmailParser {
	
	public static String checkForValidEmail(String input) {
		
	    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	    Pattern p = Pattern.compile(emailRegex);
	    
		if(input.length() <= 0) {
			return "*** Error *** The email address is empty!";
		}
		else if (!p.matcher(input).matches()) {
			return "*** Error *** Not a valid email address!";
		}
		
		return "";
	}
}

