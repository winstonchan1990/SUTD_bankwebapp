package sg.edu.sutd.bank.webapp.commons;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This is a common XssHelper class that scans user inputs for malicious cross-site scripting attacks
//anywhere a user input is used

public class XssHelper {
    private static final long serialVersionUID = 1L;

    private XssHelper() {}

//  Normalises the string by removing non-ASCII characters
//  This helps to prevent injection attacks by limiting the scope of what character is allowed.
    
    public static String input_normalizer(String input) {
        input = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return input.replaceAll("[^\\p{ASCII}]", "");
    }

//    Scans the string for presence of the "<script>" tag
    public static Boolean xss_match(String input) {
        Pattern pattern = Pattern.compile("<script>");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }






}