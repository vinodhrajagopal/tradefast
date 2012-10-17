function sendMessage(postId, threadId) {
	var msgBoxId = "#msgbox-" + postId;
	if(threadId != null) {
		msgBoxId += "-" + threadId;
	}
	var box = $(msgBoxId);
	var msg = box.val();
	jsRoutes.controllers.MessageController.sendMessage().ajax({
		data : {
			"postId": postId,
			"threadId": threadId,
			"message": msg
		},
		success : function(data) {
			var messageId = data.messageId;
			var threadId = data.threadId;
			$(msgBoxId).val('');
			var newMessageListItem =  $('<li>').addClass('messageContainer').append(
												$('<div>').addClass('messageUserPhoto').append(
														$('<a>').append(
																$('<img>').addClass('userPhotoSmall').attr('src','/assets/images/default_profile_photo.jpeg')))).append(
												$('<div>').addClass('messageContent').append(
														$('<div>').addClass('messageBody').append(
																$('<a>').append("UserName")).append(
																$('<span>').append(msg))).append(
														$('<div>').addClass('messageFooter').append("Created Time")));
			box.parent().before(newMessageListItem);
		},
		error : function(data) {
			//TODO: Show the error message below the textbox.
		}
	});
}