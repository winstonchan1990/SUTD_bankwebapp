package sutdbank;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.sutd.bank.webapp.service.*;
import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.*;

public class ClientAccountDAOImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void TestCreate1 () { 
		
		User user1 = new User();
		user1.setId(12);
		user1.setUserName("client_1");
		
		ClientAccount CA1 = new ClientAccount();		
		CA1.setUser(user1);
		BigDecimal setamt = new BigDecimal("99"); 
		CA1.setAmount(setamt);
		
		ClientAccountDAOImpl CA1DAO = new ClientAccountDAOImpl();
		
		try {
			double amt = CA1DAO.getAccountBalance(CA1);
			System.out.println(amt);
			CA1DAO.create(CA1);
			amt = CA1DAO.getAccountBalance(CA1);
			System.out.println(amt);
			
			assert(setamt.doubleValue() == amt);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestCreate2 () { 
		
		User user1 = new User();
		user1.setId(12);
		user1.setUserName("client_1");
		
		ClientAccount CA1 = new ClientAccount();		
		CA1.setUser(user1);
		BigDecimal setamt = new BigDecimal("199"); 
		CA1.setAmount(setamt);
		
		ClientAccountDAOImpl CA1DAO = new ClientAccountDAOImpl();
		
		try {
			double amt = CA1DAO.getAccountBalance(CA1);
			System.out.println(amt);
			CA1DAO.create(CA1);
			amt = CA1DAO.getAccountBalance(CA1);
			System.out.println(amt);
			assert(setamt.doubleValue() == amt);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
