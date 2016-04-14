package pt.upa.transporter;
import pt.upa.transporter.ws.TransporterManager;
import pt.upa.ui.Dialog;
import javax.xml.registry.JAXRException;

public class TransporterMain {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterMain.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 4) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterMain.class.getName());
			return;
		}
		
		try{
			new TransporterManager(args[0], args[1], args[2], Integer.parseInt(args[3]));
			TransporterManager.getInstance().publish();
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press enter to shutdown");
			System.in.read();
			TransporterManager.getInstance().stop();
		}catch(JAXRException e){
			Dialog.IO().red();
			Dialog.IO().println("Web Service couldn't start.");
			Dialog.IO().println("Reason: " + e);
			Dialog.IO().reset();
		}
	}

}

