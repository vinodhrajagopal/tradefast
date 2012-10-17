package exceptions;

public class MessageCannotBeNullOrEmptyException extends Exception {
	public final static String ERROR_MESSAGE = "Message cannot be empty";

	@Override
	public String getMessage() {
		return ERROR_MESSAGE;
	}

}
