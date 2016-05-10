package pt.upa.broker.ws;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.registry.JAXRException;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import example.ws.handler.AuthenticationHandler;
import pt.upa.broker.ws.InvalidPriceFault;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.upa.broker.ws.cli.BrokerClientException;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.ui.Dialog;

public class BrokerManager {
	
	private class IDConverter{
		private String _id;
		private String _companyName;
		protected IDConverter(String id, String companyName){ _id = id; _companyName = companyName; }
		protected String id(){ return _id; }
		protected String companyName(){ return _companyName; }
		
	}
	
	private ArrayList<String> _brokerSlaves;
	
	private String _uddiURL;
	private String _wsName;
	private String _wsURL;
	
	private Map<String, IDConverter> _ids;
	
	private BrokerPort _port;
	private EndpointManager _endpoint;
	private Map<String, TransportView> _transports;
	
	private boolean _master;
	
	private static BrokerManager _broker;
	
	
	public String getNewId(){ return new String("UpaTransports" + _transports.size()); }
	
	public static BrokerManager getInstance(){
		if(_broker == null){
			_broker = new BrokerManager();
			try {
				_broker._master = false;
				_broker._port = new BrokerPort();
				_broker._ids = new HashMap<String, IDConverter>();
			} catch (JAXRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_broker.clearTransports();
		}
		return _broker;
	}
	
	private static final String[] NORTE = { "Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança" };
	private static final String[] CENTRO = { "Lisboa", "Leiria", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda" };
	private static final String[] SUL = { "Setúbal", "Évora", "Portalegre", "Beja", "Faro" };
	
	private BrokerManager(){}
	
	public BrokerManager(String uddiURL, String name, String url, EndpointManager endpointManager) throws JAXRException{
		Dialog.IO().debug(this.getClass().getSimpleName(), "Creating instance");
		
		EndpointManager endpoint = new EndpointManager(uddiURL);

		getInstance()._uddiURL = uddiURL;
		getInstance()._wsName = name;
		getInstance()._wsURL = url;
		getInstance()._endpoint = endpointManager;
		
		//Checks if master is alive
		if(endpoint.masterAlive(name)){
			try {
				//register as slave is masters is alive
				//if it throws an exception while registering as slave, it will become master
				Dialog.IO().debug(this.getClass().getSimpleName(), "Registering as Slave");
				registerAsSlave();
			} catch (BrokerClientException e) {
				Dialog.IO().debug(this.getClass().getSimpleName(), "Error while registering as slave");
				Dialog.IO().debug(this.getClass().getSimpleName(), "Becoming master");
				becomeMaster();
				Dialog.IO().debug(this.getClass().getSimpleName(), "Master Created");
			}
		}
		//register as master if master is dead
		else{
			Dialog.IO().debug(this.getClass().getSimpleName(), "Becoming master");
			becomeMaster();
			Dialog.IO().debug(this.getClass().getSimpleName(), "Master Created");
		}
		Dialog.IO().debug(this.getClass().getSimpleName(), "Created instance");
	}
	
	/**
	 * This method is called to set a Broker as Masters
	 * @throws JAXRException 
	 */
	public void becomeMaster() throws JAXRException{
		Dialog.IO().debug("becomeMaster", "Becoming master");
		getInstance()._master = true;
		Dialog.IO().debug("becomeMaster", "Port: " + getInstance()._port);
		Dialog.IO().debug("becomeMaster", "WebService Name: " + getInstance()._wsName);
		Dialog.IO().debug("becomeMaster", "WebService URL: " + getInstance()._wsURL);
		getInstance()._endpoint.publish(getInstance()._port, getInstance()._wsName, getInstance()._wsURL);
		
		//Sets Author on AuthenticationHandler for messages sent by this app
		//This is needed to check authenticity of messages
		AuthenticationHandler.setAuthor(getInstance()._wsName + "@" + getInstance()._wsURL);

	}
	
	/**
	 * This method register slave broker on Masters
	 * @throws JAXRException 
	 * @throws BrokerClientException 
	 */
	public void registerAsSlave() throws BrokerClientException{
		Dialog.IO().debug("registerAsSlave", "Becoming slave");
		getInstance()._master = false;
		try{
			Dialog.IO().debug("registerAsSlave", "Publishing WebService as slave");
			getInstance()._endpoint.publish(getInstance()._port, getInstance()._wsName, getInstance()._wsURL + "_Slave");
			Dialog.IO().debug("registerAsSlave", "Creating BrokerClient ");
			
			new BrokerClient(getInstance()._uddiURL, getInstance()._wsName).addSlave(getInstance()._wsURL);
			
			Dialog.IO().debug("registerAsSlave", "Added as slave on Master Broker");
		}catch(JAXRException e){
			Dialog.IO().debug("registerAsSlave", "Error on connecting to broker master");
			throw new BrokerClientException("Could not connect to Broker Master");
		}
	}
	
	public void stop() throws JAXRException{
		Dialog.IO().debug("BrokerManager.stop", "Endpoint is going to unpublish");
		getInstance()._endpoint.unpublish();
		Dialog.IO().debug("BrokerManager.stop", "Endpoint has unpublished WebService");
	}
	private TransporterClient transporter(String companyName) throws JAXRException{
		Dialog.IO().debug("transporter","Getting transporter from uddi");
		return getInstance()._endpoint.transporter(companyName);
	}
	
	private List<TransporterClient> transporters(){
		return getInstance()._endpoint.transporters();		
	}

	public String ping(String name){
		String result = new String("PING! ");
		List<TransporterClient> transporters = transporters();
		Dialog.IO().debug("[     PING     ]  SIZE OF TRANSPORTERS:" + transporters.size());
    	for(TransporterClient client : transporters){
    		String response = client.ping();
    		Dialog.IO().debug("[     PING     ]  client's response: " + response);
    		result += response;
    	}
		Dialog.IO().debug("[     PING     ]  Returning ping response");
    	return result;
	}
	
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception,
			UnknownLocationFault_Exception,
			UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
		if(price < 0){
    		Dialog.IO().debug("requestTransport", "Price is lower than zero. Aborted");
    		throw new InvalidPriceFault_Exception("Price lower than zero: " + price, new InvalidPriceFault());
    	}
		
    	boolean originNotFound = true;
		boolean destinationNotFound = true;
		
		for(String s : NORTE){
			if(s.equals(origin)) originNotFound = false;
			if(s.equals(destination)) destinationNotFound = false;
		}
		for(String s : CENTRO){
			if(s.equals(origin)) originNotFound = false;
			if(s.equals(destination)) destinationNotFound = false;
		}
		for(String s : SUL){
			if(s.equals(origin)) originNotFound = false;
			if(s.equals(destination)) destinationNotFound = false;
		}

		if(originNotFound){
			Dialog.IO().debug("requestTransport", "Origin not found: " + origin);
			throw new UnknownLocationFault_Exception("Origin unknown: " + origin, new UnknownLocationFault());
		}
		if(destinationNotFound){
			Dialog.IO().debug("requestTransport", "Destination not found: " + destination);
			throw new UnknownLocationFault_Exception("Destination unknown: " + destination, new UnknownLocationFault());
		}
    	
		//in this moment the origin destination and price is valid
		//so we will look for an offer in the transporters
		Dialog.IO().debug("requestTransport", "Job is being requested to transporters");
		
		//Init TransportView
		Dialog.IO().debug("requestTransport", "Creating transportview");
		TransportView transport = new TransportView();
		
		Dialog.IO().debug("requestTransport", "Setting origin");
		transport.setOrigin(origin);
		Dialog.IO().debug("requestTransport", "Origin set");
		
		Dialog.IO().debug("requestTransport", "Setting destination");
		transport.setDestination(destination);
		Dialog.IO().debug("requestTransport", "Destination set");
		
		//Get all transporters available
		Dialog.IO().debug("requestTransport", "Getting transporters");
		List<TransporterClient> transporters = transporters();
		Dialog.IO().debug("requestTransport", "Got transporters");
		List<JobView> offers = new ArrayList<JobView>();
		
		//Get all offers from all transporters
		for(TransporterClient client : transporters){
			Dialog.IO().debug("requestTransport", "Iterating over transporters companies");
    		JobView j;
    		Dialog.IO().debug("requestTransport", "Requesting job to transporter");
			try {
	    		Dialog.IO().debug("requestTransport", "Connecting to client requesting job");
				j = client.requestJob(origin, destination, price);
				if(j == null){
					Dialog.IO().debug("requestTransport", "Job received is null");
					continue;
				}
				Dialog.IO().debug("requestTransport", "Job received and is not null");
			} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
				continue;
			}
			Dialog.IO().debug("requestTransport", "Adding returned job to offers");
    		offers.add(j);
    	}
		
		Dialog.IO().debug("requestTransport", "Setting state after getting offers");
		transport.setState(TransportStateView.REQUESTED);
		Dialog.IO().debug("requestTransport", "State set");
		
		if(offers.size() == 0){
			Dialog.IO().debug("requestTransport", "No offers received");
			throw new UnavailableTransportFault_Exception("There is no transporter available for this travel",
				new UnavailableTransportFault());
		}
		
		Dialog.IO().debug("requestTransport", "Sorting offers by price");
		//Sort offers by price
		Collections.sort(offers, new Comparator<JobView>(){
			public int compare(JobView j1, JobView j2){
				return j1.getJobPrice() - j2.getJobPrice();
			}
		});
		Dialog.IO().debug("requestTransport", "Offers sorted by price");
		
		Dialog.IO().debug("requestTransport", offers.size() + " available jobs");
		boolean accepted = false;
		
		Dialog.IO().debug("requestTransport", "Iterating over offers to accept one");
		for(JobView j : offers){
	    	JobView returnJobView;
			try {
				Dialog.IO().debug("requestTransport", j.getCompanyName());
				Dialog.IO().debug("requestTransport", j.getJobIdentifier());
				if(price < j.getJobPrice())
					throw new UnavailableTransportPriceFault_Exception("No price lower than client's request", new UnavailableTransportPriceFault());
				returnJobView = transporter(j.getCompanyName()).decideJob(j.getJobIdentifier(), true);
				if(returnJobView == null){
					Dialog.IO().debug("requestTransport", "TransporterClient return null on decideJob");
					continue;
				}
		    	
		    	if(returnJobView.getJobState() == JobStateView.ACCEPTED){
		    		j.setJobState(JobStateView.ACCEPTED);
		    		transport.setTransporterCompany(j.getCompanyName());
		    		transport.setPrice(j.getJobPrice());
		    		String id = getInstance().getNewId();
		    		_ids.put(id, new IDConverter(j.getJobIdentifier(), j.getCompanyName()));
		    		transport.setId(id);
		    		transport.setState(TransportStateView.BOOKED);
		    		Dialog.IO().debug("requestTransport", "Transport is now booked on the transport company");
		        	_transports.put(transport.getId(), transport);
		        	accepted = true;
		        	break;
		    	}
			} catch (BadJobFault_Exception | JAXRException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

	    	
		}
				
		//For all the other reject the job
		for(JobView j : offers){
			if(j.getJobState() != JobStateView.ACCEPTED)
				try {
					TransporterClient client = transporter(j.getCompanyName());
					if(client == null){
						Dialog.IO().debug("requestTransport", "TranporterClient returned is null");
						continue;
					}
					if(client.decideJob(j.getJobIdentifier(), false) == null){
						Dialog.IO().debug("decideJob", "TransporterClient returned null on reject job request");
					}
				} catch (BadJobFault_Exception | JAXRException e) {
					// TODO Auto-generated catch block
					//FIXME need to check return and retry if return is null
					//e.printStackTrace();
				}
		}

		if(!accepted){
			throw new UnavailableTransportFault_Exception("No job available", new UnavailableTransportFault());
		}
    	return transport.getId();
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
    	Dialog.IO().debug("viewTransport", "Getting transport object");
    	TransportView transport = _transports.get(id);
    	if(transport == null){ Dialog.IO().debug("viewTransport", "There is no such transport with this id " + id);
    	throw new UnknownTransportFault_Exception("No such job", new UnknownTransportFault()); }
    	if(transport.getState().value().equals("COMPLETED")){
    		Dialog.IO().debug("viewTransport", "Transport is completed so there is no need to check with transporter company");
    		return transport;
    	}
    	Dialog.IO().debug("viewTransport", "Transport is not complete, so broker will check updates with transporter company");
    	//Need to update job state from transporters info
    	try {
    		Dialog.IO().debug("viewTransport", "trying to get Transporter company");
			TransporterClient client = transporter(transport.getTransporterCompany());
			if(client == null){
				Dialog.IO().debug("viewTransport", "There is not a transporter company with this name: " + transport.getTransporterCompany());
				return null;
			}
			Dialog.IO().debug("viewTransport", "Will connect to transporter webService");
			
			String companyID = _ids.get(id).id();
			
			JobView job = client.jobStatus(companyID);
			Dialog.IO().debug("viewTransport", "Response received from transporter company");
			if(job.getJobState() == JobStateView.PROPOSED){
				transport.setState(TransportStateView.BUDGETED);
			}else if(job.getJobState() == JobStateView.ACCEPTED){
				transport.setState(TransportStateView.BOOKED);
			}else if(job.getJobState() == JobStateView.REJECTED){
				transport.setState(TransportStateView.FAILED);
			}else{
				transport.setState(TransportStateView.fromValue(job.getJobState().value()));
			}
			Dialog.IO().debug("viewTransport", "New JobState: " + job.getJobState().value());
			return transport;
		} catch (JAXRException e) {
			Dialog.IO().debug("viewTransport", "Some exception: " + e);
			//e.printStackTrace();
			return null;
		}
	}
	
	public List<TransportView> listTransports(){
		Dialog.IO().debug("listTransports", "Listing transports. There are " + _transports.size() + " jobs");
    	List<TransportView> list = new ArrayList<TransportView>();
    	for(TransportView transport : _transports.values()){
    		if(transport.getState().value().equals("COMPLETED")){
        		Dialog.IO().debug("viewTransport", "Transport is completed so there is no need to check with transporter company");
        		list.add(transport);
        		continue;
        	}
        	Dialog.IO().debug("viewTransport", "Transport is not complete, so broker will check updates with transporter company");
        	
        	try {
        		Dialog.IO().debug("viewTransport", "trying to get Transporter company");
    			TransporterClient client = transporter(transport.getTransporterCompany());
    			if(client == null){
    				Dialog.IO().debug("viewTransport", "There is not a transporter company with this name: " + transport.getTransporterCompany());
    				transport.setState(TransportStateView.FAILED);
        			list.add(transport);
        			Dialog.IO().debug("viewTransport", "Transporter company is not on UDDI anymore, so JOB will fail");
    				continue;
    			}
    			Dialog.IO().debug("viewTransport", "Will connect to transporter webService");
    			String companyID = _ids.get(transport.getId()).id();
    			
    			JobView job = client.jobStatus(companyID);
    			if(job == null){
    				Dialog.IO().debug("listTransports", "job received from company is null. companyID: " + companyID);
    				transport.setState(TransportStateView.FAILED);
    				continue;
    			}
    			Dialog.IO().debug("listTransports", "Response received from transporter company");
    			
    			if(job.getJobState() == JobStateView.PROPOSED){
    				transport.setState(TransportStateView.BUDGETED);
    			}else if(job.getJobState() == JobStateView.ACCEPTED){
    				transport.setState(TransportStateView.BOOKED);
    			}else if(job.getJobState() == JobStateView.REJECTED){
    				transport.setState(TransportStateView.FAILED);
    			}else{
    				transport.setState(TransportStateView.fromValue(job.getJobState().value()));
    			}
    			Dialog.IO().debug("listTransports", "New JobState: " + job.getJobState().value());
    			list.add(transport);
    		} catch (JAXRException e) {
    			Dialog.IO().debug("listTransports", "Some exception: " + e);
    		}
        	
    	}
    	return list;
	}
	
	/**
	 * This method clears the transports hash map
	 */
	public void clearTransports(){
		Dialog.IO().debug("clearTransports", "Cleaning transports hash map");
		getInstance()._transports = new HashMap<String, TransportView>();
		Dialog.IO().debug("clearTransports", "Transports hash map clean");
	}
	
	public void updateJob(String origin, String destination, int price, String id, String companyID, String companyName){
    	Dialog.IO().debug("updateJob", "Job is being updated by Master Broker");
    	//TODO
    }

	public void addSlave(String endpoint){
    	Dialog.IO().debug("addSlave", "addSlave");
		_brokerSlaves.add(endpoint);
		Dialog.IO().debug("addSlave", "Slave added with endpoind: " + endpoint);
    }
    
}
