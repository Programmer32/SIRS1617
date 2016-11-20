package pt.andred.sirs1617.ws.cli;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import pt.andred.sirs1617.ws.NotFenixService;
import pt.andred.sirs1617.ws.NotFenixPortType;

public class NotFenixClient {
	
	private String _uddiURL;
	private String _wsName;
	private NotFenixService _client;
	private NotFenixPortType _port;
	private BindingProvider _bindingProvider;
	private static final int _TRIES = 50;
	
	public NotFenixClient(String uddiURL, String wsName) {
		_uddiURL = uddiURL;
		_wsName = wsName;
		/*establishConnection();*/
		
	}
	public NotFenixClient(String endpointAddress) {
		/*Dialog.IO().debug("BrokerClient", "Creating a BrokerClient " + endpointAddress);
		if (endpointAddress == null){
			Dialog.IO().debug("BrokerClient", "Null endpoint received");
			return; //Should throw exception
		}
		try{
			
			_client = new BrokerService();
			_port = _client.getBrokerPort();

			_bindingProvider = (BindingProvider) _port;
			Map<String, Object> requestContext = _bindingProvider
					.getRequestContext();
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpointAddress);
		
		}catch(Exception e){
			throw new BrokerClientException("Service with name BrokerWebService not found on UDDI at http://localhost:9090");
		}*/
	}
	
	public void establishConnection() throws NotFenixClientException{
		/*UDDINaming uddiNaming;
		String endpointAddress;
		try{
			Dialog.IO().debug("establishConnection", "Connecting to uddi: " + _uddiURL);
			uddiNaming = new UDDINaming(_uddiURL);
			Dialog.IO().debug("establishConnection", "Searching WebService: " + _wsName);
			endpointAddress = uddiNaming.lookup(_wsName);
			
		}catch(Exception e){
			throw new BrokerClientException("Client failed lookup on UDDI",e);
		}
		
		if (endpointAddress == null){
			Dialog.IO().debug("establishConnection", "Not found!");
			throw new BrokerClientException("Service with name BrokerWebService not found on UDDI at "+ _uddiURL);
		}else{
			System.out.printf("Found %s%n", endpointAddress);
		}
		try{
			_client = new BrokerService();
			_port = _client.getBrokerPort();

			_bindingProvider = (BindingProvider) _port;
			Map<String, Object> requestContext = _bindingProvider
					.getRequestContext();
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
		}catch(Exception e){
			throw new BrokerClientException("Service with name BrokerWebService not found on UDDI at " + _uddiURL + ".\n" + e.getClass() + " " + e.getMessage());
		}*/
	}
	
	public String ping(String name){
		/*for(int i = 0; i < _TRIES; i++){
			try{
				return _port.ping(name);
			}catch(com.sun.xml.ws.client.ClientTransportException e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}*/
		return null;
	}
	
}
