package sg.edu.sutd.bank.webapp.model;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import org.junit.Rule;

public class RoleTest {
	@Test
	public void valueOf_client() {
		assertNotNull(Role.valueOf("client"));
	}
	
	@Test
	public void valueOf_staff() {
		assertNotNull(Role.valueOf("staff"));
	}
	
	@Rule public ExpectedException thrown= ExpectedException.none();
    
    @Test
    public void of_Error() {
    	thrown.expect( IllegalArgumentException.class );
        Role value = Role.valueOf("randomstring");
    }
}
