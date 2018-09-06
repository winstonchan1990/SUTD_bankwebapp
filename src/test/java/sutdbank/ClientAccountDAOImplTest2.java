package sutdbank;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.sutd.bank.webapp.service.*;
import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.*;

public class ClientAccountDAOImplTest2 {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void TestUpdate () { 
		
		User user1 = new User();
		user1.setId(99);
		
		ClientAccount CA1 = new ClientAccount();		
		CA1.setUser(user1);
		BigDecimal setamt = new BigDecimal("88"); 
		CA1.setAmount(setamt);
		
		ClientAccountDAOImpl CA1DAO = new ClientAccountDAOImpl();
		
		try {
			double amt = CA1DAO.getAccountBalance(CA1);
			System.out.println(amt);
			CA1DAO.update(CA1);
			amt = CA1DAO.getAccountBalance(CA1);
			System.out.println(amt);
			assertTrue(amt==88);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
