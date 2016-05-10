package pt.upa.broker.ws.cli;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.ui.Dialog;

public class BrokerClient {
	
	private String _uddiURL;
	private String _wsName;
	private BrokerService _client;
	private BrokerPortType _port;
	private BindingProvider _bindingProvider;
	
	public BrokerClient(String uddiURL, String wsName) throws BrokerClientException {
		_uddiURL = uddiURL;
		_wsName = wsName;
		establishConnection();
		
	}
	public BrokerClient(String endpointAddress)  throws BrokerClientException {
		Dialog.IO().debug("BrokerClient", "Creating a BrokerClient " + endpointAddress);
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
		}
	}
	
	public void establishConnection() throws BrokerClientException{
		UDDINaming uddiNaming;
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
		}
	}
	
	public String ping(String name) throws BrokerClientException{
		try{
			return _port.ping(name);
		}catch(Exception e){
			establishConnection();
			return _port.ping(name);
		}
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
	
	public void addSlave(String endpoint){
		_port.addSlave(endpoint);
	}
	
	public void updateJob(String id, TransportView status){
		_port.updateJob(id, status);
	}
	
	public void pingSlave(int a){
		_port.pingSlave(a);
	}
}
