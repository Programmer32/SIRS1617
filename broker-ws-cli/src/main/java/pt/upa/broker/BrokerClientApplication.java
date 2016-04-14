package pt.upa.broker;

import java.util.List;
import java.util.Scanner;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.ui.Dialog;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		String _uddiURL = args[0];
		String _wsName = args[1];
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
		System.out.println("UDDI URL: " + _uddiURL);
		System.out.println("WS NAME:  " + _wsName);
		try {
			String command = new String("");
			Scanner input = new Scanner(System.in);
            
			BrokerClient client = new BrokerClient(_uddiURL, _wsName);
			do{
				command = input.next();
	            switch(command){
		            case "ping":
		            	System.out.println(client.ping(""));
		            	break;
		            case "request":
		            	try{
		            		String id = client.requestTransport(input.next(), input.next(), input.nextInt());
		            		System.out.println("Transport requested with id " + id);
		            	}catch(UnavailableTransportFault_Exception | UnknownLocationFault_Exception e){
		            		System.out.println(e.getMessage());
		            	}
		            	break;
		            case "list":
		            	boolean end = true;
		            	List<TransportView> list = client.listTransports();
		            	
		            	if(list.size() == 0){
		            		Dialog.IO().println("There is no transport records on UPA");
		            		break;
		            	}
		            	System.out.println("LISTING TRANSPORTS");
		            	System.out.println(" ID                                               |" +
		            					   " Origin                  |" +
		            					   " Destination             |" +
		            					   " Price     |" +
		            					   " TransporterCompany |" + 
		            					   " Estado da Viagem");
		            	int round = 0;
		            	do{
		            		end = true;
			            	list = client.listTransports();
			            	
			            	if(list.size() == 0){
			            		Dialog.IO().println("There is no transport records on UPA");
			            		break;
			            	}
			            	if(round != 0)
			            		for(TransportView transport : list)
			            			Dialog.IO().clearLine();
			            	
			            	for(TransportView transport : list){
			            		System.out.print(" " + transport.getId());
			            		for(int i = 1 + transport.getId().length(); i < 50; i++)
			            			System.out.print(" ");
			            		System.out.print("|");
	
			            		System.out.print(" " + transport.getOrigin());
			            		for(int i = 1 + transport.getOrigin().length(); i < 25; i++)
			            			System.out.print(" ");
			            		System.out.print("|");
			            		System.out.print(" " + transport.getDestination());
			            		for(int i = 1 + transport.getDestination().length(); i < 25; i++)
			            			System.out.print(" ");
			            		System.out.print("|");
			            		
			            		String price = transport.getPrice().toString();
			            		System.out.print(" " + price);
			            		for(int i = 1 + price.length(); i < 11; i++)
			            			System.out.print(" ");
			            		System.out.print("|");
			            		
			            		System.out.print(" " + transport.getTransporterCompany());
			            		for(int i = 1 + transport.getTransporterCompany().length(); i < 20; i++)
			            			System.out.print(" ");
			            		System.out.print("|");
			            		
			            		if(!transport.getState().value().equals("COMPLETED") && !transport.getState().value().equals("FAILED")){
			            			end = false;
			            		}
			            		
			            		System.out.println(" " + transport.getState().value());
			            	}
		            	}while(!end);
		            	break;
		            case "view":
		            	TransportView transport = client.viewTransport(input.next());
	            		System.out.print(" " + transport.getId());
	            		for(int i = 1 + transport.getId().length(); i < 50; i++)
	            			System.out.print(" ");
	            		System.out.print("|");

	            		System.out.print(" " + transport.getOrigin());
	            		for(int i = 1 + transport.getOrigin().length(); i < 25; i++)
	            			System.out.print(" ");
	            		System.out.print("|");
	            		System.out.print(" " + transport.getDestination());
	            		for(int i = 1 + transport.getDestination().length(); i < 25; i++)
	            			System.out.print(" ");
	            		System.out.print("|");
	            		
	            		String price = transport.getPrice().toString();
	            		System.out.print(" " + price);
	            		for(int i = 1 + price.length(); i < 11; i++)
	            			System.out.print(" ");
	            		System.out.print("|");
	            		
	            		System.out.print(" " + transport.getTransporterCompany());
	            		for(int i = 1 + transport.getTransporterCompany().length(); i < 20; i++)
	            			System.out.print(" ");
	            		System.out.print("|");
	            		
	            		System.out.println(" " + transport.getState().value());
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
			e.printStackTrace();
		}
	}

}
