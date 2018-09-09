package sg.edu.sutd.bank.webapp.commons;
import java.io.IOException;

import org.junit.Test;
import sg.edu.sutd.bank.webapp.commons.XSSFinder;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XSSFinderTest  {
	
	
	@Test
	public void check_string_invalid() {
		String input = "€€€<scri€€€pt>";
		try {	
			XSSFinder.check_string(input);
		} catch(IOException e) {
			assertThat(e.getMessage(),is("Warning: Potential XSS Attack found!"));
		}
	}
	
	@Test
	public void check_string_valid() throws IOException {
		String input = "hello";
		String out = XSSFinder.check_string(input);
		assertThat(out, is(input));
	}
}
