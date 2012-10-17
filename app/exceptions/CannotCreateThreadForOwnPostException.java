package exceptions;

public class CannotCreateThreadForOwnPostException extends Exception {
	private final static String ERROR_MESSAGE = "You cannot update a thread which you have not initiated";
	
	@Override
	public String getMessage() {
		return ERROR_MESSAGE;
	}

}
