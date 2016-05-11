package pt.upa.ca.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

import javax.jws.WebService;

import sun.misc.BASE64Encoder;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA {

	private static String KEY_PATH = "./src/main/resources/";
	private static String PUB_ENDING = "pub.key";
	private static String PRIV_ENDING = "priv.key";

	public CAImpl() {

		File folder = new File(KEY_PATH);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
				if (name.endsWith(PUB_ENDING)){
					byte[] pubEncoded = null;
					try {
						pubEncoded = readFile(KEY_PATH + name);
					} catch (IOException e) {
						//Everything Will Be Okay..
						System.out.println("\u001B[31;1mReading " + name + " went Wrong... : " + e.getClass() + " : " + e.getMessage()+ "\u001B[0m");
					}
					String pub = bytes2String(pubEncoded);
					int index = name.lastIndexOf(PUB_ENDING);
					String ws_name = name.substring(0,index);
					pubKeys.put(ws_name, pub);
					System.out.println("\u001B[33;1mRead entitity! : " + ws_name + "\u001B[0m");
				}
			} else {	/* IGNORE NOT FILES */ }
		}
	}

	private Map<String, String> pubKeys = new TreeMap<String, String>();
	/*
	 * public String getKeyByName(String name){ for (java.util.Map.Entry<String,
	 * String> entry : pubKeys.entrySet()) { if (Objects.equals(name,
	 * entry.getValue())) { return entry.getKey(); } } return null; }
	 */

	@Override
	public void addEntity(String name) {
		System.out.println("addEntity received: " + name);

		String pubKey = pubKeys.get(name);
		if (pubKey != null) {
			System.out.println("\u001B[35;1mEntity already existed, " +name+ " not created\u001B[0m");
			return;
		}

		String pub = null;
		try {
			pub = write(KEY_PATH + name + PUB_ENDING, KEY_PATH + name + PRIV_ENDING);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pubKeys.put(name, pub);
		System.out.println("\u001B[33;1mCreated: " + name + "\u001B[0m");
	}

	@Override
	public String getPublicKey(String name) throws EntityNotFoundException {
		System.out.println("\u001B[34mgetPublicKey request received: \u001B[0m" + name);
		String pubKey = pubKeys.get(name);
		if (pubKey != null) {
			return pubKeys.get(name);
		}
		throw new EntityNotFoundException("Entity: " + name);

	}

	public static String write(String publicKeyPath, String privateKeyPath) throws Exception {

		// generate RSA key pair

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();

		// System.out.println("Writing public key to " + publicKeyPath + "
		// ...");
		byte[] pubEncoded = key.getPublic().getEncoded();
		writeFile(publicKeyPath, pubEncoded);

		System.out.println("Writing private key to '" + privateKeyPath + "' ...");
		byte[] privEncoded = key.getPrivate().getEncoded();
		writeFile(privateKeyPath, privEncoded);
		System.out.println("Done with the keys");
		return bytes2String(pubEncoded);
	}

	private static void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}

	private static String bytes2String(byte[] message) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(message);
	}

	private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}

}
