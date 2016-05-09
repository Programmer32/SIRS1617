//package main.java.pt.upa.handlers;  
package pt.upa.handlers;


import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;



public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = "urn:example";
	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:example";
	
	
	public static final String CA_CERTIFICATE_FILE = "./src/main/resources/ca.pem";
	public static final String CIPHER_MODE = "RSA/ECB/PKCS1Padding";
	public static final String DIGEST_MODE = "SHA-512";
	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		/*if (outbound) {
			try{
				signMessage(smc);
			}catch (Exception e){
				System.out.printf("\u001B[31mFailed to cipher body because: %s%n\u001B[0m\n", e);
				return false;
			}
		} else {
			try{
				boolean valid = validateSignature(smc); 
				if(valid){
					System.out.println("\u001B[32mMessage Valid\u001B[0m");
				} else {
					System.out.println("\u001B[31mMessage Not Valid\u001B[0m");
				}
				return valid;
			}catch (Exception e){
				System.out.printf("\u001B[31mFailed to validate message because: %s%n\u001B[0m\n", e);
				return false;
			}
		}*/
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		//System.out.println("\u001B[31mHandle Fault: TODO\u001B[0m");
		//        logToSystemOut(smc);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {}

	
	
	
	
	
	
	private static byte[] hash(String text) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_MODE);
//		System.out.println(messageDigest.getProvider().getInfo());
		final byte[] plainBytes = text.getBytes();

//		System.out.println("Computing digest ...");
		messageDigest.update(plainBytes);
		byte[] digest = messageDigest.digest();
		
		return digest;

	}
	

	/** auxiliary method to calculate digest from text and cipher it */
	public static byte[] makeDigitalSignature(String plainText, PrivateKey privKey) throws Exception {

		//Digest the text
		byte[] digest = hash(plainText); 
		
		// get an RSA cipher object
		Cipher cipher = Cipher.getInstance(CIPHER_MODE);

		// encrypt the plaintext using the private key
		cipher.init(Cipher.ENCRYPT_MODE, privKey);
		byte[] cipherDigest = cipher.doFinal(digest);

//		System.out.println("Cipher digest:");
//		System.out.println(printHexBinary(cipherDigest));

		return cipherDigest;
	}

	private void signMessage(SOAPMessageContext smc) throws Exception{
		System.out.printf("\u001B[35mSigning Message\u001B[0m\n");
		try {
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sPart = msg.getSOAPPart();
			SOAPEnvelope sEnvelope = sPart.getEnvelope();
			SOAPBody sBody = sEnvelope.getBody();
			SOAPHeader sHeader = sEnvelope.getHeader();
			
			// add header
			if (sHeader == null){
				sHeader = sEnvelope.addHeader();
			}
			
			// add header element (name, namespace prefix, namespace)
			Name name = sEnvelope.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
			SOAPHeaderElement element = sHeader.addHeaderElement(name);
			
			 //Sign with private Key
			KeyPair keys = read("./src/main/resources/pub.key","./src/main/resources/priv.key");
			byte[] signedBody = makeDigitalSignature(sBody.getTextContent(), keys.getPrivate());
			
			// add value to header element 
			element.setTextContent(printHexBinary(signedBody));
			//Update Envelope
			msg.saveChanges();
		} catch (SOAPException e) {
			System.out.printf("\u001B[31m\nFailed to add SOAP header because: %s%n\n\u001B[0m", e);
			return;
		} 
	}
	
	/**
	 * Verifica se um certificado foi devidamente assinado pela CA
	 * 
	 * @param certificate
	 *            certificado a ser verificado
	 * @param caPublicKey
	 *            certificado da CA
	 * @return true se foi devidamente assinado
	 */
	public static boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
		try {
			certificate.verify(caPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			// O método Certifecate.verify() não retorna qualquer valor (void).
			// Quando um certificado é inválido, isto é, não foi devidamente
			// assinado pela CA
			// é lançada uma excepção: java.security.SignatureException:
			// Signature does not match.
			// também são lançadas excepções caso o certificado esteja num
			// formato incorrecto ou tenha uma
			// chave inválida.

			return false;
		}
		return true;
	}
	/**
	 * auxiliary method to calculate new digest from text and compare it to the
	 * to deciphered digest
	 */
	public static boolean verifyDigitalSignature(String receivedHeader, String receivedBody, PublicKey pubKey) throws Exception {

		
		// get an RSA cipher object
		Cipher cipher = Cipher.getInstance(CIPHER_MODE);

		// decrypt the ciphered digest using the public key (HEADER)
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		byte[] signedBytes = DatatypeConverter.parseHexBinary(receivedHeader); 
		byte[] decipheredDigest = cipher.doFinal(signedBytes);

		
		
		
		
		//Digest the received Text
		byte[] digest = hash(receivedBody);

		// compare digests
		if (digest.length != decipheredDigest.length)
			return false;

		for (int i = 0; i < digest.length; i++)
			if (digest[i] != decipheredDigest[i])
				return false;
		return true;
	}
	/**
	 * Reads a certificate from a file
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Certificate readCertificateFile(String certificateFilePath) throws Exception {
		FileInputStream fis;

		try {
			fis = new FileInputStream(certificateFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Certificate file <" + certificateFilePath + "> not found.");
			return null;
		}
		BufferedInputStream bis = new BufferedInputStream(fis);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		if (bis.available() > 0) {
			Certificate cert = cf.generateCertificate(bis);
			return cert;
			// It is possible to print the content of the certificate file:
			// System.out.println(cert.toString());
		}
		bis.close();
		fis.close();
		return null;
	}
	
	
	private boolean validateSignature(SOAPMessageContext smc) throws Exception{
		System.out.printf("\u001B[33;1m---Validating Message: \u001B[0m");
		// get SOAP envelope header
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPBody sBody = sEnvelope.getBody();
		SOAPHeader sHeader = sEnvelope.getHeader();

		// check header
		if (sHeader == null) {
			System.out.println("Header not found.");
			return false;
		}

		// get first header element
		Name name = sEnvelope.createName(RESPONSE_HEADER, "e", REQUEST_NS);
		Iterator<?> it = sHeader.getChildElements(name);
		// check header element
		if (!it.hasNext()) {
			System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
			return false;
		}
		SOAPElement headerElement = (SOAPElement) it.next();
		
		//Get Public Key
		KeyPair keys = read("./src/main/resources/pub.key","./src/main/resources/priv.key");
		PublicKey pKey = keys.getPublic();
		
		//Ask CA message's Author's certificate
//		Certificate certificate = readCertificateFile(CERTIFICATE_FILE);
////		certificate = CA.getCertificate(String name); TODO
//		Certificate caCertificate = readCertificateFile(CA_CERTIFICATE_FILE);
//		PublicKey caPublicKey = caCertificate.getPublicKey();

		//Verify if the received certificate is a valid one!
//		if(!verifySignedCertificate(certificate, caPublicKey)){
//			System.out.println("\u001B[31mInvalid Certificate Received\u001B[0m");
//			return false;
//		}
// 		PublicKey pKey = certificate.getPublicKey();
		
		return verifyDigitalSignature(headerElement.getValue(), sBody.getTextContent(), pKey);
	}

	public static KeyPair read(String publicKeyPath, String privateKeyPath) throws Exception {

//		System.out.println("Reading public key from file " + publicKeyPath + " ...");
		byte[] pubEncoded = readFile(publicKeyPath);

		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
		KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
		PublicKey pub = keyFacPub.generatePublic(pubSpec);
//		System.out.println(pub);

//		System.out.println("---");

//		System.out.println("Reading private key from file " + privateKeyPath + " ...");
		byte[] privEncoded = readFile(privateKeyPath);

		PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
		KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
		PrivateKey priv = keyFacPriv.generatePrivate(privSpec);

//		System.out.println(priv);
//		System.out.println("---");

		KeyPair keys = new KeyPair(pub, priv);
		return keys;
	}	
	
	private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}
	
}
