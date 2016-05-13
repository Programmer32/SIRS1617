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

			Dialog.IO().debug("getInstance", "Creating new TransporterPort");
			getInstance()._port = new TransporterPort();
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
	 *
	 * @param uddiURL
	 * @param name
	 * @param url
	 * @param id
	 * @throws TransporterManagerException
	 * @throws JAXRException
	 */
	public static void config(String uddiURL, String name, String url, int id) throws TransporterManagerException, JAXRException {
		Dialog.IO().debug("TransporterManager", "Creating instance");

		Dialog.IO().trace("TransporterManager", "Setting uddiURL: " + uddiURL);
		if(getInstance()._uddiURL != null) throw new TransporterManagerException("UDDI URL is already defined");
		getInstance()._uddiURL = uddiURL;

		Dialog.IO().trace("TransporterManager", "Setting WebService Name: " + name);
		if(getInstance()._wsName != null) throw new TransporterManagerException("WebService Name is already defined");
		getInstance()._wsName = name;

		Dialog.IO().trace("TransporterManager", "Setting WebService URL: " + url);
		if(getInstance()._wsURL != null) throw new TransporterManagerException("WebService URL is already defined");
		getInstance()._wsURL = url;

		Dialog.IO().trace("TransporterManager", "Setting company Name: " + name);
		if(getInstance()._companyName != null) throw new TransporterManagerException("Company Name is already defined");
		getInstance()._companyName = name;

		Dialog.IO().debug("TransporterManager", "Clearing jobs");
		getInstance().clearJobs();
		Dialog.IO().debug("TransporterManager", "Jobs cleared");

		Dialog.IO().trace("TransporterManager", "Setting id: " + id);
		getInstance()._id = id;

		Dialog.IO().debug("TransporterManager", "Creating new EndpointManager with uddiURL: " + getInstance()._uddiURL);
		if(getInstance()._endpoint != null) throw new TransporterManagerException("EndpointManager is already defined");
		getInstance()._endpoint = new EndpointManager(getInstance()._uddiURL);
		Dialog.IO().debug("TransporterManager", "EndpointManager Created");

		AuthenticationHandler.setUDDI_URL(uddiURL);

		Dialog.IO().debug("TransporterManager", "Created instance");
	}


	/**
	 *
	 * @param id
	 */
	public static void id(int id){ getInstance()._id = id; }

	/**
	 *
	 * @param name
	 */
	public static void companyName(String name){ getInstance()._companyName = name; }

	/**
	 * This method publishes the WebService on the UDDI Server
	 * @throws JAXRException
	 */
	public static void publish() throws JAXRException{
		Dialog.IO().debug("publish", "Publishing WebService");

		if(getInstance()._endpoint == null){
			Dialog.IO().debug("publish", "EndpointManager is null. Creating a new EndpointManager with uddi " + getInstance()._uddiURL);
			//This should not happen, but just in case...
			getInstance()._endpoint = new EndpointManager(getInstance()._uddiURL);
			Dialog.IO().debug("publish", "EndpointManager created");
		}

		Dialog.IO().trace("publish", "WebService Name: " + getInstance()._wsName);
		Dialog.IO().trace("publish", "WebService URL : " + getInstance()._wsURL);
		getInstance()._endpoint.publish(getInstance()._port, getInstance()._wsName, getInstance()._wsURL);

		Dialog.IO().debug("publish", "Setting author on handle: " + getInstance()._wsName);
		AuthenticationHandler.setAuthor(getInstance()._wsName);
		Dialog.IO().debug("publish", "WebService published");
	}

	/**
	 * This method unpublishes the WebService of the UDDI Server
	 * Cancels the Timers running
	 *
	 * @throws JAXRException
	 */
	public static void stop() throws JAXRException{
		Dialog.IO().debug("BrokerManager.stop", "Endpoint is going to unpublish");
		getInstance()._exit = true;
		for(TimerTask t : getInstance()._timerTasks){
			if(t != null){
				Dialog.IO().debug("stop", "Stopping timerTask: " + t.cancel());
				Dialog.IO().debug("stop", "TimerTask stopped");
			}
		}
		getInstance()._endpoint.unpublish();

		Dialog.IO().debug("BrokerManager.stop", "Endpoint has unpublished WebService");
	}

	/**
	 * Delete timer from arraylist
	 *
	 * @param t
	 */
	protected void removeTimer(TimerTask t){ _timerTasks.remove(t); }

	/**
	 * Adds a TimerTask to the arrayList
	 *
	 * @param t
	 */
	protected void addTimer(TimerTask t){ _timerTasks.add(t); }

	/**
	 * This method should generate a new unique JobIdentifier
	 *
	 * @return
	 */
	private static String newJobIdentifier(){
		return getInstance()._companyName + new Integer(getInstance()._numberOfJobs++).toString();
	}

	/**
	 * This method is called by TransporterPort
	 *
	 * @param name
	 * @return
	 */
	protected String ping(String name){
		return new String(_companyName  + " is online!");
	}

	/**
	 * This method returns a proposal price for the job asked
	 *
	 * @param origin
	 * @param destination
	 * @param price
	 * @return JobView
	 * @throws BadLocationFault_Exception if origin or destination is unknown
	 * @throws BadPriceFault_Exception if the price is lower than zero
	 */
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		{

			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(),"Job requested");

			if(origin == null){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Origin string cannot be null");
				throw new BadLocationFault_Exception("Origin string is null", new BadLocationFault());
			}

			if(destination == null){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Destination string cannot be null");
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
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Origin location not found on database: " + origin);
				throw new BadLocationFault_Exception("Origin unknown", new BadLocationFault());
			}
			if(destinationNotFound){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Destination location not found on database: " + destination);
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
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Transporter does not provide service for this origin: " + origin);
				throw new BadLocationFault_Exception("Transporter does not provide service for this origin: " + origin, new BadLocationFault());
			}
			if(destinationNotFound){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Transporter does not provide service for this destination: " + destination);
				throw new BadLocationFault_Exception("Transporter does not provide service for this destination: " + destination, new BadLocationFault());
			}
			if(price < 0){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price is too low: " + price);
				throw new BadPriceFault_Exception("negative price", new BadPriceFault());
			}
			if(price > 100){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price is too high: " + price);
				return null;
			}
			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Job is valid.");

			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Creating a new JobView");
			JobView j = new JobView();
			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Add JobView to List");
			_jobs.getReturn().add(j);
			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Setting origin: " + origin);
			j.setJobOrigin(origin);
			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Setting destination: " + destination);
			j.setJobDestination(destination);

			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Setting Company Name: " + getInstance()._companyName);
			j.setCompanyName(_companyName);

			JobStateView jobStateView = JobStateView.PROPOSED;
			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Setting State: " + jobStateView);
			j.setJobState(jobStateView);

			String id = newJobIdentifier();
			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Setting JobIdentifier: " + id);
			j.setJobIdentifier(id);


			Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Computing price");
			if(price < 2){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price is lower than two and higher or equal to zero. Price will be zero.");
				j.setJobPrice(0);
			}
			else if(price <= 10){
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price <= 10, so Price = Price - random(price-1) + 1.");
				int random = (_random.nextInt(price - 1) + 1);
				int finalPrice = (price - random);
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Random: " + random + " Final price: " + finalPrice);
				j.setJobPrice(finalPrice);
			}
			else{
				Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price > 10");
				if(price % 2 == 0){
					Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price is even");
					if(_id % 2 == 0){
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "CompanyIdentifier is even");
						int random = (_random.nextInt(price - 1) + 1);
						int finalPrice = (price - random);
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Random: " + random + " Final price: " + finalPrice);
						j.setJobPrice(finalPrice);
					}
					else{
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "CompanyIdentifier is odd");
						int random = (_random.nextInt(price - 1) + 1);
						int finalPrice = (price + random);
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Random: " + random + " Final price: " + finalPrice);
						j.setJobPrice(finalPrice);
					}
				}else{
					Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Price is odd");
					if(_id % 2 == 0){
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "CompanyIdentifier is even");
						int random = (_random.nextInt(price - 1) + 1);
						int finalPrice = (price + random);
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Random: " + random + " Final price: " + finalPrice);
						j.setJobPrice(finalPrice);
					}
					else{
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "CompanyIdentifier is odd");
						int random = (_random.nextInt(price - 1) + 1);
						int finalPrice = (price - random);
						Dialog.IO().debug(new Object(){}.getClass().getEnclosingMethod().getName(), "Random: " + random + " Final price: " + finalPrice);
						j.setJobPrice(finalPrice);
					}
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
				if(job.getJobState() != JobStateView.PROPOSED)
					throw new BadJobFault_Exception("Duplicated job", new BadJobFault());
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
			_timer.schedule(this, _random.nextInt(4000) + 1000);
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
