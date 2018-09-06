package sutdbank;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.sutd.bank.webapp.service.*;
import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.*;

public class UserDAOImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void TestCreateDup () { 
		
		User user1 = new User();
		user1.setId(12);
		user1.setUserName("client_1");
		user1.setPassword("654321");

		UserDAOImpl user1DAO = new UserDAOImpl();
		try {
			user1DAO.create(user1);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert(false);
		}
	}

}
