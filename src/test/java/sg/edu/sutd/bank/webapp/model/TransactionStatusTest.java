package sg.edu.sutd.bank.webapp.model;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import org.junit.Rule;


public class TransactionStatusTest {

    @Test
    public void of_NULL() {
        TransactionStatus value = TransactionStatus.of(null);
        assertEquals(value, null);
    }

    @Test
    public void of_APPROVED() {
        TransactionStatus value = TransactionStatus.of("APPROVED");
        assertEquals(value, TransactionStatus.APPROVED);
    }

    @Test
    public void of_DECLINED() {
        TransactionStatus value = TransactionStatus.of("DECLINED");
        assertEquals(value, TransactionStatus.DECLINED);
    }

    @Rule public ExpectedException thrown= ExpectedException.none();
    
    @Test
    public void of_Error() {
    	thrown.expect( IllegalArgumentException.class );
        TransactionStatus value = TransactionStatus.of("randomstring");
    }
}

