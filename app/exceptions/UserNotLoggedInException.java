package exceptions;

public class UserNotLoggedInException extends Exception {
	private final static String ERROR_MESSAGE = "You need to login to perform this operation";

	@Override
	public String getMessage() {
		return ERROR_MESSAGE;
	}
}
