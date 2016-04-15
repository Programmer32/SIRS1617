package pt.upa.transporter;
import pt.upa.transporter.ws.TransporterManager;
import pt.upa.ui.Dialog;
import javax.xml.registry.JAXRException;

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
			new TransporterManager(args[0], args[1], args[2], Integer.parseInt(args[3]));
			TransporterManager.getInstance().publish();
			Dialog.IO().println("Waiting for connections");
			Dialog.IO().println("Press enter to shutdown");
			Dialog.IO().readLine();
			TransporterManager.getInstance().stop();
		}catch(JAXRException e){
			Dialog.IO().error("Web Service couldn't start.");
			Dialog.IO().error("Reason: " + e);
		}
		for(Thread t : Thread.getAllStackTraces().keySet()){
			Dialog.IO().debug(t.getName() + " " + t.isAlive());
		}
	}

}

