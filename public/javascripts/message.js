function sendMessage(postId, threadId) {
	jsRoutes.controllers.MessageController.sendMessage(postId, threadId).ajax({
		success : function(data) {
		},
		error : function(data) {
		}
	});
}