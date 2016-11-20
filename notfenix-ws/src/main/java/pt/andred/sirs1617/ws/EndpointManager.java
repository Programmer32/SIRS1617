package pt.andred.sirs1617.ws;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import com.sun.xml.ws.client.ClientTransportException;

import example.ws.handler.AuthenticationHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.andred.sirs1617.ui.Dialog;

public class EndpointManager {

	private String _uddiURL;
	private UDDINaming _uddiNaming;
	private Endpoint _endpoint;
	private String _name;
	
	public EndpointManager(String uddiURL) throws JAXRException{
		Dialog.IO().debug("EndpoingManager", "Creating Endpoint Manager");
		_uddiURL = uddiURL;
		AuthenticationHandler.setUDDI_URL(_uddiURL);
		Dialog.IO().debug("EndpoingManager", "Endpoint Manager created");
	}	
	/**
	 * This method publishes the WebService on UDDI Sever
	 * @param BrokerPort to associate with the WebService
	 * @param WebService Name
	 * @param Endpoint URL
	 * @throws JAXRException
	 */
	protected void publish(NotFenixPort t, String wsName, String url) throws JAXRException{
		Dialog.IO().debug("publish", "Init publishing " + wsName + " available on " + url);
		try{
			Dialog.IO().debug("publish", "Creating endpoint");
			_endpoint = Endpoint.create(t);
			Dialog.IO().debug("publish", "endpoint created");
			_name = wsName;

			Dialog.IO().debug("publish", "Starting " + url);
			_endpoint.publish(url);
			Dialog.IO().debug("publish", "Started");
			
			if(_uddiNaming == null){
				// publish to UDDI
				Dialog.IO().debug("publish", "Publishing '" + wsName + "' to UDDI at " + _uddiURL);
				_uddiNaming = new UDDINaming(_uddiURL);
			}
			
			Dialog.IO().debug("publish", "Binding with name: " + _name);
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
				Dialog.IO().debug("publish", "Unbinding with name: " + _name);
				_uddiNaming.unbind(_name);
				
				Dialog.IO().println("Deleted '" + _name +"' from UDDI");
			}
		} catch (Exception e) {
			Dialog.IO().println("Caught exception when deleting: " + e);
		}
		Dialog.IO().debug("EndManager.unpublish", "Endpoint has unpublished WebService");
		
	}
	
}
