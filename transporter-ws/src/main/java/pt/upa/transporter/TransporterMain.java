package pt.upa.transporter;
import pt.upa.transporter.ws.TransporterManager;
import pt.upa.ui.Dialog;
import javax.xml.registry.JAXRException;

import example.ws.handler.AuthenticationHandler;

public class TransporterMain {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterMain.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 4) {
			Dialog.IO().error("Argument(s) missing!");
			Dialog.IO().error("Usage: java " + TransporterMain.class.getName() + " uddiURL wsName wsURL%n");
			return;
		}
		
		try{
			TransporterManager.config(args[0], args[1], args[2], Integer.parseInt(args[3]));
			TransporterManager.publish();

			AuthenticationHandler.setUDDI_URL(args[0]);
			AuthenticationHandler.setCA_WS_NAME(args[4]);
			
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press enter to shutdown");
			Dialog.IO().readLine();
			TransporterManager.stop();
		}catch(JAXRException e){
			Dialog.IO().error("Web Service couldn't start.");
			Dialog.IO().error("Reason: " + e);
		}
		for(Thread t : Thread.getAllStackTraces().keySet()){
			Dialog.IO().debug(t.getName() + " " + t.isAlive());
		}
	}

}

