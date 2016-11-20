package pt.andred.sirs1617.ws.cli;

public class NotFenixClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotFenixClientException() {
    }

    public NotFenixClientException(String message) {
        super(message);
    }

    public NotFenixClientException(Throwable cause) {
        super(cause);
    }

    public NotFenixClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
