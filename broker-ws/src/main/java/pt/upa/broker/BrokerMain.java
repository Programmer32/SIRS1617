package pt.upa.broker;

import javax.xml.ws.Endpoint;

import pt.upa.broker.ws.BrokerPort;
import pt.upa.ui.Dialog;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class BrokerMain {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerMain.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerMain.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		try {
			BrokerPort t = new BrokerPort(uddiURL);
			endpoint = Endpoint.create(t);

			// publish endpoint
			Dialog.IO().println("Starting " + url);
			endpoint.publish(url);
			Dialog.IO().println("Started!");
			// publish to UDDI
			Dialog.IO().println("Publishing '" + name + "' to UDDI at " + uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);

			// wait
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press any key to shutdown");
			System.in.read();
		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint

					Dialog.IO().println("Stoping " + url);
					endpoint.stop();

					Dialog.IO().println("Stopped " + url);
				}
			} catch (Exception e) {
				Dialog.IO().println("Caught exception when stopping: " + e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					
					Dialog.IO().println("Deleted '" + name +"' from UDDI");
				}
			} catch (Exception e) {
				Dialog.IO().println("Caught exception when deleting: " + e);
			}
		}

	}

}
