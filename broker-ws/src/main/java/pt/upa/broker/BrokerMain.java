package pt.upa.broker;

import javax.xml.registry.JAXRException;

import example.ws.handler.AuthenticationHandler;
import pt.upa.broker.ws.BrokerManager;
import pt.upa.broker.ws.EndpointManager;
import pt.upa.ui.Dialog;

public class BrokerMain {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerMain.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerMain.class.getName());
			return;
		}
		
		try{
			new BrokerManager(args[0], args[1], args[2], new EndpointManager(args[0]));
			AuthenticationHandler.setUDDI_URL(args[0]);
			AuthenticationHandler.setCA_WS_NAME(args[3]);
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press enter to shutdown");
			System.in.read();
			BrokerManager.getInstance().stop();
		}catch(JAXRException e){
			Dialog.IO().red();
			Dialog.IO().println("Web Service couldn't start.");
			Dialog.IO().println("Reason: " + e);
			Dialog.IO().reset();
		}

	}
}
