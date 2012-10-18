package controllers;

import models.Message;

import exceptions.CannotCreateThreadForOwnPostException;
import exceptions.CannotUpdateNonParticipatingThread;
import exceptions.MessageCannotBeNullOrEmptyException;
import exceptions.PostCannotBeNullException;
import exceptions.UserNotLoggedInException;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.tags.messageListItem;

public class MessageController extends Controller{
	private static final String INTERNAL_SERVER_ERROR = "We are currently unable to send your message. Please try after sometime";
	public static Result sendMessage() {
		DynamicForm form = form().bindFromRequest();
		try {
			Message newMessage = Message.create(Long.parseLong(form.get("postId")), Long.parseLong(form.get("threadId")), form.get("message"));
			String newMessageNode = messageListItem.render(newMessage).body();
			return created(newMessageNode);
		} catch (UserNotLoggedInException e) {
			return unauthorized(e.getMessage());
		} catch (MessageCannotBeNullOrEmptyException e) {
			return badRequest(e.getMessage());
		} catch (PostCannotBeNullException e) {
			return badRequest(e.getMessage());
		} catch (CannotUpdateNonParticipatingThread e) {
			return forbidden(e.getMessage());
		}  catch (CannotCreateThreadForOwnPostException e) {
			return forbidden(e.getMessage());
		} catch (Exception e) {
			return internalServerError(INTERNAL_SERVER_ERROR);
		}
	}
}