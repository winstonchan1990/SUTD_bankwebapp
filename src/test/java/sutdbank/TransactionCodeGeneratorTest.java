package sutdbank;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.sutd.bank.webapp.servlet.*;

public class TransactionCodeGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void TestLen1 () { 
		List<String> c1 = TransactionCodeGenerator.generateCodes(10);
		assertTrue(c1.size() == 10);
	}

	@Test
	public void TestLen2 () { 
		List<String> c1 = TransactionCodeGenerator.generateCodes(0);
		assertTrue(c1.size() == 0);
	}

	@Test
	public void TestLen3 () { 
		List<String> c1 = TransactionCodeGenerator.generateCodes(-1);
		System.out.println(c1.size());
		assertTrue(c1.size() == 0);
	}

}
