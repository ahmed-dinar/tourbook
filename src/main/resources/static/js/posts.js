jQuery().ready(function(){
    $('.edit-post .pin-post').click(pinPost);
    $('.edit-post .unpin-post').click(unpinPost);
    $('.edit-post .delete-post').click(deletePost);
});

function pinPost() {
  const postId = $(this).closest('.postid-holder').attr("postid");
  $.ajax({
    type: "post",
    data: "",
    url: `/posts/${postId}/pin`,
    dataType: "json",
    success: function(result){
      console.log("result ", result);
      showSuccess("Post Pinned!");
      location.reload();
    },
    error: handleError
  });
}

function unpinPost() {
  const postId = $(this).closest('.postid-holder').attr("postid");
  $.ajax({
    type: "post",
    data: "",
    url: `/posts/${postId}/unpin`,
    dataType: "json",
    success: function(result){
      console.log("result ", result);
      showSuccess('Post UnPinned!');
      location.reload();
    },
    error: handleError
  });
}

function deletePost() {
  const postId = $(this).closest('.postid-holder').attr("postid");
  $.ajax({
    type: "delete",
    url: `/posts/${postId}`,
    dataType: "json",
    success: function(result){
      console.log("result ", result);
      showSuccess('Post Removed!');
      location.reload();
    },
    error: handleError
  });
}

function showSuccess(title) {
  Swal.fire({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: false,
    icon: 'success',
    title: title
  });
}

function handleError(xhr, textStatus,error) {
  console.log("An error occured: " + xhr.status + " " + xhr.statusText);
  console.log(xhr);
  console.log(error);

  Swal.fire({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: false,
    icon: 'error',
    title: xhr.status === 403
      ? 'Access Denied!'
      :  `${xhr.status}`
  });
}