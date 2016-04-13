package pt.upa.broker.ws.cli;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

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
	
	public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		return _port.requestTransport(origin, destination, price);
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		return _port.viewTransport(id);
	}
	public List<TransportView> listTransports(){
		return _port.listTransports();
	}
	public void clearTransports(){
		_port.clearTransports();
	}
}
