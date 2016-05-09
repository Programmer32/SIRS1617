package pt.upa.transporter.ws.cli;

import java.util.List;
import java.util.Map;

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
	public TransporterClient(String endpoint){

		_endpoint = endpoint;
		_client = new TransporterService();
		_port = _client.getTransporterPort();

		_bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = _bindingProvider
				.getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				_endpoint);
	}
	public TransporterClient(String uddiURL, String wsName) throws TransporterClientException {
		_uddiURL = uddiURL;
		_wsName = wsName;
		establishConnection();
	}
	
	public void establishConnection() throws TransporterClientException{
		try{
			UDDINaming uddiNaming = new UDDINaming(_uddiURL);
			_endpoint = uddiNaming.lookup(_wsName);
		}catch(JAXRException e){
			throw new TransporterClientException("Client failed lookup on UDDI",e);
		}
		
		if (_endpoint == null){
			System.out.println("Not found!");
			throw new TransporterClientException("Service not found on UDDI");
		}else{
			System.out.printf("Found %s%n", _endpoint);
		}

		_client = new TransporterService();
		_port = _client.getTransporterPort();

		_bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = _bindingProvider
				.getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				_endpoint);
	}
	
	public String ping(){
		return _port.ping("ola");
	}
	public String ping(String s){
		return _port.ping(s);
	}
	public JobView jobStatus(String id){
		return _port.jobStatus(id);
	}	
	public List<JobView> listJobs(){
		return _port.listJobs();
	}
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		return _port.requestJob(origin, destination, price);
	}
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception{
		return _port.decideJob(id, accept);
	}
	
	public void clearJobs(){
		_port.clearJobs();
	}
	
	@Override
	public String toString() {
		return "uddiURL: " + _uddiURL + ", wsName: " + _wsName + ", endpoint" + _endpoint; 
	}
}
