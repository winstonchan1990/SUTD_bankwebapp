package sg.edu.sutd.bank.webapp.commons;

import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XSSFinder {
    private static final long serialVersionUID = 1L;

    private XSSFinder() {}

   
    // Check string for presence of the "<script>" tag
    public static String check_string(String input) throws IOException {
    	
    	String processed_input = input;
    	processed_input = Normalizer.normalize(input, Normalizer.Form.NFKC);
    	processed_input = processed_input.replaceAll("[^\\p{ASCII}]", "");
    	
        Pattern pattern = Pattern.compile("<script>");
        Matcher matcher = pattern.matcher(processed_input);
        if(matcher.find()) {
        	throw new IOException("Warning: Potential XSS Attack found!");
        }
        
        return input;
    }






}