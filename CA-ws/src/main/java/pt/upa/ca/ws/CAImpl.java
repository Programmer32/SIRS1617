package pt.upa.ca.ws;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Map;
import java.util.TreeMap;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA {
	
	private Map<String,String> pubKeys = new TreeMap<String, String>();
	/*
	public String getKeyByName(String name){
		for (java.util.Map.Entry<String, String> entry : pubKeys.entrySet()) {
	        if (Objects.equals(name, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
		return null;
	}*/
	
	@Override
	public void addEntity(String name) {
		System.out.println("addEntity received: " + name);
		String prefix = "./src/main/resources/" + name;
		String pub = "";
		try {
			pub = write(prefix + "pub.key", prefix + "priv.key");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pubKeys.put(name, pub);
		System.out.println( name + " value: " + pub);
	}

	@Override
	public String getPublicKey(String name) throws EntityNotFoundException {
		System.out.println("getPublicKey received: " + name);
		return pubKeys.get(name);
	}
	
	public static String write(String publicKeyPath, String privateKeyPath) throws Exception {

		// generate RSA key pair
		System.out.println("Generating RSA keys ...");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();

//		System.out.println("Public key info:");
//		System.out.println("algorithm: " + key.getPublic().getAlgorithm());
//		System.out.println("format: " + key.getPublic().getFormat());

		System.out.println("Writing public key to " + publicKeyPath + " ...");
		byte[] pubEncoded = key.getPublic().getEncoded();
		writeFile(publicKeyPath, pubEncoded);
		System.out.println("Done");

//		System.out.println("---");

//		System.out.println("Private key info:");
//		System.out.println("algorithm: " + key.getPrivate().getAlgorithm());
//		System.out.println("format: " + key.getPrivate().getFormat());

		System.out.println("Writing private key to '" + privateKeyPath + "' ...");
		byte[] privEncoded = key.getPrivate().getEncoded();
		writeFile(privateKeyPath, privEncoded);
		System.out.println("Done");
		return printHexBinary(privEncoded);
	}
	private static void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}

}
