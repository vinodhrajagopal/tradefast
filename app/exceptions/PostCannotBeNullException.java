package exceptions;

public class PostCannotBeNullException extends Exception {
	public final static String ERROR_MESSAGE = "Post cannot be null";
	@Override
	public String getMessage() {
		return ERROR_MESSAGE;
	}
}
