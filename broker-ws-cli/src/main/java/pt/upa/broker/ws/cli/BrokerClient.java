package pt.upa.broker.ws.cli;

import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

public class BrokerClient {
	
	private String _uddiURL;
	private String _wsName;
	private BrokerService _client;
	private BrokerPortType _port;
	private BindingProvider _bindingProvider;
	
	public BrokerClient(String uddiURL, String wsName) throws JAXRException{
		_uddiURL = uddiURL;
		_wsName = wsName;
		establishConnection();
	}
	public void establishConnection() throws JAXRException{
		UDDINaming uddiNaming = new UDDINaming(_uddiURL);
		
		String endpointAddress = uddiNaming.lookup(_wsName);

		if (endpointAddress == null){
			System.out.println("Not found!");
			return; //Should throw exception
		}else{
			System.out.printf("Found %s%n", endpointAddress);
		}

		_client = new BrokerService();
		_port = _client.getBrokerPort();

		_bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = _bindingProvider
				.getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
	}
	
	public String ping(String name){
		return _port.ping(name);
	}
}
