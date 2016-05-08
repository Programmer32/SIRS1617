package pt.upa.ca.ws;

import java.security.PublicKey;

public class CAManager {

	private String _uddiURL;
	private String _wsName;
	private String _wsURL;
	
	private static final String PATH = "src/main/resources/";

	public static final String[] entities = { "broker-ws", "broker-ws-cli", "transporter-ws", "transporter-ws-cli" };

	public CAManager(String uddiURL, String wsName, String wsURL) {
		_uddiURL = uddiURL;
		_wsName = wsName;
		_wsURL = wsURL;
	}

	public PublicKey requestPublicKey(String entity) {
		PublicKey requestedKey = null;
		
		for(String ent : entities) {
			if(ent.equals(entity)){
				try {
					requestedKey = KeyUtils.readPublicKey(PATH + ent + ".public.key");
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
		return requestedKey;
	}

}
