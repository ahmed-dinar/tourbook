var fetching;

/**
 * TourBook Posts
 */
$(document).ready(function() {
  if (isPostsPage()) {
    getPosts();
    $(window).scroll(function () {
      if (!hasContent()) {
        if ($("#no-more").length < 1) {
          $("#posts-section").append('<div id="no-more" class="h6 mt-4" style="text-align: center; color: #8f8d88">End of content</div>');
        }
      } else if (fetching === false && isPageBottom()) {
        getPosts();
      }
    });
  }

  $(document).on('click', '.edit-post .pin-post', pinPost);
  $(document).on('click', '.edit-post .unpin-post', unpinPost);
  $(document).on('click', '.edit-post .delete-post', deletePost);
  $(document).on('click', '.edit-post .modify-post', setModalContent);
  $('#edit-post-form').submit(editPost);
});

/**
 * Check if is in bottom of the page while scrolling
 * @returns {boolean}
 */
function isPageBottom () {
  return $(window).scrollTop() >= $(document).height() - $(window).height() - 20;
}

/**
 * will be replaced with regex may be
 * @returns {boolean}
 */
function isPostsPage() {
  const url = window.location.pathname;
  return url === '' || url === '/' || (url.includes("users") && url.split("/").length === 3);
}

/**
 * Toggle ajax loader
 * @param show
 */
function toggleLoader (show = true) {
  if ($('#post-loader').is(':visible') && !show) {
    $('#post-loader').hide();
  } else if (!$('#post-loader').is(':visible') && show) {
    $('#post-loader').show();
  }
}

/**
 * Check if any more content available to fetch in infinite scroll
 * @returns {jQuery|*|boolean}
 */
function hasContent() {
  const info = $(".pagination-info").last();
  return info && info.length && $(info).attr("is-last") === "false";
}

/**
 * Set modal content for edit before opening the modal
 */
function setModalContent() {
  const postId = $(this).closest('.postid-holder').attr("postid");
  const locationId = $(this).closest('.post-info').find('span.location-info').first().attr("locationid");
  const privacy = $(this).closest('.post-info').find('span.privacy-info').first().attr("privacy");
  const text = $(this).closest('.post-info').find('p.post-text').first().text();

  $('#edit-post-form input[name="postid"]').val(postId);
  $('#edit-post-form textarea[name="text"]').val(text.trim());
  $('#edit-post-form select[name="privacy"] option[value='+ privacy +']').attr('selected','selected');
  $('#edit-post-form select[name="location"] option[value='+ locationId +']').attr('selected','selected');

  $('#edit-modal').modal('toggle');
}

function getPosts() {
  const info = $(".pagination-info").last();
  const  page = info && info.length ? parseInt(info.attr("current-page"), 10) + 1 : 1;
  const username =  window.location.pathname.split("/").pop();
  console.log("page ", page);

  $.ajax({
    type: "get",
    url: `/posts?page=${page}` + (username ? `&username=${username}` : ''),
    dataType: "",
    beforeSend: function () {
      fetching = true;
      toggleLoader();
    },
    success: function(result){
      console.log("getting post success");
      $("#posts-section").append(result);
    },
    error: handleError,
    complete: function () {
      fetching = false;
      toggleLoader(false);
    }
  });
}

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
        $("#posts-section").html('');
        getPosts();
      },
      error: handleError
    });
}

function pinPost(done) {
  const postId = $(this).closest('.postid-holder').attr("postid");
  $.ajax({
    type: "post",
    data: "",
    url: `/posts/${postId}/pin`,
    dataType: "",
    success: function(result){
      console.log("result ", result);
      showSuccess("Post Pinned!");
      $("#posts-section").html('');
      getPosts();
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
      $("#posts-section").html('');
      getPosts();
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
      $("#posts-section").html('');
      getPosts();
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