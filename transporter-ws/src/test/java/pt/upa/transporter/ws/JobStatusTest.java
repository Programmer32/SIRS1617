package pt.upa.transporter.ws;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class JobStatusTest {

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

}