package pt.upa.transporter.ws.it;

import org.junit.*;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.*;

import javax.xml.registry.JAXRException;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class TransporterClientTest {

    // static members

	
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

    }

    @After
    public void tearDown() {
    }


    // Test the data received from server like origin, destination.
    @Test
    public void test1(){
    	try {
    		TransporterClient client = new TransporterClient("http://localhost:9090","UpaTransporter1");
        	JobView j = client.requestJob("Leiria", "Lisboa", 40);
        	assertEquals("Leiria",j.getJobOrigin());
        	assertEquals("Lisboa",j.getJobDestination());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}