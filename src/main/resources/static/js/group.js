function updateGroupContent(email) {
  const $groupSize = $('#groupSize');
  const $groupMembers = $('#group-members');

  if (document.getElementById(email)) {
    let num = Number($groupSize.html()) - 1;
    $groupSize.html(num.toString());
    $('#' + email, this).remove();
  } else {
    console.log($groupSize.html());
    let num = Number($groupSize.html()) + 1;
    $groupSize.html(num.toString());
    $groupMembers.html( $groupMembers.html() + "<p id='" + email + "'>" + email + "</p>");
  }
}

$(document).ready(() => {

  let totalMins = $('#group-duration').html() * 60;
  console.log(totalMins);
  updateDuration();


  const $end_button = $('#end-button');
  const $leave_button = $('#leave-button');
  console.log(localStorage.getItem("gid"));

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

  // If group page is invoked, check if the session is newly joining or not
  if(window.location.href.split('?')[0] === "http://localhost:4567/grouper/group"){
    // Function in websockets.js
    setup_live_groups();
    conn.onopen = function() {
      // Make dynamic changes to group page across clients only if newly joined
      // i.e. don't make any changes if user is refreshing page
      console.log(localStorage.getItem("gid"));
      if (localStorage.getItem("gid") === "-1") {
        console.log("inhere");
        const urlParams = new URLSearchParams(window.location.search);
        const gid = urlParams.get('gid');
        // Save new groupID in local storage
        localStorage.setItem("gid", gid);
        console.log("Commencing update group.");
        // Function in websockets.js
        update_group(localStorage.getItem("grouper_email"));
      }
    }
  }

  // Timer for duration countdown
  function updateDuration() {
      displayTime(totalMins);
      totalMins = totalMins - 1;
      t = setTimeout(updateDuration,60000);
  }

});

// Converts total numbers of minutes remaining to proper format and adds to HTML
function displayTime(totalMins) {
    const hours = Math.floor(totalMins / 60);
    const mins = totalMins - hours * 60;
    $('#group-duration').html(hours + " hr " + mins + " min remaining");
}
