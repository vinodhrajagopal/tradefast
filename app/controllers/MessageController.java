package controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.mvc.Http.RequestBody;

public class MessageController extends Controller{

	//@BodyParser.Of(BodyParser.Json.class)
	//public static Result sendMessage(Long postId, Long threadId) {
	public static Result sendMessage() {
		//RequestBody body = request().body();
		DynamicForm form = form().bindFromRequest();
		String message = form.get("message");
		ObjectNode result = Json.newObject();
		if(message == null) {
			result.put("status", "KO");
			result.put("message", "Missing parameter [name]");
			return badRequest(result);
		} else {
			result.put("status", "OK");
			result.put("message", "Hello " + message);
			return ok(result);
		}		
	}
}
