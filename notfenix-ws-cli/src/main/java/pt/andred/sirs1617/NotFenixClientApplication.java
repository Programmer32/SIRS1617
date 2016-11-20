package pt.andred.sirs1617;

import java.util.List;
import java.util.Scanner;

import pt.andred.sirs1617.ws.cli.NotFenixClient;
import pt.andred.sirs1617.ui.Dialog;
public class NotFenixClientApplication {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		String _uddiURL = args[0];
		String _wsName = args[1];
		System.out.println(NotFenixClientApplication.class.getSimpleName() + " starting...");
		System.out.println("UDDI URL: " + _uddiURL);
		System.out.println("WS NAME:  " + _wsName);
		try {
			String command = new String("");
			Scanner input = new Scanner(System.in);
            
			NotFenixClient client = new NotFenixClient(_uddiURL, _wsName);
			do{
				command = input.next();
	            switch(command){
		            case "ping":
		            	Dialog.IO().println(client.ping(""));
		            	break;
	            	case "exit":
	            		System.out.println("Exiting app");
	            		break;
	            	default:
	            		System.out.println("Command not found");
	            }
			}while(!command.equals("exit"));
			input.close();
		} catch (Exception e) {
			if(e.getMessage().equals("Client not found")){
				Dialog.IO().error("Could not connect to NotFenix WebService!");
			}else{
				e.printStackTrace();
			}
		}
	}

}
