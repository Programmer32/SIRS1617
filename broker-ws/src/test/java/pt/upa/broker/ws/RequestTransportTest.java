package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class RequestTransportTest {

	private static  BrokerManager broker;


	Map<String,Object> contextMap = null;

	// one-time initialization and clean-up

	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}


	// members


	// initialization and clean-up for each test

	@Before
	public void setUp() {
		//BrokerManager _broker = BrokerManager.getInstance();
		contextMap = new HashMap<String,Object>();
		broker = BrokerManager.getInstance();
		//_broker.requestTransport(origin, destination, price)
	}

	@After
	public void tearDown() {
		contextMap = null;
		broker.clearTransports();
	}


	// tests

	@Test(expected=InvalidPriceFault_Exception.class)
	public void price_minor_0() throws InvalidPriceFault_Exception, UnknownLocationFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
		broker.requestTransport("Lisboa", "Lisboa", -1);
	}
	public void price_equal_0() throws InvalidPriceFault_Exception, UnknownLocationFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
		broker.requestTransport("Lisboa", "Lisboa", 0);
	}
	public void price_equal_50() throws InvalidPriceFault_Exception, UnknownLocationFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
		String ola = broker.requestTransport("Lisboa", "Lisboa", 0);
		System.out.println("\100B[33m"+ola);
	}
	@Test(expected=UnknownLocationFault_Exception.class)
	public void unkwown_dest() throws InvalidPriceFault_Exception, UnknownLocationFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
		broker.requestTransport("Lisboa", "olaEscoNDido", 25);
	}
	@Test(expected=UnknownLocationFault_Exception.class)
	public void unkwown_or() throws InvalidPriceFault_Exception, UnknownLocationFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
		broker.requestTransport("olaEscoNDido","Lisboa",  25);
	}
}