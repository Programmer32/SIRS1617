package pt.upa.transporter.ws;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import example.ws.handler.AuthenticationHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ui.Dialog;

public class EndpointManager {

	private String _uddiURL;
	private UDDINaming _uddiNaming;
	private Endpoint _endpoint;
	private String _name;
	
	protected EndpointManager(String uddiURL) throws JAXRException{
		Dialog.IO().debug("EndpoingManager", "Creating Endpoint Manager");
		_uddiURL = uddiURL;
		AuthenticationHandler.setUDDI_URL(_uddiURL);
		Dialog.IO().debug("EndpoingManager", "Endpoint Manager created");
	}
	
	protected void publish(TransporterPortType t, String wsName, String url) throws JAXRException{
		try{
			_endpoint = Endpoint.create(t);
			_name = wsName;
			
			Dialog.IO().println("Starting " + url);
			_endpoint.publish(url);
			Dialog.IO().println("Started!");
	
			// publish to UDDI
			Dialog.IO().println("Publishing '" + wsName + "' to UDDI at " + _uddiURL);
			_uddiNaming = new UDDINaming(_uddiURL);
			_uddiNaming.rebind(_name, url);
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
}
