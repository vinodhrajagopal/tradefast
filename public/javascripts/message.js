function sendMessage(postId, threadId) {
	var msgBoxId = "msgbox-" + postId;
	if(threadId != null) {
		msgBoxId += "-" + threadId;
	}
	var box = $("#"+msgBoxId);
	var errorMessageDiv = $('#error-'+msgBoxId);
	if (errorMessageDiv != null) {
		errorMessageDiv.remove();
	}
	//You should disable the 'Send' link to avoid double posting 
	jsRoutes.controllers.MessageController.sendMessage().ajax({
		data : {
			"postId": postId,
			"threadId": threadId,
			"message": box.val()
		},
		success : function(data) {
			box.val('');
			box.parent().before(data);
		},
		error : function(jqXHR) {
			var errorNode = $('<div>').attr('id','error-'+msgBoxId).attr('class','errorMessage').append(jqXHR.responseText);
			box.after(errorNode);
		}
	});
}