package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.registry.JAXRException;

import example.ws.handler.AuthenticationHandler;
import pt.upa.ui.Dialog;

public class TransporterManager {

	private String _uddiURL;
	private String _wsName;
	private String _wsURL;
	
	private TransporterPortType _port;
	private EndpointManager		_endpoint;
	
	private static TransporterManager _transporter;

	private List<TimerTask> _timerTasks;
	private int _numberOfJobs;
	private ListJobsResponse _jobs;
	private int _id;
	private String _companyName;
	private Random _random;
	private boolean _exit;
	
	private static final String[] NORTE = { "Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança" };
	private static final String[] CENTRO = { "Lisboa", "Leiria", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda" };
	private static final String[] SUL = { "Setúbal", "Évora", "Portalegre", "Beja", "Faro" };
	
	/**
	 * This method is used on Singleton design pattern
	 * 
	 * @return TransporterManager
	 */
	public static TransporterManager getInstance(){
		if(_transporter == null){
			Dialog.IO().debug("getInstance", "Singleton not initalized");
			Dialog.IO().debug("getInstance", "Creating instance of TransporterManager");
			_transporter = new TransporterManager();
			Dialog.IO().debug("getInstance", "TransporterManager created");
			_transporter._exit = false;
			Dialog.IO().debug("getInstance", "Exit flag is " + _transporter._exit);
			Dialog.IO().debug("getInstance", "Creating TransporterPort");
			_transporter._port = new TransporterPort();
			Dialog.IO().debug("getInstance", "TransporterPort created");

			Dialog.IO().debug("getInstance", "Creating Random");
			_transporter._random = new Random();
			Dialog.IO().debug("getInstance", "Random created");

			Dialog.IO().debug("getInstance", "Initializing TimerTask ArrayList");
			_transporter._timerTasks = new ArrayList<TimerTask>();
			Dialog.IO().debug("getInstance", "TimerTask ArrayList initialized");
			Dialog.IO().debug("getInstance", "Clearing jobs");
			getInstance().clearJobs();
			Dialog.IO().debug("getInstance", "Jobs cleared");
		}
		return _transporter;
	}
	
	/**
	 * Returns the exit flag
	 * 
	 * @return boolean true is application is closing
	 */
	public boolean exit(){ return _exit; }
	
	/**
	 * This constructor should not be used
	 * It will be called by getInstance on Singleton
	 * 
	 * That's why it's private
	 * 
	 * Do not make it public!!
	 */
	private TransporterManager(){}
	
	/**
	 * This constructor is used to create a new instance of the class
	 * It should be replaced by a new static method called setConfig
	 * @param uddiURL
	 * @param name
	 * @param url
	 * @param id
	 * @throws JAXRException
	 */
	public TransporterManager(String uddiURL, String name, String url, int id) throws JAXRException{		
		Dialog.IO().debug("TransporterManager", "Creating instance");
		
		Dialog.IO().trace("TransporterManager", "Setting uddiURL: " + uddiURL);
		getInstance()._uddiURL = uddiURL;
		
		Dialog.IO().trace("TransporterManager", "Setting WebService Name: " + name);
		getInstance()._wsName = name;
		
		Dialog.IO().trace("TransporterManager", "Setting company Name: " + name);
		getInstance()._companyName = name;
		
		Dialog.IO().debug("TransporterManager", "Creating new TransporterPort");
		getInstance()._port = new TransporterPort();
		Dialog.IO().debug("TransporterManager", "TransporterPort created");
		
		Dialog.IO().debug("TransporterManager", "Clearing jobs");
		getInstance().clearJobs();
		Dialog.IO().debug("TransporterManager", "Jobs cleared");
		
		Dialog.IO().trace("TransporterManager", "Setting id: " + id);
		getInstance()._id = id;
		Dialog.IO().trace("TransporterManager", "Setting WebService URL: " + url);
		getInstance()._wsURL = url;

		Dialog.IO().debug("TransporterManager", "Creating new EndpointManager with uddiURL: " + getInstance()._uddiURL);
		getInstance()._endpoint = new EndpointManager(getInstance()._uddiURL);
		Dialog.IO().debug("TransporterManager", "EndpointManager Created");
		Dialog.IO().debug("TransporterManager", "Created instance");
	}
	
	/**
	 * This method publishes the WebService on the UDDI Server
	 * @throws JAXRException
	 */
	public void publish() throws JAXRException{
		Dialog.IO().debug("publish", "Publishing WebService");
		
		if(getInstance()._endpoint == null){
			Dialog.IO().debug("publish", "EndpointManager is null. Creating a new EndpointManager with uddi " + getInstance()._uddiURL);
			//This should not happen, but just in case...
			getInstance()._endpoint = new EndpointManager(getInstance()._uddiURL);
			Dialog.IO().debug("publish", "EndpointManager created");
		}
			
		Dialog.IO().trace("publish", "WebService Name: " + getInstance()._wsName);
		Dialog.IO().trace("publish", "WebService URL: " + getInstance()._wsURL);
		getInstance()._endpoint.publish(getInstance()._port, getInstance()._wsName, getInstance()._wsURL);
		
		Dialog.IO().debug("publish", "Setting author on handle: " + getInstance()._wsURL);
		AuthenticationHandler.setAuthor(getInstance()._wsURL);
		Dialog.IO().debug("publish", "WebService published");
	}
	
	public void stop() throws JAXRException{
		Dialog.IO().debug("BrokerManager.stop", "Endpoint is going to unpublish");
		_exit = true;
		for(TimerTask t : _timerTasks){
			if(t != null){
				Dialog.IO().debug("stop", "Stopping timerTask: " + t.cancel());
				Dialog.IO().debug("stop", "TimerTask stopped");
			}
		}
		_endpoint.unpublish();

		Dialog.IO().debug("BrokerManager.stop", "Endpoint has unpublished WebService");
	}

	protected void removeTimer(TimerTask t){ _timerTasks.remove(t); }
	protected void addTimer(TimerTask t){ _timerTasks.add(t); }
	
	private String newJobIdentifier(String origin, String destination){
		return _companyName + new Integer(_numberOfJobs++).toString();
	}
	
	public void id(int id){ _id = id; }
	
	public void companyName(String companyName){ _companyName = companyName; }
	
	public String ping(String name){
		return new String(_companyName  + " is online!\t");
	}
	
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		{
			String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
			
			Dialog.IO().debug(methodName,"Job requested");
			
			if(origin == null){
				Dialog.IO().debug(methodName, "Origin string cannot be null");
				throw new BadLocationFault_Exception("Origin string is null", new BadLocationFault());
			}
			
			if(destination == null){
				Dialog.IO().debug(methodName, "Destination string cannot be null");
				throw new BadLocationFault_Exception("Destination string is null", new BadLocationFault());
			}
			
			boolean originNotFound = true; 
			boolean destinationNotFound = true;
			
			//Checks if Location is known
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
				Dialog.IO().debug(methodName, "Origin location not found on database: " + origin);
				throw new BadLocationFault_Exception("Origin unknown", new BadLocationFault());
			}
			if(destinationNotFound){
				Dialog.IO().debug(methodName, "Destination location not found on database: " + destination);
				throw new BadLocationFault_Exception("Destination unknown", new BadLocationFault());
			}

			originNotFound = true; 
			destinationNotFound = true;
			if(_id % 2 == 0){
				//Even transporters
				for(String s : NORTE){
					if(s.equals(origin)) originNotFound = false;
					if(s.equals(destination)) destinationNotFound = false;
				}
				for(String s : CENTRO){
					if(s.equals(origin)) originNotFound = false;
					if(s.equals(destination)) destinationNotFound = false;
				}
			}else{
				//Odd transporters
				for(String s : CENTRO){
					if(s.equals(origin)) originNotFound = false;
					if(s.equals(destination)) destinationNotFound = false;
				}
				for(String s : SUL){
					if(s.equals(origin)) originNotFound = false;
					if(s.equals(destination)) destinationNotFound = false;
				}
			}
			if(originNotFound){
				Dialog.IO().debug(methodName, "Transporter does not provide service for this origin: " + origin);
				throw new BadLocationFault_Exception("Transporter does not provide service for this origin: " + origin, new BadLocationFault());
			}
			if(destinationNotFound){
				Dialog.IO().debug(methodName, "Transporter does not provide service for this destination: " + destination);
				throw new BadLocationFault_Exception("Transporter does not provide service for this destination: " + destination, new BadLocationFault());
			}
			if(price < 0) return null;
			if(price > 100){
				Dialog.IO().debug(methodName, "Price is too high: " + price);
				return null;
			}
			JobView j = new JobView();
			_jobs.getReturn().add(j);
			j.setJobOrigin(origin);
			j.setJobDestination(destination);
			j.setCompanyName(_companyName);
			j.setJobState(JobStateView.PROPOSED);
			j.setJobIdentifier(newJobIdentifier(origin, destination));
			if(price < 2) j.setJobPrice(0);
			else if(price <= 10) j.setJobPrice(price - (_random.nextInt(price - 1) + 1));
			else{
				if(price % 2 == 0){
					if(_id % 2 == 0) j.setJobPrice(price - (_random.nextInt(price - 1) + 1));
					else j.setJobPrice(price + (_random.nextInt(price - 1) + 1));
				}else{
					if(_id % 2 == 0) j.setJobPrice(price + (_random.nextInt(price - 1) + 1));
					else j.setJobPrice(price - (_random.nextInt(price - 1) + 1));
				}
			}
			
			printRequest(j);
			return j;
		}
	}
	private void printRequest(JobView j){
		String id = j.getJobIdentifier();
		String origin = j.getJobOrigin();
		String destination = j.getJobDestination();
		int price = j.getJobPrice();
		String state = j.getJobState().value();
		Dialog.IO().trace("[JOB] ID: " + id + " O: " + origin + " D: " + destination + " P: " + price + " S: " + state);
	}

	public JobView decideJob( String id, boolean accept) throws BadJobFault_Exception {
		if(id == null) throw new BadJobFault_Exception("id can not be null", new BadJobFault());
		for(JobView job : _jobs.getReturn())
			if(job.getJobIdentifier().equals(id)){
				if(accept){
					job.setJobState(JobStateView.ACCEPTED);
					ChangeStatus timer = new ChangeStatus(job);
					timer.start();
					addTimer(timer);
					printRequest(job);
				}else{
					printRequest(job);
					job.setJobState(JobStateView.REJECTED);
				}
				return job;
			}
		throw new BadJobFault_Exception("Identifier job has not been found on database", new BadJobFault());
	}
	
	public JobView jobStatus(String id) {
		if(id == null) return null;
		for(JobView job : _jobs.getReturn())
			if(job.getJobIdentifier().equals(id)){
				JobView status = new ObjectFactory().createJobView();
				status.setCompanyName(job.getCompanyName());
				status.setJobDestination(job.getJobDestination());
				status.setJobIdentifier(job.getJobIdentifier());
				status.setJobOrigin(job.getJobOrigin());
				status.setJobPrice(job.getJobPrice());
				status.setJobState(job.getJobState());
				return status;
			}
		return null;
	}

	public List<JobView> listJobs(){
		ListJobsResponse response = new ObjectFactory().createListJobsResponse();
		for(JobView job : _jobs.getReturn()){
			JobView status = new ObjectFactory().createJobView();
			status.setCompanyName(job.getCompanyName());
			status.setJobDestination(job.getJobDestination());
			status.setJobIdentifier(job.getJobIdentifier());
			status.setJobOrigin(job.getJobOrigin());
			status.setJobPrice(job.getJobPrice());
			status.setJobState(job.getJobState());
			response.getReturn().add(status);
		}
		return response.getReturn();
	}

	public void clearJobs(){ _jobs = new ObjectFactory().createListJobsResponse(); }
	
	private class ChangeStatus extends TimerTask{
		private JobView _j;
		private Timer _timer;
		protected ChangeStatus(JobView j){
			Dialog.IO().debug("ChangeStatus", "ChangeStatus created");
			_j = j;
		}
		
		public void start(){
			_timer = new Timer();
			_timer.schedule(this, _random.nextInt(5000));
		}
		@Override
		public boolean cancel(){
			_timer.cancel();
			return super.cancel();
		}
		
		@Override
		public void run() {
			Dialog.IO().debug("ChangeStatus.run", "Changing status on a ChangeStatus object. State: " + _j.getJobState().value());
			
			if(_j.getJobState() == JobStateView.ACCEPTED){
				_j.setJobState(JobStateView.HEADING);
				printRequest(_j);
				getInstance().removeTimer(this);
				ChangeStatus c = new ChangeStatus(_j);
				c.start();
				if(!getInstance().exit()) getInstance().addTimer(c);
				if(getInstance().exit()) c.cancel();
			}else if(_j.getJobState() == JobStateView.HEADING){
				
				_j.setJobState(JobStateView.ONGOING);
				printRequest(_j);
				getInstance().removeTimer(this);
				ChangeStatus c = new ChangeStatus(_j);
				c.start();
				if(!getInstance().exit()) getInstance().addTimer(c);
				if(getInstance().exit()) c.cancel();
			}else if(_j.getJobState() == JobStateView.ONGOING){
				_j.setJobState(JobStateView.COMPLETED);
				printRequest(_j);
				getInstance().removeTimer(this);
			}
		}
		
		
	}
}
