package controllers;

import models.Message;
import org.codehaus.jackson.node.ObjectNode;

import exceptions.CannotCreateThreadForOwnPostException;
import exceptions.CannotUpdateNonParticipatingThread;
import exceptions.MessageCannotBeNullOrEmptyException;
import exceptions.PostCannotBeNullException;
import exceptions.UserNotLoggedInException;

import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class MessageController extends Controller{

	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String MESSAGE_ID = "messageId";
	private static final String THREAD_ID = "threadId";

	public static Result sendMessage() {
		DynamicForm form = form().bindFromRequest();
		Long postId = Long.parseLong(form.get("postId"));
		Long threadId = Long.parseLong(form.get("threadId"));
		String message = form.get("message");
		ObjectNode result = Json.newObject();
		try {
			Message newMessage = Message.create(postId, threadId, message);
			result.put(MESSAGE_ID, newMessage.id);
			result.put(THREAD_ID, newMessage.thread.id);
			return ok(result);
		} catch (UserNotLoggedInException e) {
			result.put(ERROR_MESSAGE, e.getMessage());
			return badRequest(result);
		} catch (MessageCannotBeNullOrEmptyException e) {
			result.put(ERROR_MESSAGE, e.getMessage());
			return badRequest(result);
		} catch (CannotUpdateNonParticipatingThread e) {
			result.put(ERROR_MESSAGE, e.getMessage());
			return badRequest(result);
		} catch (PostCannotBeNullException e) {
			result.put(ERROR_MESSAGE, e.getMessage());
			return badRequest(result);
		} catch (CannotCreateThreadForOwnPostException e) {
			result.put(ERROR_MESSAGE, e.getMessage());
			return badRequest(result);
		} catch (Exception e) {
			result.put(ERROR_MESSAGE, e.getMessage());
			return badRequest(result);
		}
	}
}
