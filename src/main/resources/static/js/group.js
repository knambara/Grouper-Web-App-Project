function updateGroupContent(email) {
  const $groupSize = $('#group-detail-size');
  const $groupMembers = $('#group-members');

  let num = Number($groupSize.html()) + 1; 
  $groupSize.html(num.toString());
  $groupMembers.html( $groupMembers.html() + "<br />" + email);
}

$(document).ready(() => {

  const $end_button = $('#end-button');

  $end_button.on('click', event => {
    // Send mod email to backend
    const postParameter = {mod: localStorage.getItem("grouper_email")};

    // Deletes group mod is in and returns the URL corresponding to
    // that group, which the user is sent to
    $.post("/deleteGroup", postParameter, responseJSON => {

        const responseObject = JSON.parse(responseJSON);
        const msg = responseObject.msg;
        console.log(msg);
    });

    // Call function in websockets.js
    update_dash();
    // Redirect current user's page to dashboard
    redirectToDashboard();
  });

  if(window.location.href.split('?')[0] === "http://localhost:4567/grouper/group"){
    console.log("commencing update group");
    update_group(localStorage.getItem("grouper_email"));
  }

});