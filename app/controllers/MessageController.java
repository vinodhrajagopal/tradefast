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
import views.html.tags.threadListItem;

public class MessageController extends Controller{
	private static final String INTERNAL_SERVER_ERROR = "We are currently unable to send your message. Please try after sometime";
	public static Result sendMessage() {
		try {
			DynamicForm form = form().bindFromRequest();
			Long postId = parseLong(form.get("postId"));
			Long threadId = parseLong(form.get("threadId"));
			Message newMessage = Message.create(postId, threadId, form.get("message"));
			String newMessageNode = messageListItem.render(newMessage).body();
			//TODO: There is more to this.. if this is the first message of a thread, you have to re-render the threadlist
			if (threadId == null) {
				newMessage.thread.messages.add(newMessage);
				String newThreadNode = threadListItem.render(newMessage.thread, false).body();
				return created(newThreadNode);
			}
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
	
	private static Long parseLong(String val) {
		return val == null ? null : Long.parseLong(val);
	}
}