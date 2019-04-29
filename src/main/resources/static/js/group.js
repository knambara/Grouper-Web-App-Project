function updateGroupContent(email) {
  const $groupSize = $('#group-detail-size');
  const $groupMembers = $('#group-members');

  if (document.getElementById(email)) {
    let num = Number($groupSize.html()) - 1;
    $groupSize.html(num.toString());
    $('#' + email, this).remove();
  } else {
    let num = Number($groupSize.html()) + 1; 
    $groupSize.html(num.toString());
    $groupMembers.html( $groupMembers.html() + "<p id='" + email + "'>" + email + "</p>");
  }
}

$(document).ready(() => {

  const $end_button = $('#end-button');
  const $leave_button = $('#leave-button');

  $end_button.on('click', event => {
    // Send mod email to backend
    const postParameter = {mod: localStorage.getItem("grouper_email")};

    // Deletes group mod is in and returns the URL corresponding to
    // that group, which the user is sent to
    $.post("/deleteGroup", postParameter, responseJSON => {

        const responseObject = JSON.parse(responseJSON);
        const msg = responseObject.msg;
        console.log(msg);

        // Call function in websockets.js
        update_dash();    
        // Redirect all other users' page to dashboard
        redirect_all(localStorage.getItem("gid"));

        // Redirect current user's page to dashboard and reset gid
        localStorage.setItem("gid", "-1");
        redirectToDashboard(); 
    });
  });

  $leave_button.on('click', event => {
    // Send user email to backend
    const postParameter = {user : localStorage.getItem("grouper_email"), group : localStorage.getItem("gid")};
    $.post("/leaveGroup", postParameter, responseJSON => {
      const responseObject = JSON.parse(responseJSON);
      const msg = responseObject.msg;
      console.log(msg);
      console.log("Commencing update group.");
      update_group(localStorage.getItem("grouper_email"));

      // Redirect current user's page to dashboard and reset gid
      localStorage.setItem("gid", "-1");
      redirectToDashboard(); 
    });
  });

  if(window.location.href.split('?')[0] === "http://localhost:4567/grouper/group"){
    setup_live_groups();
    conn.onopen = function() {
      const urlParams = new URLSearchParams(window.location.search);
      const gid = urlParams.get('gid');
      // Save groupID in local storage
      localStorage.setItem("gid", gid);
      console.log("Commencing update group.");
      update_group(localStorage.getItem("grouper_email"));
    }
  }

});