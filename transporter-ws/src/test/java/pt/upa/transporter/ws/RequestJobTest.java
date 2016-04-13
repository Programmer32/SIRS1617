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
public class RequestJobTest {

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


	/* ********************************************************************** */
	/* **************************** JobPrice ******************************** */

	/*FIXME: Par e impar?*/
	@Test(expected = BadPriceFault_Exception.class)
	public void refPriceOver100() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Lisboa", "Lisboa", 101));
	}

	/*FIXME: Par e impar?*/
	@Test
	public void refPriceEquals10() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 10);
		int price = job.getJobPrice();
		boolean cond = price < 10;
		assert(cond);
	}
	@Test
	public void refPriceEquals9() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 9);
		int price = job.getJobPrice();
		boolean cond = price < 9;
		assert(cond);
	}	
	@Test
	public void refPriceEquals2() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 2);
		int price = job.getJobPrice();
		boolean cond = price < 2;
		assert(cond);
	}
	@Test(expected = BadPriceFault_Exception.class)
	public void refPriceMinor2() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Lisboa", "Lisboa", 1));
	}

	@Test
	public void refPriceEquals100Par() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(0);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 100);
		int price = job.getJobPrice();
		boolean cond = price < 100;
		assert(cond);
	}
	@Test
	public void refPriceEquals100Impar() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(1);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 100);
		int price = job.getJobPrice();
		boolean cond = price > 100;
		assert(cond);
	}
	@Test
	public void refPriceEquals11Par() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(0);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 11);
		int price = job.getJobPrice();
		boolean cond = price > 11;
		assert(cond);
	}
	@Test
	public void refPriceEquals11Impar() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(1);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 11);
		int price = job.getJobPrice();
		boolean cond = price < 11;
		assert(cond);
	}

	@Test
	public void refPriceEquals50Par() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(0);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 50);
		int price = job.getJobPrice();
		boolean cond = price < 50;
		assert(cond);
	}
	@Test
	public void refPriceEquals50Impar() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(1);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 50);
		int price = job.getJobPrice();
		boolean cond = price > 50;
		assert(cond);
	}
	@Test
	public void refPriceEquals51Par() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(0);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 51);
		int price = job.getJobPrice();
		boolean cond = price > 51;
		assert(cond);
	}
	@Test
	public void refPriceEquals51Impar() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporter.id(1);
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 51);
		int price = job.getJobPrice();
		boolean cond = price < 51;
		assert(cond);
	}

	/* **************************** JobPrice ******************************** */
	/* ********************************************************************** */
	@Test(expected = BadLocationFault_Exception.class)
	public void outOfBoundsOriginImpar() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(1);
		assertNull(transporter.requestJob("Porto", "Setúbal", 51));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void outOfBoundsDestinationImpar() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(1);
		assertNull(transporter.requestJob("Setúbal", "Porto", 51));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void outOfBoundsOriginPar() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(0);
		assertNull(transporter.requestJob("Setúbal","Porto", 51));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void outOfBoundsDestinationPar() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(0);
		assertNull(transporter.requestJob("Porto","Setúbal", 51));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void unknownDestination() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(0);
		assertNull(transporter.requestJob("Lisboa","ola", 51));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void unknownOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(0);
		assertNull(transporter.requestJob("","Lisboa", 51));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void nullOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception{
		transporter.id(0);
		assertNull(transporter.requestJob(null,"Lisboa", 51));
	}
	@Test
	public void jobViewDetails() throws BadLocationFault_Exception, BadPriceFault_Exception {
		String company = "nomeComp";
		String origin  = "Braga";
		String destination = "Leiria";

		transporter.id(0);
		transporter.companyName(company);
		JobView job = transporter.requestJob(origin, destination, 51);
		String companyName      = job.getCompanyName();
		//String jobIdentifier    = job.getJobIdentifier();
		String jobOrigin        = job.getJobOrigin();
		String jobDestination   = job.getJobDestination();

		assertEquals("company name",company, companyName);
		assertEquals("job origin",origin, jobOrigin);
		assertEquals("job destination",destination, jobDestination);
		//assertEquals("job Id",company, companyName);
	}

	/* ********************************************************************** */
	/* **************************** JobStatus ******************************* */
	@Test
	public void jobStatusInitial() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 50);
		JobStateView state = job.getJobState();
		assertEquals(JobStateView.PROPOSED,state);
	}
}