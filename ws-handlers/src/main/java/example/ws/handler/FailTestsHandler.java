//package main.java.pt.upa.handlers;  
package example.ws.handler;


import static org.junit.Assert.assertNotNull;

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

import org.junit.Test;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.cli.CAClient;
import pt.upa.ca.ws.EntityNotFoundException_Exception;
import pt.upa.ui.Dialog;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class FailTestsHandler implements SOAPHandler<SOAPMessageContext> {

	public static int TEST_MODE = 0;

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

	public static final String NOUNCE_DELIMITER = AuthenticationHandler.NOUNCE_DELIMITER;
	public static final int DIFFERENCE_SECONDS  = 30;

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound) {
			switch (TEST_MODE) {
			case 0:
				
				break;
			case 1:
				tamper_changeNounce(smc);
				break;
			case 2:
				tamper_changeValidDigestExpiredNounce(smc);
				break;
			case 3:
				tamper_changeAuthor(smc);
				break;
			case 4:
				tamper_changeDigest(smc);
				break;
			case 5:
				tamper_changeBodyContent(smc);
				break;

			default:
				break;
			}
		} else {
			//Only trying to tamper with outbound messages
		}
		return true;
	}
	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {}


	private static String bytes2String(byte[] message){
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(message);
	}
	
	private void tamper_changeNounce(SOAPMessageContext smc){
		System.out.println("\u001B[33,1m" + TEST_MODE +"\u001B[0m" );
		
	}
	private void tamper_changeValidDigestExpiredNounce(SOAPMessageContext smc){
		System.out.println("\u001B[33,1m" + TEST_MODE +"\u001B[0m" );
		
	}
	private void tamper_changeAuthor(SOAPMessageContext smc){
		System.out.println("\u001B[33,1m" + TEST_MODE +"\u001B[0m" );
		
	}
	private void tamper_changeDigest(SOAPMessageContext smc){
		System.out.println("\u001B[33,1m" + TEST_MODE +"\u001B[0m" );
		
	}
	private void tamper_changeBodyContent(SOAPMessageContext smc){
		System.out.println("\u001B[33,1m" + TEST_MODE +"\u001B[0m" );
		
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
	
	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
