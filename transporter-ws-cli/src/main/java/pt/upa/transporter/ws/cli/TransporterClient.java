package pt.upa.transporter.ws.cli;

import java.util.List;
import java.util.Map;
import pt.upa.ui.Dialog;
import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStatus;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClient {

	private String _uddiURL;
	private String _wsName;
	private String _endpoint;
	private TransporterService _client;
	private TransporterPortType _port;
	private BindingProvider _bindingProvider;
	
	/**
	 * This is the constructor that doesn't require UDDI connection
	 * Using this constructor doesn't allow for dynamic changes of the WebService
	 * 
	 * @param endpoint
	 */
	public TransporterClient(String endpoint){
		Dialog.IO().debug("TransporterClient", "Creating TransporterClient");

		Dialog.IO().debug("TransporterClient", "Setting Endpoint: " + endpoint);
		_endpoint = endpoint;

		Dialog.IO().debug("TransporterClient", "Creating TransporterService");
		_client = new TransporterService();
		Dialog.IO().debug("TransporterClient", "TransporterService created");

		Dialog.IO().debug("TransporterClient", "Setting Port");
		_port = _client.getTransporterPort();

		//Should verify what's being done in here!!!
		//FIXME
		_bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = _bindingProvider
				.getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				_endpoint);

		Dialog.IO().debug("TransporterClient", "TransporterClient created");
	}
	
	/**
	 * This is the main constructor
	 * 
	 * Using this constructor allows the client to reconnect to the WebService asking UDDI Server for newer WebService Endpoint
	 * 
	 * @param uddiURL
	 * @param wsName
	 * @throws TransporterClientException
	 */
	public TransporterClient(String uddiURL, String wsName) throws TransporterClientException {
		Dialog.IO().debug("TransporterClient", "Creating TransporterClient");
		Dialog.IO().debug("TransporterClient", "Setting uddiURL: " + uddiURL);
		_uddiURL = uddiURL;
		Dialog.IO().debug("TransporterClient", "Setting WebService Name: " + wsName);
		_wsName = wsName;

		Dialog.IO().debug("TransporterClient", "Establish Connection");
		establishConnection();
		
		Dialog.IO().debug("TransporterClient", "TransporterClient created");
	}
	
	/**
	 * This function connectsto UDDI asking for WebService endpoint
	 * 
	 * @throws TransporterClientException
	 */
	public void establishConnection() throws TransporterClientException{
		try{
			Dialog.IO().debug("establishConnection", "Creating UDDINaming: " + _uddiURL);
			UDDINaming uddiNaming = new UDDINaming(_uddiURL);
			Dialog.IO().debug("establishConnection", "LookingUp for WebService: " + _wsName);
			_endpoint = uddiNaming.lookup(_wsName);
		}catch(JAXRException e){
			Dialog.IO().debug("establishConnection", "Client failed to lookup on UDDI");
			throw new TransporterClientException("Client failed lookup on UDDI",e);
		}
		
		if (_endpoint == null){
			Dialog.IO().error("Not found!");
			throw new TransporterClientException("Service not found on UDDI");
		}else{
			Dialog.IO().debug("establishConnection","Found " + _endpoint + "%n");
		}

		Dialog.IO().debug("TransporterClient", "Creating TransporterService");
		_client = new TransporterService();
		Dialog.IO().debug("TransporterClient", "TransporterService created");
		
		Dialog.IO().debug("establishConnection", "Setting Port");
		_port = _client.getTransporterPort();

		//Should verify what's being done in here!!!
		//FIXME
		_bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = _bindingProvider
				.getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				_endpoint);
	}
	
	/**
	 * This method calls the WebService method with the "I'm alive" string
	 * 
	 * @return String
	 */
	public String ping(){
		return _port.ping("I'm alive");
	}
	
	/**
	 * This method calls the WebService method Ping with the given string
	 * 
	 * @param s
	 * @return
	 */
	public String ping(String s){
		return _port.ping(s);
	}
	
	/**
	 * This method calls the WebService method jobStatus and returns the returned value
	 * 
	 * @param id
	 * @return
	 */
	public JobView jobStatus(String id){
		return _port.jobStatus(id);
	}
	
	/**
	 * This method calls the WebService method listJobs and returns the returned value
	 * @return
	 */
	public List<JobView> listJobs(){
		return _port.listJobs();
	}
	
	/**
	 * This method calls the WebService method requestJob
	 * @param origin
	 * @param destination
	 * @param price
	 * @return
	 * @throws BadLocationFault_Exception
	 * @throws BadPriceFault_Exception
	 */
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		return _port.requestJob(origin, destination, price);
	}
	
	/**
	 * This method calls the WebService method decideJob
	 * 
	 * @param id
	 * @param accept
	 * @return
	 * @throws BadJobFault_Exception
	 */
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception{
		return _port.decideJob(id, accept);
	}
	
	/**
	 * This method calls the WebService method clearJobs
	 */
	public void clearJobs(){
		_port.clearJobs();
	}
	
	@Override
	/**
	 * Not sure why this is in here?!
	 */
	public String toString() {
		return "uddiURL: " + _uddiURL + ", wsName: " + _wsName + ", endpoint" + _endpoint; 
	}
}
