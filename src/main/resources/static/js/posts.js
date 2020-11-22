jQuery().ready(function(){
    $('.edit-post .pin-post').click(pinPost);
    $('.edit-post .unpin-post').click(unpinPost);
    $('.edit-post .delete-post').click(deletePost);
    $('.edit-post .modify-post').click(function () {
      const postId = $(this).closest('.postid-holder').attr("postid");
      const locationId = $(this).closest('.post-info').find('span.location-info').first().attr("locationid");
      const privacy = $(this).closest('.post-info').find('span.privacy-info').first().attr("privacy");
      const text = $(this).closest('.post-info').find('p.post-text').first().text();

      $('#edit-post-form input[name="postid"]').val(postId);
      $('#edit-post-form textarea[name="text"]').val(text.trim());
      $('#edit-post-form select[name="privacy"] option[value='+ privacy +']').attr('selected','selected');
      $('#edit-post-form select[name="location"] option[value='+ locationId +']').attr('selected','selected');

      $('#edit-modal').modal('toggle');
    });

    $('#edit-post-form').submit(editPost);
});

function editPost(e) {
    e.preventDefault();

    const postId = $('#edit-post-form input[name="postid"]').val();
    const formData = {
      text: $('#edit-post-form textarea[name="text"]').val() || '',
      privacy: $('#edit-post-form select[name="privacy"]').find('option:selected').val(),
      location: $('#edit-post-form select[name="location"]').find('option:selected').val()
    };

    $.ajax({
      type: "put",
      data: formData,
      url: `/posts/${postId}`,
      dataType: "json",
      success: function(result){
        console.log("result ", result);
        showSuccess("Post Updated!");
        $('#edit-modal').modal('toggle');
        location.reload();
      },
      error: handleError
    });
}

function pinPost() {
  const postId = $(this).closest('.postid-holder').attr("postid");
  $.ajax({
    type: "post",
    data: "",
    url: `/posts/${postId}/pin`,
    dataType: "",
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
    dataType: "",
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
    dataType: "",
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

function handleError(xhr, textStatus, error) {
  console.log("An error occured: " + xhr.status + " " + xhr.statusText);
  console.log(xhr);

  Swal.fire({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: false,
    icon: 'error',
    title: xhr.responseText
      ? xhr.responseText
      : xhr.status === 403
        ? 'Access Denied!'
        :  `${xhr.status} - No detail Message Found!`
  });
}