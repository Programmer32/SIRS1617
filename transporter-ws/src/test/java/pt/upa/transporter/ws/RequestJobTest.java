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
	private TransporterManager transporter;

	// one-time initialization and clean-up

	@BeforeClass
	public static void oneTimeSetUp() {

	}

	@AfterClass
	public static void oneTimeTearDown() {

	}
	
	// members

	// Initialisation and clean-up for each test

	@Before
	public void setUp() {
		transporter = TransporterManager.getInstance();
	}

	@After
	public void tearDown() {
		transporter.clearJobs();
	}


	@Test(expected = BadLocationFault_Exception.class)
	public void nullParamsTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob(null, null, 0));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void nullOriginTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob(null, "Lisboa", 0));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void nullDestinationTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Lisboa", null, 0));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void UnknownOriginTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Madrid", "Lisboa", 0));
	}
	@Test(expected = BadLocationFault_Exception.class)
	public void UnknownDestinationTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Lisboa", "Madrid", 0));
	}

	@Test(expected = BadPriceFault_Exception.class)
	public void negativePriceTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Lisboa", "Lisboa", -1));
	}
	@Test
	public void overPricedTest() throws BadLocationFault_Exception, BadPriceFault_Exception {
		assertNull(transporter.requestJob("Lisboa", "Lisboa", 100 + 1));
	}
	@Test
	public void price10Test() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 10);
		int price = job.getJobPrice();
		boolean cond = price < 10;
		assert(cond);
	}
	@Test
	public void price9Test() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 9);
		int price = job.getJobPrice();
		boolean cond = price < 9;
		assert(cond);
	}	
	@Test
	public void price2Test() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 2);
		int price = job.getJobPrice();
		boolean cond = price < 2;
		assert(cond);
	}
	@Test(expected = BadPriceFault_Exception.class)
	public void price1Test() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 1);
		assert(job.getJobPrice() == 0);
	}
	@Test(expected = BadPriceFault_Exception.class)
	public void price0Test() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView job = transporter.requestJob("Lisboa", "Lisboa", 0);
		assert(job.getJobPrice() == 0);
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