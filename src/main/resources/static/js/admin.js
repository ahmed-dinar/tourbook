jQuery().ready(function(){
  console.log("loaded");

  $('#add-loc-btn').click(function () {
    $('#addloc-modal').modal('toggle');
  });

  $('.loc-edit-btn').click(editLocation);
  $('.loc-visible-btn').click(editLocation);

  $('#addloc-form').submit(function (e) {
    e.preventDefault();

    const formData = {
      name: $('#addloc-form input[name="name"]').val()
    };

    $.ajax({
      type: "post",
      data: formData,
      url: `/admin/location`,
      dataType: "",
      success: function(result){
        console.log("result ", result);
        showSuccess("Location Added!");
        $('#addloc-modal').modal('toggle');
        location.reload();
      },
      error: handleError
    });
  });
});


function  editLocation(e) {
  e.preventDefault();

  const locationId = $(this).closest('tr.loc-info').attr("locid");
  const formData = {
    name: $(this).closest('tr.loc-info').find('input.location-name').first().val(),
    visible: $(this).hasClass('loc-visible-btn')
        ? $(this).closest('tr.loc-info').find('td.isactive').first().attr("isactive") !== 'true'
        : $(this).closest('tr.loc-info').find('td.isactive').first().attr("isactive") === 'true'
  };

  console.log("formData ", formData);
  console.log("locationId ", locationId);

  $.ajax({
    type: "post",
    data: formData,
    url: `/admin/location/${locationId}`,
    dataType: "",
    success: function(result){
      console.log("result ", result);
      showSuccess("Location Update!");
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