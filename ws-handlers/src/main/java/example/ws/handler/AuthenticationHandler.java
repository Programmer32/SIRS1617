//package main.java.pt.upa.handlers;  
package example.ws.handler;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
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

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.cli.CAClient;
import pt.upa.ca.ws.EntityNotFoundException_Exception;
import pt.upa.ui.Dialog;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

	private static long counter = 0;

	public static final String DIGEST_HEADER    = "digested_message";
	public static final String DIGEST_NAMESPACE = "http://ws.transporter.upa.pt/";
	public static final String DIGEST_PREFIX    = "paa";

	public static final String NOUNCE_HEADER    = "nounce_header";
	public static final String NOUNCE_NAMESPACE = DIGEST_NAMESPACE;
	public static final String NOUNCE_PREFIX    = "paa";

	public static final String AUTHOR_HEADER    = "author_header";
	public static final String AUTHOR_NAMESPACE = DIGEST_NAMESPACE;
	public static final String AUTHOR_PREFIX    = "paa";


	public static final String CA_CERTIFICATE_FILE  = "../CA-ws/src/main/resources/ca-certificate.pem.txt"; //TODO
	public static final String KEYSTORE_EXTENSION   = ".jks";
	public static final String KEYSTORE_PASSWORD    = "ins3cur3";
    public static final String KEY_PASSWORD         = "1nsecure";
	
	public static final String CIPHER_MODE = "RSA/ECB/PKCS1Padding";
	public static final String DIGEST_MODE = "SHA-512";
	public static final String RESOURCES = "./src/main/resources/";

	public static final String NOUNCE_DELIMITER = "\\_/|__|\\_/";
	public static final int DIFFERENCE_SECONDS  = 30;



	public static String MESSAGE_AUTHOR;//   = "TransporterClient";
	public static String UDDI_URL   = "http://localhost:9090";  //TODO
	public static String CA_WS_NAME = "CertificateAuthorityWS"; //TODO

	public Set<QName> getHeaders() {
		return null;
	}

	public static void setAuthor(String name){
		/*
		String CA_WS_NAME    = "CertificateAuthorityWS" ;
		String _uddiURL		 = "http://localhost:9090";
		UDDINaming _uddiNaming;
		String endpointAddr = "";

		try {
			_uddiNaming = new UDDINaming(_uddiURL);
			System.out.println("_uddiNaming: " + _uddiNaming);
			endpointAddr = _uddiNaming.lookup(CA_WS_NAME);

			System.out.println("endpointAddr: " + endpointAddr);
			ca_ws = new CAClient(endpointAddr);
			System.out.println("ca_ws: " + ca_ws);
		} catch (Exception e) {
			e.printStackTrace();
		}; 
		ca_ws.addEntity(name);
		Dialog.IO().debug("Register CA","Added new entity: " +name);*/
		AuthenticationHandler.MESSAGE_AUTHOR = name;
	}


	/**
	 * Method good for Transport Client being Used only for testing
	 * @param name
	 */
	public static void setAuthorIfNull(String name){

		String autor = AuthenticationHandler.MESSAGE_AUTHOR;
		if(autor != null){
			return;
		}
		setAuthor(name);

	}

//	AuthenticationHandler.setCA_WS_NAME()
//	AuthenticationHandler.setUDDI_URL()
	public static void setUDDI_URL(String url){
		Dialog.IO().println("Changing handler UDDI URL: " + url); //TODO
		UDDI_URL = url;
	}
	public static void setCA_WS_NAME(String name){
		Dialog.IO().println("Changing handler CA ws name: " + name); //TODO 
		CA_WS_NAME = name;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound) {
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
					throw new RuntimeException("Invalid Signature");
				}
				return valid;
			}catch (Exception e){
				System.out.printf("\u001B[31mFailed to validate message because: %s%n\u001B[0m\n", e);
				throw new RuntimeException("Invalid Signature");
			}
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("\u001B[31mFault Message Received\u001B[0m");
		LoggingHandler.logToSystemERR(smc);
		//        logToSystemOut(smc);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {}


	private static String bytes2String(byte[] message){
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(message);
	}

	private static byte[] string2Bytes(String message) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] publicKeyBytes = decoder.decodeBuffer(message);
		return publicKeyBytes;
	}

	private static byte[] hash(String text) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_MODE);
		final byte[] plainBytes = text.getBytes();

		messageDigest.update(plainBytes);
		byte[] digest = messageDigest.digest();

		return digest;

	}

	private void signMessage(SOAPMessageContext smc) throws Exception{
		try {
			// get SOAP envelope
			SOAPMessage  	   msg = smc.getMessage();
			SOAPPart         sPart = msg.getSOAPPart();
			SOAPEnvelope sEnvelope = sPart.getEnvelope();
			SOAPBody         sBody = sEnvelope.getBody();
			SOAPHeader     sHeader = sEnvelope.getHeader();

			// add header
			if (sHeader == null){
				sHeader = sEnvelope.addHeader();
			}

			// add header element (name, namespace prefix, namespace)
			Name nameDigest = sEnvelope.createName(DIGEST_HEADER, DIGEST_PREFIX, DIGEST_NAMESPACE);
			Name nameNounce = sEnvelope.createName(NOUNCE_HEADER, NOUNCE_PREFIX, NOUNCE_NAMESPACE);
			Name nameAuthor = sEnvelope.createName(AUTHOR_HEADER, AUTHOR_PREFIX, AUTHOR_NAMESPACE);
			SOAPHeaderElement elementNounce = sHeader.addHeaderElement(nameNounce);
			SOAPHeaderElement elementAuthor = sHeader.addHeaderElement(nameAuthor);
			SOAPHeaderElement elementDigest = sHeader.addHeaderElement(nameDigest);

			elementNounce.addTextNode(Instant.now().toEpochMilli() +NOUNCE_DELIMITER+ ++counter);
			Dialog.IO().debug("Signing Message_expiration", Instant.now().toEpochMilli() +NOUNCE_DELIMITER+ counter);
			elementAuthor.addTextNode(MESSAGE_AUTHOR);
			Dialog.IO().debug("Signing Message_Author", MESSAGE_AUTHOR);

			//Sign with private Key


			PrivateKey privKey = getPrivateKeyFromKeystore(RESOURCES + MESSAGE_AUTHOR + KEYSTORE_EXTENSION, KEYSTORE_PASSWORD.toCharArray(),MESSAGE_AUTHOR,KEY_PASSWORD.toCharArray());

			//			PrivateKey privKey = read("./src/main/resources/"+MESSAGE_AUTHOR+"priv.key");
			String message2Digest = sBody.getTextContent() + elementNounce.getTextContent() + elementAuthor.getTextContent();
			byte[] signedBody = makeDigitalSignature(message2Digest, privKey);

			elementDigest.setTextContent(bytes2String(signedBody));
			Dialog.IO().debug("Signing Message_Digest", MESSAGE_AUTHOR);

			//Update Envelope
			msg.saveChanges();
		} catch (SOAPException e) {
			Dialog.IO().error("Failed to add SOAP header because: " + e.getClass() + " " + e.getMessage());
			throw new RuntimeException("Soap Error");
		} 
	}

	/**
	 * Reads a PrivateKey from a key-store
	 *
	 * @return The PrivateKey
	 * @throws Exception
	 */
	private PrivateKey getPrivateKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword, String keyAlias, char[] keyPassword) throws Exception {

		KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);

		return (PrivateKey) keystore.getKey(keyAlias, keyPassword);
	}

	/**
	 * Reads a KeyStore from a file
	 *
	 * @return The read KeyStore
	 * @throws Exception
	 */
	private KeyStore readKeystoreFile(String keyStoreFilePath, char[] keyStorePassword) throws Exception {
		FileInputStream fis;
		try {
			fis = new FileInputStream(keyStoreFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Keystore file <" + keyStoreFilePath + "> not found.");
			return null;
		}
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(fis, keyStorePassword);
		return keystore;
	}

	private boolean validateSignature(SOAPMessageContext smc) throws Exception{
		Dialog.IO().trace("\u001B[33;1m---Validating Message: \u001B[0m");
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPBody sBody = sEnvelope.getBody();
		SOAPHeader sHeader = sEnvelope.getHeader();

		// check header
		if (sHeader == null) {
			Dialog.IO().error("Header not found.");
			throw new RuntimeException("Header not found.");
		}


		// get Digest element
		Name nameDigest = sEnvelope.createName(DIGEST_HEADER, DIGEST_PREFIX, DIGEST_NAMESPACE);
		SOAPElement elementDigest = getElement(sHeader,nameDigest);

		// get Nounce element
		Name nameNounce = sEnvelope.createName(NOUNCE_HEADER, NOUNCE_PREFIX, NOUNCE_NAMESPACE);
		SOAPElement elementNounce = getElement(sHeader,nameNounce);

		Name nameAuthor = sEnvelope.createName(AUTHOR_HEADER, AUTHOR_PREFIX, AUTHOR_NAMESPACE);
		SOAPElement elementAuthor = getElement(sHeader,nameAuthor);

		String author = elementAuthor.getTextContent();
		//Dialog.IO().print(" Author : " + author + " ");
		Dialog.IO().debug(" Validate_Author " , author);

		//Verifying the nounce was issue recently
		String nounce = elementNounce.getTextContent();
		int separator = nounce.indexOf(NOUNCE_DELIMITER);
		String expireDateStr = nounce.substring(0, separator);	
		long  expireDateLong = Long.parseLong(expireDateStr, 10);

		long now = Instant.now().toEpochMilli();
		long difference = now - expireDateLong;
		Dialog.IO().debug("Validate_now" , now+"");
		Dialog.IO().debug("Validate_beg" , expireDateStr);
		Dialog.IO().debug("Validate_dif" , difference+"");

		//if more than 30 seconds had passed
		if(Math.abs(difference) > DIFFERENCE_SECONDS * 1000){
			Dialog.IO().error("Nounce Expirated");
			throw new RuntimeException("Nounce Expirated");
		}

		//Get Public Key
		//		KeyPair keys = read("./src/main/resources/pub.key","./src/main/resources/priv.key");
		//		PublicKey pKey = keys.getPublic();

		UDDINaming _uddiNaming = new UDDINaming(UDDI_URL); 
		String endpointAddr = _uddiNaming.lookup(CA_WS_NAME);
		CAClient ca_ws = new CAClient(endpointAddr);
		String certificateSTR;
		try{
			certificateSTR = ca_ws.getPublicKey(author);
		} catch(EntityNotFoundException_Exception e) {
			throw new RuntimeException("Invalid Author");
		}

		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		InputStream in = new ByteArrayInputStream(string2Bytes(certificateSTR));
		Certificate certificateWS = certFactory.generateCertificate(in);

		//Ask CA message's Author's certificate
		Certificate caCertificate = readCertificateFile(CA_CERTIFICATE_FILE);
		PublicKey caPublicKey = caCertificate.getPublicKey();
		

		//Verify if the received certificate is a valid one!
		if(!verifySignedCertificate(certificateWS, caPublicKey)){
			System.out.println("\u001B[31mInvalid Certificate Received\u001B[0m");
			throw new RuntimeException("Invalid Certificate");
		}
		PublicKey pKey = certificateWS.getPublicKey();

//		byte[] byteKey = string2Bytes(key);
//		X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//
//		PublicKey pKey =  kf.generatePublic(X509publicKey);
		String messageToDigest = sBody.getTextContent() + elementNounce.getTextContent() + elementAuthor.getTextContent();
		return verifyDigitalSignature(elementDigest.getValue(), messageToDigest , pKey);
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
	public static boolean verifyDigitalSignature(String receivedDigest, String receivedMessage, PublicKey pubKey) throws Exception {


		// get an RSA cipher object
		Cipher cipher = Cipher.getInstance(CIPHER_MODE);

		// decrypt the ciphered digest using the public key (HEADER)
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		byte[] signedBytes = string2Bytes(receivedDigest); 
		byte[] decipheredDigest = cipher.doFinal(signedBytes);


		//Digest the received Text
		byte[] digest = hash(receivedMessage);

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


	private SOAPElement getElement(SOAPElement sElement, Name name){
		Iterator<?> itD = sElement.getChildElements(name);
		// check header element
		if (!itD.hasNext()) {
			Dialog.IO().error("Header element "+ name.getLocalName() +" not found.");
			throw new RuntimeException("Header element "+ name.getLocalName() +" not found");
		}
		return (SOAPElement) itD.next();
	}


	public static PrivateKey read(String privateKeyPath) throws Exception {

		//		System.out.println("Reading private key from file " + privateKeyPath + " ...");
		byte[] privEncoded = readFile(privateKeyPath);

		PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
		KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
		PrivateKey priv = keyFacPriv.generatePrivate(privSpec);
		return priv; 
	}	

	private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}


}
