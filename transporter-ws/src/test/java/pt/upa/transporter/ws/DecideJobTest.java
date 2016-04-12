package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

import javax.xml.bind.annotation.XmlSchemaType;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class DecideJobTest {

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
	JobView job;
	String id;
	
	// initialization and clean-up for each test

	@Before
	public void setUp() {
		try {
			transporter = new TransporterPort();
			job = transporter.requestJob("Lisboa", "Lisboa", 50);
			id = job.getJobIdentifier();
		} catch (BadLocationFault_Exception | BadPriceFault_Exception e) {
			fail("Test Set Up failed");
		}
	}

	@After
	public void tearDown() {
		transporter = null;
		job = null;
	}


	// tests

	/* ********************************************************************** */
	/* **************************** JobStatus ******************************* */
	@Test
	public void jobStatusAccepted() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		JobView updatedJob = transporter.decideJob(id, true);
		
		JobStateView state = updatedJob.getJobState();
		assertEquals(JobStateView.ACCEPTED,state);
	}
	
	@Test
	public void jobStatusRejected() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		JobView updatedJob = transporter.decideJob(id, false);
		
		JobStateView state = updatedJob.getJobState();
		assertEquals(JobStateView.REJECTED,state);
	}

	@Test
	public void nullId() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		assertNull(transporter.decideJob(null, false));
	}

	@Test
	public void wrongId() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		assertNull(transporter.decideJob(id+"ola", false));
	}
	
	@Test
	public void pickTheRightJobFromId() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		JobView job2 = transporter.requestJob("Lisboa", "Lisboa", 50);
		JobView job3= transporter.requestJob("Lisboa", "Lisboa", 50);
		JobView job4= transporter.requestJob("Lisboa", "Lisboa", 50);
		JobView job5= transporter.requestJob("Lisboa", "Lisboa", 50);
		
		String testedJobId = job.getJobIdentifier();
		
		assertNotEquals(testedJobId, job2.getJobIdentifier());
		assertNotEquals(testedJobId, job3.getJobIdentifier());
		assertNotEquals(testedJobId, job4.getJobIdentifier());
		assertNotEquals(testedJobId, job5.getJobIdentifier());
		
		JobView updatedJob = transporter.decideJob(testedJobId, true);
		assertEquals(testedJobId, updatedJob.getJobIdentifier());
	}
}
