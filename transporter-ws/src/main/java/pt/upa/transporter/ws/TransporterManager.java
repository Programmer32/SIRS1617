package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.registry.JAXRException;

import pt.upa.ui.Dialog;

public class TransporterManager {

	private String _uddiURL;
	private String _wsName;
	private String _wsURL;
	
	private TransporterPortType _port;
	private EndpointManager		_endpoint;
	
	private static TransporterManager _transporter;

	private List<Timer> _timers;
	private List<TimerTask> _timerTasks;
	private int _numberOfJobs;
	private ListJobsResponse _jobs;
	private int _id;
	private String _companyName;
	private Random _random;
	
	
	private static final String[] NORTE = { "Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança" };
	private static final String[] CENTRO = { "Lisboa", "Leiria", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda" };
	private static final String[] SUL = { "Setúbal", "Évora", "Portalegre", "Beja", "Faro" };
	
	public static TransporterManager getInstance(){
		if(_transporter == null){
			_transporter = new TransporterManager();
			_transporter._port = new TransporterPort();
			_transporter._random = new Random();
			_transporter._timers = new ArrayList<Timer>();
			_transporter._timerTasks = new ArrayList<TimerTask>();
			getInstance().clearJobs();
		}
		return _transporter;
	}
	
	private TransporterManager(){}
	
	public TransporterManager(String uddiURL, String name, String url, int id) throws JAXRException{
		this();
		Dialog.IO().debug(this.getClass().getSimpleName(), "Creating instance");
		getInstance()._uddiURL = uddiURL;
		getInstance()._wsName = name;
		getInstance()._companyName = name;
		getInstance()._port = new TransporterPort();
		getInstance().clearJobs();
		getInstance()._id = id;
		getInstance()._wsURL = url;
		getInstance()._endpoint = new EndpointManager(getInstance()._uddiURL);
		Dialog.IO().debug(this.getClass().getSimpleName(), "Created instance");
	}
	public void publish() throws JAXRException{
		getInstance()._endpoint.publish(getInstance()._port, getInstance()._wsName, getInstance()._wsURL);
	}
	public void stop() throws JAXRException{
		Dialog.IO().debug("BrokerManager.stop", "Endpoint is going to unpublish");
		for(TimerTask t : _timerTasks){
			if(t != null){
				Dialog.IO().debug("stop", "Stopping timerTask: " + t.cancel());
				Dialog.IO().debug("stop", "TimerTask stopped");
			}
		}
		for(Timer t : _timers){
			if(t != null){
				Dialog.IO().debug("stop", "Stopping timer: " + t.purge());
				t.cancel();
				Dialog.IO().debug("stop", "Timer stopped");
			}
		}
		_endpoint.unpublish();

		Dialog.IO().debug("BrokerManager.stop", "Endpoint has unpublished WebService");
	}
	protected void addTimer(Timer t, TimerTask tt){
		_timers.add(t);
		_timerTasks.add(tt);
	}
	private String newJobIdentifier(String origin, String destination){
		return _companyName + origin + destination + new Integer(_numberOfJobs++).toString();
	}
	
	public void id(int id){ _id = id; }
	
	public void companyName(String companyName){ _companyName = companyName; }
	
	public String ping(String name){
		return new String(_companyName  + " is online\n");
	}
	
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		{
			System.out.println("Job requested");
			if(origin == null){
				Dialog.IO().debug("requestJob", "Origin string cannot be null");
				throw new BadLocationFault_Exception("Origin string is null", new BadLocationFault());
			}
			if(destination == null){
				Dialog.IO().debug("requestJob", "Destination string cannot be null");
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
				System.out.println("Origin location not found on database: " + origin);
				throw new BadLocationFault_Exception("Origin unknown", new BadLocationFault());
			}
			if(destinationNotFound){
				System.out.println("Destination location not found on database: " + destination);
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
				throw new BadLocationFault_Exception("Transporter does not provide service for this origin: " + destination, new BadLocationFault());
			}
			if(destinationNotFound){
				throw new BadLocationFault_Exception("Transporter does not provide service for this destination: " + destination, new BadLocationFault());
			}
			if(price > 100){
				System.out.println("Price is too high: " + price);
				return null;
			}
			if(price < 2){
				System.out.println("Price is too low: " + price);
				throw new BadPriceFault_Exception("Price is too low", new BadPriceFault());
			}
			JobView j = new JobView();
			_jobs.getReturn().add(j);
			j.setJobOrigin(origin);
			j.setJobDestination(destination);
			j.setCompanyName(_companyName);
			j.setJobState(JobStateView.PROPOSED);
			j.setJobIdentifier(newJobIdentifier(origin, destination));
			
			if(price <= 10) j.setJobPrice(price - (_random.nextInt(price - 1) + 1));
			else{
				if(price % 2 == 0){
					if(_id % 2 == 0) j.setJobPrice(price - (_random.nextInt(price - 1) + 1));
					else j.setJobPrice(price + (_random.nextInt(price - 1) + 1));
				}else{
					if(_id % 2 == 0) j.setJobPrice(price + (_random.nextInt(price - 1) + 1));
					else j.setJobPrice(price - (_random.nextInt(price - 1) + 1));
				}
			}
			
			printRequest(j.getCompanyName(), j.getJobIdentifier(), j.getJobOrigin(), j.getJobDestination(), j.getJobPrice(),j.getJobState().value());
			return j;
		}
	}
	private void printRequest(String companyName, String id, String origin, String destination, int price, String state){
		System.out.println("[JOB] CompanyName: " + companyName + " ID: " + id + " Origin: " + origin + " Destination: " + 
				destination + " Price: " + price + " Estado: " + state);
	}

	public JobView decideJob( String id, boolean accept) throws BadJobFault_Exception {
		if(id == null) throw new BadJobFault_Exception("id can not be null", new BadJobFault());
		for(JobView job : _jobs.getReturn())
			if(job.getJobIdentifier().equals(id)){
				if(accept){
					job.setJobState(JobStateView.ACCEPTED);
					Timer timer = new Timer();
					TimerTask task = new ChangeStatus(job);
					timer.schedule(task, _random.nextInt(5000));
				}else{
					job.setJobState(JobStateView.REJECTED);
				}
				return job;
			}
		throw new BadJobFault_Exception("Identifier job has not been found on database", new BadJobFault());
	}
	
	public JobView jobStatus(String id){
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
		private TimerTask _task;
		protected ChangeStatus(JobView j){ _j = j; }
		@Override
		public void run() {
			if(_j.getJobState() == JobStateView.ACCEPTED){
				_j.setJobState(JobStateView.HEADING);
			}else if(_j.getJobState() == JobStateView.HEADING){
				_j.setJobState(JobStateView.ONGOING);
			}else if(_j.getJobState() == JobStateView.ONGOING){
				_j.setJobState(JobStateView.COMPLETED);
			}
			if(_j.getJobState() != JobStateView.COMPLETED && _j.getJobState() != JobStateView.REJECTED){
				_timer = new Timer();
				_task = new ChangeStatus(_j);
				getInstance().addTimer(_timer, _task);
				_timer.schedule(_task, _random.nextInt(5000));
			}
		}
		
		
	}
}
