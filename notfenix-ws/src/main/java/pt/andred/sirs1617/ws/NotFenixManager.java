package pt.andred.sirs1617.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.registry.JAXRException;
import javax.xml.ws.WebServiceException;

import example.ws.handler.AuthenticationHandler;
import pt.andred.sirs1617.ui.Dialog;

public class NotFenixManager {

	private static final int TIMEOUT = 50;
	private static final int WAITING_TIME = TIMEOUT * 5;


	private String _uddiURL;
	private String _wsName;
	private String _wsURL;
	private Random _random;

	private NotFenixPort _port;
	private EndpointManager _endpoint;

	private static NotFenixManager _instance;

	public static NotFenixManager getInstance(){
		if(_instance == null){
			_instance = new NotFenixManager();
			_instance._port = new NotFenixPort();
			_instance._random = new Random();
		}
		return _instance;
	}

	private NotFenixManager(){}

	public NotFenixManager(
			String uddiURL,
			String name,
			String url,
			EndpointManager endpointManager)
				throws JAXRException{
		
		Dialog.IO().debug(this.getClass().getSimpleName(), "Creating instance");

		EndpointManager endpoint = new EndpointManager(uddiURL);

		getInstance()._uddiURL = uddiURL;
		getInstance()._wsName = name;
		getInstance()._wsURL = url;
		getInstance()._endpoint = endpointManager;

		Dialog.IO().debug(this.getClass().getSimpleName(), "Created instance");
	}

	/**
	 * This method unpublishes the WebService of the UDDI Server
	 * Cancels the Timers running
	 *
	 * @throws JAXRException
	 */
	public void stop() throws JAXRException{
		Dialog.IO().debug("BrokerManager.stop", "Endpoint is going to unpublish");
		getInstance()._endpoint.unpublish();
		Dialog.IO().debug("BrokerManager.stop", "Endpoint has unpublished WebService");
	}


	public String ping(String name){
		return new String("PING!");
    }
	
	public boolean login(String name){
		return true;
	}


}
