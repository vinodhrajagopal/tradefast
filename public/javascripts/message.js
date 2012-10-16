function sendMessage(postId, threadId) {
	var msgBoxId = "msg-" + postId;
	if(threadId != null) {
		msgBoxId += "-" + threadId;
	}
	var msg = document.getElementById(msgBoxId).value
	jsRoutes.controllers.MessageController.sendMessage().ajax({
		data : {
			"postId": postId,
			"threadId": threadId,
			"message": msg
		},
		success : function(data) {
		},
		error : function(data) {
		}
	});
}