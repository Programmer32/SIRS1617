package pt.upa.ca;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CAMain {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws NoSuchAlgorithmException {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		KeyPair keyPair = keyGen.generateKeyPair();

		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		File privateKeyFile = new File("target/private.key");
		File publicKeyFile = new File("target/public.key");

		try {
			OutputStream fos = new FileOutputStream(privateKeyFile);
			fos.write(printHexBinary(privateKey.getEncoded()).getBytes());
			fos.close();
			
			fos = new FileOutputStream(publicKeyFile);
			fos.write(printHexBinary(publicKey.getEncoded()).getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO I'll deal with it later
			e.printStackTrace();
		} catch (IOException e) {
			// TODO I'll deal with it later
			e.printStackTrace();
		}
		System.out.println("done");
	}

}
