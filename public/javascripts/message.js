function sendMessage(postId, threadId) {
	var msgBoxId = "msgBox-" + postId;
	if(threadId != null) {
		msgBoxId += "-" + threadId;
	}
	var box = $("#"+msgBoxId);
	var errorMessageDiv = $('#error-'+msgBoxId);
	if (errorMessageDiv != null) {
		errorMessageDiv.remove();
	}
	
	var msgData = {
			"postId": postId,
			"message":box.val()
				};
	if (threadId != null) {
		msgData["threadId"] = threadId;
	}
	//TODO: You should disable the 'Send' link to avoid double posting 
	jsRoutes.controllers.MessageController.sendMessage().ajax({
		data : msgData,
		success : function(data) {
			box.parent().before(data);
			if(threadId != null) {
				box.val('');
			} else {
				box.parent().remove();//This is the first message for this post
			}
		},
		error : function(jqXHR) {
			var errorNode = $('<div>').attr('id','error-'+msgBoxId).attr('class','errorMessage').append(jqXHR.responseText);
			box.parent().append(errorNode);
		}
	});
}