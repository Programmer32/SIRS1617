package pt.upa.broker.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.ui.Dialog;

public class EndpointManager {

	private String _uddiURL;
	private UDDINaming _uddiNaming;
	private Endpoint _endpoint;
	private String _name;
	
	public EndpointManager(String uddiURL) throws JAXRException{
		Dialog.IO().debug("EndpoingManager", "Creating Endpoint Manager");
		_uddiURL = uddiURL;
		Dialog.IO().debug("EndpoingManager", "Endpoint Manager created");
	}
	
	protected TransporterClient transporter(String companyName) throws JAXRException{
		String endpoint = _uddiNaming.lookup(companyName);
		
		if(endpoint != null){
			try{
				Dialog.IO().debug("transporter","Endpoint received");
				TransporterClient client = new TransporterClient(endpoint);
				Dialog.IO().debug("transporter","ENDPOINT: " + endpoint + "\nPING: " + client.ping());
				Dialog.IO().debug("transporter","Client found and valid");
				return client;
			}catch(Exception e){ //FIXME
				Dialog.IO().debug("transporter","Client found but it's invalid");
				Dialog.IO().debug("transporter","Connection refused");
				return null;
			}
		}
		Dialog.IO().debug("transporter", "No endpoint received from UDDI");
		return null;
	}
	
	protected List<TransporterClient> transporters(){
		Dialog.IO().debug("transporters","Getting all transporters available on uddi");
		Map<String, TransporterClient> clients = new HashMap<String, TransporterClient>();
		try {
			Dialog.IO().debug("Transporters","Looking on UDDI server");
			Collection<String> endpoints = _uddiNaming.list("UpaTransporter%");
			Dialog.IO().debug("Transporters","UDDI server answered");
			if(endpoints != null){
				Dialog.IO().debug("Transporters","Iterating over endpoints received");
				for(String endpoint : endpoints){
					try{
						Dialog.IO().debug("Transporters","ENDPOINT: " + endpoint);
						Dialog.IO().debug("Transporters","Trying to ping client to check if it's alive");
						TransporterClient client = new TransporterClient(endpoint+"asd");
						Dialog.IO().debug("Transporters","Client is alive and ping response is: " + client.ping());
						clients.put(endpoint,client);
						Dialog.IO().debug("Transporters","Client found and it's valid");
					}catch(Exception e){
						System.out.println(e);
						Dialog.IO().debug("Transporters","Client found and invalid");
						Dialog.IO().debug("Transporters","Connection refused");
					}
				}
			}
		} catch (JAXRException e) { //FIXME check exception name
			e.printStackTrace();
		}
		return new ArrayList<TransporterClient>(clients.values());
	}
	
	protected void publish(BrokerPort t, String wsName, String url) throws JAXRException{
		Dialog.IO().debug("publish", "Init publishing " + wsName + " available on " + url);
		try{
			Dialog.IO().debug("publish", "Creating endpoint");
			_endpoint = Endpoint.create(t);
			Dialog.IO().debug("publish", "endpoint created");
			_name = wsName;

			Dialog.IO().debug("publish", "Starting " + url);
			_endpoint.publish(url);
			Dialog.IO().debug("publish", "Started");
	
			// publish to UDDI
			Dialog.IO().debug("publish", "Publishing '" + wsName + "' to UDDI at " + _uddiURL);
			
			_uddiNaming = new UDDINaming(_uddiURL);

			Dialog.IO().debug("publish", "Binding");
			_uddiNaming.rebind(_name, url);
			Dialog.IO().debug("publish", "End bind");
		}catch(JAXRException e){
			unpublish();
			throw e;
		}
	}
	protected void unpublish(){
		Dialog.IO().debug("EndPManager.unpublish", "Endpoint is going to unpublish");
		try{
			if(_endpoint != null) _endpoint.stop();
		}catch(Exception e){
			Dialog.IO().println("Caught exception when stopping: " + e);
		}
		try {
			if(_uddiNaming != null && _name != null) {
				// delete from UDDI
				_uddiNaming.unbind(_name);
				
				Dialog.IO().println("Deleted '" + _name +"' from UDDI");
			}
		} catch (Exception e) {
			Dialog.IO().println("Caught exception when deleting: " + e);
		}
		Dialog.IO().debug("EndManager.unpublish", "Endpoint has unpublished WebService");
		
	}

	public void getMaster(String name) throws JAXRException {
		if(_uddiNaming == null) _uddiNaming = new UDDINaming(_uddiURL);
		String endpoint = _uddiNaming.lookup(name);
		
		if(endpoint != null){
			try{
				Dialog.IO().debug("transporter","Endpoint received: " + endpoint);
				BrokerClient client = new BrokerClient(endpoint);
				while(true){
					Dialog.IO().debug("transporter","ENDPOINT: " + endpoint + "\nPING: " + client.ping("Trying to become master"));
					Dialog.IO().debug("transporter","Client found and valid");
					Thread.sleep(500);
				}
			}catch(Exception e){ //FIXME
				Dialog.IO().debug("transporter","Client found but it's invalid");
				Dialog.IO().debug("transporter","Connection refused");
				return;
			}
		}
		Dialog.IO().debug("transporter", "No endpoint received from UDDI. I'll become master");		
	}
}
