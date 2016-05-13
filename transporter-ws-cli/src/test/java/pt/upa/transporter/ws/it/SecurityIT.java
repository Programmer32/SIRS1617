package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;

import example.ws.handler.FailTestsHandler;


/**
 * Test suite
 */
public class SecurityIT extends AbstractIT {

	@Test
	public void testValidMessage() throws Exception {
		FailTestsHandler.TEST_MODE = 0;
		assertNotNull(CLIENT.ping("test"));
	}
	
	@Test(expected=RuntimeException.class)
	public void testChangeNounce() throws Exception {
		FailTestsHandler.TEST_MODE = 1;
		CLIENT.ping("test");
	}

	@Test(expected=RuntimeException.class)
	public void testValidDigestExpiredNounce() throws Exception {
		FailTestsHandler.TEST_MODE = 2;
		CLIENT.ping("test");
	}

	@Test(expected=RuntimeException.class)
	public void testChangeAuthor() throws Exception {
		FailTestsHandler.TEST_MODE = 3;
		CLIENT.ping("test");
	}
	
	@Test(expected=RuntimeException.class)
	public void testChangeDigest() throws Exception {
		FailTestsHandler.TEST_MODE = 4;
		CLIENT.ping("test");
	}
	
	@Test(expected=RuntimeException.class)
	public void testChangeBodyContent() throws Exception {
		FailTestsHandler.TEST_MODE = 5;
		CLIENT.ping("test");
		
	}
	
	@After
	public void cleningUp(){
		FailTestsHandler.TEST_MODE = 0;
	} 
	
}
