package pt.upa.ca;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.upa.ca.ws.*;
import pt.upa.ca.ws.cli.CAClient;

public class CAClientApplication {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", CAClientApplication.class.getName());
			return;
		}

		CAImplService service = new CAImplService();
		CA port = service.getCAImplPort();

		String uddiURL = args[0];
		String name = args[1];

		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

		System.out.printf("Looking for '%s'%n", name);
		String endpointAddress = uddiNaming.lookup(name);

		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

		CAClient client = new CAClient(port);

		String command = new String("");
		Scanner input = new Scanner(System.in);
		System.out.println("Done");
		do{
			command = input.next();
			switch(command){
			case "add":
				client.addEntity(input.next());
				break;
			case "get":
				System.out.println("get RESULT: " +client.getPublicKey(input.next()));
				break;
			default:
				System.out.println("possible commands:\n ºget\n ºadd\n");
				break;
			}
		}while(!command.equals("exit"));
		input.close();

	}
}
