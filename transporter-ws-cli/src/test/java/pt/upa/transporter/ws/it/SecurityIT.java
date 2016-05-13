package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertNotNull;

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
		assertNotNull(CLIENT.ping("test"));
		FailTestsHandler.TEST_MODE = 0;
	}

	@Test(expected=RuntimeException.class)
	public void testValidDigestExpiredNounce() throws Exception {
		FailTestsHandler.TEST_MODE = 2;
		assertNotNull(CLIENT.ping("test"));
		FailTestsHandler.TEST_MODE = 0;
	}

	@Test(expected=RuntimeException.class)
	public void testChangeAuthor() throws Exception {
		FailTestsHandler.TEST_MODE = 3;
		assertNotNull(CLIENT.ping("test"));
		FailTestsHandler.TEST_MODE = 0;
	}
	
	@Test(expected=RuntimeException.class)
	public void testChangeDigest() throws Exception {
		FailTestsHandler.TEST_MODE = 4;
		assertNotNull(CLIENT.ping("test"));
		FailTestsHandler.TEST_MODE = 0;
	}
	
	@Test(expected=RuntimeException.class)
	public void testChangeBodyContent() throws Exception {
		FailTestsHandler.TEST_MODE = 5;
		assertNotNull(CLIENT.ping("test"));
		FailTestsHandler.TEST_MODE = 0;
	}
}
