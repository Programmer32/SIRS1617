package pt.upa.transporter;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		String _uddiURL = args[0];
		String _wsName = args[1];
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		System.out.println("UDDI URL: " + _uddiURL);
		System.out.println("WS NAME:  " + _wsName);

		try {
			TransporterClient client = new TransporterClient(_uddiURL, _wsName);
			System.out.println("PING: " + client.ping());

			JobView j = client.requestJob("Lisboa", "Lisboa", 53);
			if(j == null) System.out.println("No job returned");
			else System.out.println("Price" + j.getJobPrice());
			for(JobView job : client.listJobs()){
				System.out.println("Transporter: " + job.getCompanyName() + " " + job.getJobOrigin() + " " + job.getJobDestination() + " " + job.getJobPrice() + " ");
			}
			if(client.listJobs().size() == 0)
				System.out.println("There are no jobs.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
