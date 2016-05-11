package example.ws.handler;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.upa.ui.Dialog;
/**
 *  This SOAPHandler outputs the contents of
 *  inbound and outbound messages.
 */
public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

	
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    /**
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context
     * to see if this is an outgoing or incoming message.
     * Write a brief message to the print stream and
     * output the message. The writeTo() method can throw
     * SOAPException or IOException
     */
    public static void logToSystemOut(SOAPMessageContext smc) {
    	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	
    	if (outbound) {
    		Dialog.IO().yellow();
    		Dialog.IO().debug("Outbound SOAP message:");
    		Dialog.IO().white();
    	} else {
    		Dialog.IO().cyan();
    		Dialog.IO().debug("Inbound SOAP message:");
    		Dialog.IO().white();
    	}
    	
    	SOAPMessage message = smc.getMessage();
    	try {
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		message.writeTo(out);
    		String strMsg = new String(out.toByteArray());
    		strMsg = prettyFormat(strMsg);
    		Dialog.IO().SOAP(strMsg);
    	} catch (Exception e) {
    		System.out.printf("Exception in handler: %s%n\n", e);
    	}
    }

    public static void logToSystemERR(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outbound) {
        	Dialog.IO().yellow();
        	Dialog.IO().println("Outbound SOAP message:");
        	Dialog.IO().white();
        } else {
        	Dialog.IO().cyan();
        	Dialog.IO().println("Inbound SOAP message:");
        	Dialog.IO().white();
        }

        SOAPMessage message = smc.getMessage();
        try {
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
        	message.writeTo(out);
        	String strMsg = new String(out.toByteArray());
        	strMsg = prettyFormat(strMsg);
        	Dialog.IO().print(strMsg);
        } catch (Exception e) {
            System.out.printf("Exception in handler: %s%n\n", e);
        }
    }
    
    
    protected static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer(); 
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    protected static String prettyFormat(String input) {
        return prettyFormat(input, 2);
    }
    
    
    
    
}
