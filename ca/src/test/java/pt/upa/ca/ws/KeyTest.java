package pt.upa.ca.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.PublicKey;

import org.junit.Before;
import org.junit.Test;

public class KeyTest {

	private CAManager caManager;

	private PublicKey brokerKey;
	private PublicKey brokerCliKey;
	private PublicKey transporterKey;
	private PublicKey transporterCliKey;

	@Before
	public void setUp() {
		caManager = new CAManager("localhost", "ca", "localhost/ca");
		loadKeys();
	}

	@Test
	public void askBrokerWS() {
		PublicKey actualKey = caManager.requestPublicKey("broker-ws");
		assertEquals(brokerKey, actualKey);
	}

	@Test
	public void askBrokerWSCli() {
		PublicKey actualKey = caManager.requestPublicKey("broker-ws-cli");
		assertEquals(brokerCliKey, actualKey);
	}

	@Test
	public void askTransporterWS() {
		PublicKey actualKey = caManager.requestPublicKey("transporter-ws");
		assertEquals(transporterKey, actualKey);
	}

	@Test
	public void askTransporterWSCli() {
		PublicKey actualKey = caManager.requestPublicKey("transporter-ws-cli");
		assertEquals(transporterCliKey, actualKey);
	}

	private void loadKeys() {
		final String PATH = "src/main/resources/";
		try {
			brokerKey = KeyUtils.readPublicKey(PATH + "broker-ws.public.key");
			brokerCliKey = KeyUtils.readPublicKey(PATH + "broker-ws-cli.public.key");
			transporterKey = KeyUtils.readPublicKey(PATH + "transporter-ws.public.key");
			transporterCliKey = KeyUtils.readPublicKey(PATH + "transporter-ws-cli.public.key");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test setup error");
		}
	}

}
