package pt.upa.handlers; //package pt.upa.handlers;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;

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
import static javax.xml.bind.DatatypeConverter.printHexBinary;



public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = "urn:example";
	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:example";
	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound) {
			try{
				signMessage(smc);
			}catch (NoSuchAlgorithmException e) {
				System.out.printf("\u001B[31mFailed to cipher body because: %s%n\u001B[0m\n", e);
				return false;
			}catch (IOException e){
				System.out.printf("\u001B[31mFailed to cipher body because: %s%n\u001B[0m\n", e);
				return false;
			}
		} else {
			try{
				validateSignature(smc);
			}catch (NoSuchAlgorithmException e) {
				System.out.printf("\u001B[31mFailed to validate message because: %s%n\u001B[0m\n", e);
				return false;
			}
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("\u001B[31mHandle Fault: TODO\u001B[0m");
		//        logToSystemOut(smc);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {}

	private byte[] hash(String text) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//		System.out.println(messageDigest.getProvider().getInfo());
		final byte[] plainBytes = text.getBytes();

//		System.out.println("Computing digest ...");
		messageDigest.update(plainBytes);
		byte[] digest = messageDigest.digest();
		
		return digest;

	}
	
	private static String readFile(String path, Charset encoding) throws IOException {
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
	}

	private void signMessage(SOAPMessageContext smc) throws IOException, NoSuchAlgorithmException {
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

			//Aply Digest Function
			byte[] digestedBody = hash(sBody.getTextContent());
//			byte[] newValue  = hash("que texto grande, sera que vai funcionar assim?=");
//			byte[] newValue2 = hash("que texto grande, sera que vai funcionar assim?=");			
//			System.out.printf("\ncorrect Hash:" + printHexBinary(newValue)+ "\ngot         :" + printHexBinary(newValue2) + "\n");
//			System.out.println( (printHexBinary(newValue).equals(printHexBinary(newValue2))) ? "Great Sucees" : "Failed");
			
			//Sign with private Key
			String privateKey = readFile("./src/main/resources/private.key",StandardCharsets.UTF_8);
			

			
			// add value to header element 
			element.setTextContent(printHexBinary(digestedBody));
			//Update Envelope
			msg.saveChanges();
		} catch (SOAPException e) {
			System.out.printf("\u001B[31m\nFailed to add SOAP header because: %s%n\n\u001B[0m", e);
			return;
		} 
	}

	private void validateSignature(SOAPMessageContext smc) throws NoSuchAlgorithmException{
		System.out.printf("\u001B[33;1m---Validating Message: \u001B[0m");
		try {
			// get SOAP envelope header
			SOAPMessage msg = smc.getMessage();
			SOAPPart sPart = msg.getSOAPPart();
			SOAPEnvelope sEnvelope = sPart.getEnvelope();
			SOAPBody sBody = sEnvelope.getBody();
			SOAPHeader sHeader = sEnvelope.getHeader();

			// check header
			if (sHeader == null) {
				System.out.println("Header not found.");
				return ;
			}

			// get first header element
			Name name = sEnvelope.createName(RESPONSE_HEADER, "e", REQUEST_NS);
			Iterator it = sHeader.getChildElements(name);
			// check header element
			if (!it.hasNext()) {
				System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
				return ;
			}
			SOAPElement element = (SOAPElement) it.next();

			String hashedSignedString = element.getValue();
			byte[] digestedBody = hash(sBody.getTextContent());
			//System.out.printf("correct Hash:" + printHexBinary(digestedBody)+ "\ngot         :" + hashedSignedString + "\n");
			if( printHexBinary(digestedBody).equals(hashedSignedString) )
				System.out.println("\u001B[32mMessage Valid\u001B[0m");
			else
				System.out.println("\u001B[31mMessage Not Valid\u001B[0m");
		} catch (SOAPException e) {
			System.out.printf("Failed to get SOAP header because of %s%n\n", e);
		}
	}
}
