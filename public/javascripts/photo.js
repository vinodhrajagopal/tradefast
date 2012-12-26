function deletePhoto(postId, photoId) {
	var confirmDelete = confirm("Are you sure you want to delete this photo ?");
	if (!confirmDelete) {
		return;
	}
	var photoContainerId = "photoContainer-" + photoId;
	var photoContainer = $("#"+photoContainerId);
	
	var photoData = {
			"postId": postId,
			"photoId":photoId
			};	
	
	jsRoutes.controllers.PostController.deletePhoto().ajax({
		data : photoData,
		success : function(data) {
			photoContainer.remove();
		},
		error : function(jqXHR) {
			var errorNode = $('<div>').attr('id','error-'+photoContainerId).attr('class','errorMessage').append(jqXHR.responseText);
			photoContainer.append(errorNode);
		}
	});	
}