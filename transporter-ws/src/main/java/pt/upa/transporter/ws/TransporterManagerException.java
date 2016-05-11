package pt.upa.transporter.ws;

public class TransporterManagerException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6153262826251056296L;

	protected TransporterManagerException(String reason){
		super(reason);
	}
	
	protected TransporterManagerException(){
		super();
	}
}
