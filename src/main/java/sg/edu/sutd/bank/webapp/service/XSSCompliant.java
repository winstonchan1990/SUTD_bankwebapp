package sg.edu.sutd.bank.webapp.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class XSSCompliant {
	private static final XSSCompliant inst = new XSSCompliant();
	
	private XSSCompliant() {
		
	}
	
	public static XSSCompliant getInstance() {
		return inst;
	}
	
	public String convertXSSComplianceString(String input) throws IOException {
		// Write string if it does not contain <script>
		if(input.indexOf("<script>") >= 0) {
			throw new IOException("Potential XSS Attack Detected");
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		outStream.write(input.getBytes());
		outStream.flush();
		
		String output = new String(outStream.toByteArray());
		
		return output;
	}
}
