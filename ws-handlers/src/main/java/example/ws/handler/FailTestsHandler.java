//package main.java.pt.upa.handlers;  
package example.ws.handler;


import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.upa.ui.Dialog;
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

		try {
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
		} catch (SOAPException e) {
			System.out.println("Unexpected Exception");
			e.printStackTrace();
		}
		return true;
	}
	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {}

	
	private void tamper_changeNounce(SOAPMessageContext smc) throws SOAPException{
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPHeader sHeader = sEnvelope.getHeader();


		Name nameNounce = sEnvelope.createName(NOUNCE_HEADER, NOUNCE_PREFIX, NOUNCE_NAMESPACE);
		SOAPElement elementNounce = getElement(sHeader,nameNounce);
		
		String digestOld = elementNounce.getTextContent();
		String input  = digestOld;
		char c[]      = input.toCharArray();
		if(c[0] < 95)
			c[0] += 1;
		else
			c[0] -= 1;
		String output = new String(c);
		
		elementNounce.setTextContent(output);
		
		msg.saveChanges();		
	}
	private void tamper_changeValidDigestExpiredNounce(SOAPMessageContext smc) throws SOAPException{
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPHeader sHeader = sEnvelope.getHeader();

		Name nameNounce = sEnvelope.createName(NOUNCE_HEADER, NOUNCE_PREFIX, NOUNCE_NAMESPACE);
		SOAPElement elementNounce = getElement(sHeader,nameNounce);
		
		String nounce = elementNounce.getTextContent();
		int separator = nounce.indexOf(NOUNCE_DELIMITER);
		String expireDateStr = nounce.substring(0, separator);
		long  expireDateLong = Long.parseLong(expireDateStr, 10);
		expireDateLong -= (AuthenticationHandler.DIFFERENCE_SECONDS *2) * 1000;
		
		String counter = nounce.substring(separator);
		elementNounce.setTextContent(expireDateLong + counter);
		
		msg.saveChanges();
	}
	private void tamper_changeAuthor(SOAPMessageContext smc) throws SOAPException{
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPHeader sHeader = sEnvelope.getHeader();

		Name nameAuthor = sEnvelope.createName(AUTHOR_HEADER, AUTHOR_PREFIX, AUTHOR_NAMESPACE);
		SOAPElement elementAuthor = getElement(sHeader,nameAuthor);
		
		elementAuthor.setTextContent("UpaTransporter1");
		
		msg.saveChanges();
	}
	private void tamper_changeDigest(SOAPMessageContext smc) throws SOAPException{
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPHeader sHeader = sEnvelope.getHeader();

//		// get Digest element
		Name nameDigest = sEnvelope.createName(DIGEST_HEADER, DIGEST_PREFIX, DIGEST_NAMESPACE);
		SOAPElement elementDigest = getElement(sHeader,nameDigest);
		
		String digestOld = elementDigest.getTextContent();
		String input  = digestOld;
		char c[]      = input.toCharArray();
		if(c[0] < 95)
			c[0] += 1;
		else
			c[0] -= 1;
		String output = new String(c);
		
		elementDigest.setTextContent(output);
		
		msg.saveChanges();
		
	}
	private void tamper_changeBodyContent(SOAPMessageContext smc) throws SOAPException{
		SOAPMessage msg = smc.getMessage();
		SOAPPart sPart = msg.getSOAPPart();
		SOAPEnvelope sEnvelope = sPart.getEnvelope();
		SOAPBody sBody = sEnvelope.getBody();

		String digestOld = sBody.getTextContent();
		String input  = digestOld;
		char c[]      = input.toCharArray();
		if(c[0] < 95)
			c[0] += 1;
		else
			c[0] -= 1;
		String output = new String(c);
		
		sBody.setTextContent(output);
		
		msg.saveChanges();		
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
