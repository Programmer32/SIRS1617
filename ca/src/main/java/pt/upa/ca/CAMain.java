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

import pt.upa.ca.ws.CAManager;

public class CAMain {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws NoSuchAlgorithmException {

		new CAManager(uddiURL, wsName, wsURL)
		
	}

}
