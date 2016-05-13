package pt.upa.transporter;
import java.util.Scanner;

import example.ws.handler.AuthenticationHandler;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.ui.Dialog;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		String _uddiURL = args[0];
		String _wsName = args[1];
		String _caWsName = args[3];
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		System.out.println("UDDI URL: " + _uddiURL);
		System.out.println("WS NAME:  " + _wsName);

		AuthenticationHandler.setUDDI_URL(_uddiURL);
		AuthenticationHandler.setCA_WS_NAME(_caWsName);
		
		try {
			String command = new String();
			Scanner input = new Scanner(System.in);
			
			TransporterClient client = new TransporterClient(_uddiURL, _wsName);
			
			do{
				command = input.next();
				switch(command){
				case "ping":
					Dialog.IO().println(client.ping(""));
					break;
				case "list":
					for(JobView job : client.listJobs()){
						Dialog.IO().println("Transporter: " + job.getCompanyName() + " " + job.getJobOrigin() + " " + job.getJobDestination() + " " + job.getJobPrice() + " ");
					}
					if(client.listJobs().size() == 0){
						Dialog.IO().println("There are no jobs.");
					}
					break;
				case "request":
					JobView j = client.requestJob(input.next(), input.next(), input.nextInt());
					if(j == null) Dialog.IO().println("No job returned");
					else Dialog.IO().println("ID: " + j.getJobIdentifier() + " | Price: " + j.getJobPrice());
				}
			}while(!command.equals("exit"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
