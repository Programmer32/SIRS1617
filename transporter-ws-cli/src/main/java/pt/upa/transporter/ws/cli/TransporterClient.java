package pt.upa.transporter.ws.cli;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
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
	public TransporterClient(String uddiURL, String wsName) throws JAXRException {
		_uddiURL = uddiURL;
		_wsName = wsName;
		establishConnection();
	}
	
	public void establishConnection() throws JAXRException{
		UDDINaming uddiNaming = new UDDINaming(_uddiURL);
		
		_endpoint = uddiNaming.lookup(_wsName);

		if (_endpoint == null){
			System.out.println("Not found!");
			return; //Should throw exception
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
	public JobView jobStatus(String id){
		return _port.jobStatus(id);
	}	
	public List<JobView> listJobs(){
		return _port.listJobs();
	}
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		return _port.requestJob(origin, destination, price);
	}
}
