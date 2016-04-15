package pt.upa.transporter.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

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
	TransporterManager transporter;
	String id;
	// initialization and clean-up for each test

	@Before
	public void setUp() {
		transporter = TransporterManager.getInstance();
	}

	@After
	public void tearDown() {
		transporter.clearJobs();
	}

	// tests

	@Test
	public void nullId() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		assertNull(transporter.jobStatus(null));
	}

	@Test
	public void wrongId() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		assertNull(transporter.jobStatus(id+"ola"));
	}
	
	@Test
	public void pickTheRightJobFromId() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		JobView job2 = transporter.requestJob("Lisboa", "Lisboa", 50);
		JobView job3= transporter.requestJob("Lisboa", "Lisboa", 50);
		JobView job4= transporter.requestJob("Lisboa", "Lisboa", 50);
		JobView job5= transporter.requestJob("Lisboa", "Lisboa", 50);
		
		String testedJobId = job2.getJobIdentifier();
		
		assertNotEquals(testedJobId, job3.getJobIdentifier());
		assertNotEquals(testedJobId, job4.getJobIdentifier());
		assertNotEquals(testedJobId, job5.getJobIdentifier());
		
		JobView updatedJob = transporter.jobStatus(testedJobId);
		assertEquals(testedJobId, updatedJob.getJobIdentifier());
	}
}