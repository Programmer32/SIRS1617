package pt.upa.handlers; //package pt.upa.handlers;


import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;



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
			signMessage(smc);
		} else {
			validateSignature(smc);
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("\u001B[31mHandle Fault: TODO\u001B[0m");
		//        logToSystemOut(smc);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {
	}

	private void signMessage(SOAPMessageContext smc){
		System.out.println("\u001B[35mSigning Message:\u001B[0m");
		try {
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();

			// add header
			SOAPHeader sh = se.getHeader();
			if (sh == null){
				sh = se.addHeader();
			}
			
	/*		
			QName qNameUserCredentials = new QName("https://your.target.namespace/", "UserCredentials");
            SOAPHeaderElement userCredentials = header.addHeaderElement(qNameUserCredentials);
            QName qNameUsername = new QName("https://your.target.namespace/", "Username");
            SOAPHeaderElement username = header.addHeaderElement(qNameUsername );
            username.addTextNode(this.username);
            userCredentials.addChildElement(username);
*/
			// add header element (name, namespace prefix, namespace)
			Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
			SOAPHeaderElement element = sh.addHeaderElement(name);

			// add header element value
			String newValue = "resumo";
			element.setTextContent(newValue);
		//	element.addChildElement(userElement);
			msg.saveChanges();
		} catch (SOAPException e) {
			System.out.printf("Failed to add SOAP header because: %s%n\n", e);
			return;
		} 
		System.out.println("\u001B[35mSigned:\u001B[0m");
	}

	private void validateSignature(SOAPMessageContext smc){
		try {
			// get SOAP envelope header
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPHeader sh = se.getHeader();

			// check header
			if (sh == null) {
				System.out.println("Header not found.");
				return ;
			}

			// get first header element
			Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
			Iterator it = sh.getChildElements(name);
			// check header element
			if (!it.hasNext()) {
				System.out.printf("Header element %s not found.%n", REQUEST_HEADER);
				return ;
			}
			SOAPElement element = (SOAPElement) it.next();

			// *** #4 ***
			// get header element value
			String headerValue = element.getValue();
			System.out.printf("%s got '%s'%n", "Class Name" , headerValue);
		} catch (SOAPException e) {
			System.out.printf("Failed to get SOAP header because of %s%n", e);
		}
		System.out.println("\u001B[31mValidate Message: TODO\u001B[0m");
	}
}
