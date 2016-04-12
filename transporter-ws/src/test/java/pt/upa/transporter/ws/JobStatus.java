package pt.upa.transporter.ws;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class JobStatus {

	// static members


	// one-time initialization and clean-up

	@BeforeClass
	public static void oneTimeSetUp() {

	}

	@AfterClass
	public static void oneTimeTearDown() {

	}


	// members
	TransporterPort transporter;
	String id;
	// initialization and clean-up for each test

	@Before
	public void setUp() {
		transporter = new TransporterPort();
	}

	@After
	public void tearDown() {
		transporter = null;
	}

	// tests
	@Test
	public void listJobs() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.requestJob("Lisboa", "Lisboa", 50);
		transporter.requestJob("Lisboa", "Lisboa", 50);
		transporter.requestJob("Lisboa", "Lisboa", 50);
		
		ArrayList<JobView> lista = (ArrayList<JobView>) transporter.listJobs();
		assertEquals(3,lista.size());
	}
	@Test
	public void clearJobs() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.requestJob("Lisboa", "Lisboa", 50);
		transporter.requestJob("Lisboa", "Lisboa", 50);
		transporter.requestJob("Lisboa", "Lisboa", 50);
		
		transporter.clearJobs();
		
		ArrayList<JobView> lista = (ArrayList<JobView>) transporter.listJobs();
		assertEquals(0,lista.size());
	}
}