package pt.upa.broker.ws;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.registry.JAXRException;

import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

public class ViewTransportTest {

	private BrokerPortType brokerPortType;
	@Mocked
	private UDDINaming mockUDDI;
	@Mocked
	private EndpointManager mockEndpointMgr;
	private BrokerManager brokerManager;
	@Mocked
	private TransporterClient mockClient;
	@Mocked
	private JobView mockJob;
	private List<TransporterClient> fakeTransporters;

	@Before
	public void setUp() throws JAXRException {
		brokerPortType = new BrokerPort();
	}

	@Test
	public void happyPath() throws JAXRException, InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, UnknownTransportFault_Exception {

		fakeTransporters = new ArrayList<TransporterClient>();
		fakeTransporters.add(mockClient);
		
		new Expectations() {
			{
				mockEndpointMgr.transporters();
				result = fakeTransporters;
				mockClient.requestJob("Lisboa", "Porto", 50);
				result = mockJob;
				mockJob.getJobPrice();
				result = 25;
				mockJob.getCompanyName();
				result = "fakeCompany";
				mockJob.getJobIdentifier();
				result = "xpto";
				mockEndpointMgr.transporter("fakeCompany");
				result = mockClient;
				mockJob.getJobState();
				result = JobStateView.ACCEPTED;
				mockJob.setJobState(JobStateView.ACCEPTED);
			}
		};

		brokerManager = new BrokerManager("mockURL", "mockName", "mockUrl", mockEndpointMgr);

		brokerPortType.requestTransport("Lisboa", "Porto", 50);
		
		List<TransportView> bookedTransports = brokerPortType.listTransports();
		assertEquals(1, bookedTransports.size());
		TransportView actualTransportView = bookedTransports.get(0);
		
		new Verifications() {{
			assertEquals("Lisboa", actualTransportView.getOrigin());
			assertEquals("Porto", actualTransportView.getDestination());
			assertEquals(Integer.valueOf(25), actualTransportView.getPrice());
			assertEquals("fakeCompany", actualTransportView.getTransporterCompany());
			assertEquals(TransportStateView.BOOKED, actualTransportView.getState());
		}};
	}

	@Test(expected = UnavailableTransportFault_Exception.class)
	public void noTransports() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, JAXRException,
			BadLocationFault_Exception, BadPriceFault_Exception {

		new Expectations() {
			{
				mockEndpointMgr.transporters();
				result = new ArrayList<TransporterClient>();;
			}
		};

		brokerManager = new BrokerManager("mockURL", "mockName", "mockUrl", mockEndpointMgr);

		brokerPortType.requestTransport("Lisboa", "Porto", 50);
	}

}
