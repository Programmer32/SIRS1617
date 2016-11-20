package pt.andred.sirs1617;

import javax.xml.registry.JAXRException;

import example.ws.handler.AuthenticationHandler;
import pt.andred.sirs1617.ws.*;
import pt.andred.sirs1617.ui.*;

public class NotFenixMain {

	public static void main(String[] args) throws Exception {
		System.out.println(NotFenixMain.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", NotFenixMain.class.getName());
			return;
		}
		
		try{
			new NotFenixManager(args[0], args[1], args[2], new EndpointManager(args[0]));
			AuthenticationHandler.setUDDI_URL(args[0]);
			AuthenticationHandler.setCA_WS_NAME(args[3]);
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press enter to shutdown");
			System.in.read();
			NotFenixManager.getInstance().stop();
		}catch(JAXRException e){
			Dialog.IO().red();
			Dialog.IO().println("Web Service couldn't start.");
			Dialog.IO().println("Reason: " + e);
			Dialog.IO().reset();
		}

	}
}
