package sg.edu.sutd.bank.webapp.servlet;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.junit.Test;

public class TransactionCodeGeneratorTest {


	@Test
	public void generate_correct_number () { 
		List<String> c1 = TransactionCodeGenerator.generateCodes(10);
		assertTrue(c1.size() == 10);
	}

	@Test
	public void generate_all_unique_codes() {
		List<String> c1 = TransactionCodeGenerator.generateCodes(10);
		Set<String> set = new HashSet<String>();
		for(String val:c1) {
			set.add(val);
		}
		assertEquals(set.size(), 10);
	}

}