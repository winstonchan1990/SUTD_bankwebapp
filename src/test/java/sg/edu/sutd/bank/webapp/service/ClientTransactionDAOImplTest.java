package sg.edu.sutd.bank.webapp.service;

import static org.junit.Assert.*;
import org.junit.Test;

import java.math.BigDecimal;
import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.*;


public class ClientTransactionDAOImplTest {
	
	@Test
	public void positive_value_txn_test() throws ServiceException {
		ClientTransactionDAOImpl clientTxnDAO = new ClientTransactionDAOImpl();
		ClientTransaction clientTxn = new ClientTransaction();
		clientTxn.setAmount(new BigDecimal("10"));
		assert(clientTxnDAO.isPositiveValue(clientTxn));
	}
	
	@Test
	public void negative_value_txn_test() throws ServiceException {
		ClientTransactionDAOImpl clientTxnDAO = new ClientTransactionDAOImpl();
		ClientTransaction clientTxn = new ClientTransaction();
		clientTxn.setAmount(new BigDecimal("-1"));
		assert(!clientTxnDAO.isPositiveValue(clientTxn));
	}
	
	@Test
	public void zero_value_txn_test() throws ServiceException {
		ClientTransactionDAOImpl clientTxnDAO = new ClientTransactionDAOImpl();
		ClientTransaction clientTxn = new ClientTransaction();
		clientTxn.setAmount(new BigDecimal("0"));
		assert(!clientTxnDAO.isPositiveValue(clientTxn));
	}
	
	
}
