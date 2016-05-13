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
	private static final int _TRIES = 50;
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
	
	public String ping(String name){
		for(int i = 0; i < _TRIES; i++){
			try{
				return _port.ping(name);
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
		return null;
	}
	
	public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		for(int i = 0; i < _TRIES; i++){
			try{
				return _port.requestTransport(origin, destination, price);
				
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
		return null;
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		for(int i = 0; i < _TRIES; i++){
			try{
				return _port.viewTransport(id);
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
		return null;
	}
	public List<TransportView> listTransports(){
		for(int i = 0; i < _TRIES; i++){
			try{
				return _port.listTransports();
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
		return null;
	}
	public void clearTransports(){
		for(int i = 0; i < _TRIES; i++){
			try{
				_port.clearTransports();
				return;
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
	}
	
	public void addSlave(String endpoint){
		for(int i = 0; i < _TRIES; i++){
			try{
				_port.addSlave(endpoint);
				return;
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
	}
	
	public void updateJob(String id, TransportView status){
		for(int i = 0; i < _TRIES; i++){
			try{
				_port.updateJob(id, status);
				return;
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
	}
	
	public void pingSlave(int a){
		for(int i = 0; i < _TRIES; i++){
			try{
				_port.pingSlave(a);
				return;
			}catch(Exception e){
				try {
					establishConnection();
				} catch (BrokerClientException e1) {
					//This shouldn't happen
				}
			}
		}
	}
}
