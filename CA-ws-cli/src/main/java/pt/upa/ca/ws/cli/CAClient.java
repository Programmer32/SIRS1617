package pt.upa.ca.ws.cli;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.*;

public class CAClient {
	
	private String _uddiURL;
	private String _wsName;
	private CAImplService _client;
	private CA _port;
	private BindingProvider _bindingProvider;
	
	public CAClient(String uddiURL, String wsName) throws Exception{
		_uddiURL = uddiURL;
		_wsName = wsName;
		establishConnection();
		
	}
	public CAClient(String endpointAddress) throws Exception{
		System.out.println("CAClient: " + "Creating a CAClient " + endpointAddress);
		if (endpointAddress == null){
			System.out.println("CAClient " + "Null endpoint received");
			return; //Should throw exception
		}
		try{
			
			_client = new CAImplService();
			_port = _client.getCAImplPort();

			_bindingProvider = (BindingProvider) _port;
			Map<String, Object> requestContext = _bindingProvider
					.getRequestContext();
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					endpointAddress);
		
		}catch(Exception e){
			throw new Exception("Service with name " + _wsName +" not found on UDDI at " + _uddiURL + " because: " + e.getClass() + " : " + e.getMessage());
		}
	}
	public CAClient(CA ca){
		_port = ca;
	}
	
	public void establishConnection() throws Exception{
		UDDINaming uddiNaming;
		String endpointAddress;
		try{
			System.out.println("establishConnection" + "Connecting to uddi: " + _uddiURL);
			uddiNaming = new UDDINaming(_uddiURL);
			System.out.println("establishConnection" + "Searching WebService: " + _wsName);
			endpointAddress = uddiNaming.lookup(_wsName);
			
		}catch(Exception e){
			throw new Exception("Client failed lookup on UDDI",e);
		}
		
		if (endpointAddress == null){
			System.out.println("establishConnection: " + "Not found!");
			throw new Exception("Service with name BrokerWebService not found on UDDI at "+ _uddiURL);
		}else{
			System.out.printf("Found %s%n", endpointAddress);
		}
		try{
			_client = new CAImplService();
			_port = _client.getCAImplPort();

			_bindingProvider = (BindingProvider) _port;
			Map<String, Object> requestContext = _bindingProvider
					.getRequestContext();
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
		}catch(Exception e){
			throw new Exception("Service with name " + _wsName +" not found on UDDI at " + _uddiURL + " because: " + e.getClass() + " : " + e.getMessage());
		}
	}

	public void addEntity(String name) {
		System.out.println("tyring  to add Entity: " + name);
		_port.addEntity(name);
		System.out.println("success to add Entity: " + name);
	}

	public String getPublicKey(String name) throws EntityNotFoundException_Exception {
		System.out.println("requesting PublicKey name: " + name);
		return _port.getPublicKey(name);
	}	
}
