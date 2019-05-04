
// Updates the HTML for number of members and members' emails
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

  // Start timer to check for time updates
  updateDuration();

  const $end_button = $('#end-button');
  const $leave_button = $('#leave-button');
  const $extend_button = $('#extend-button');

  const $extend_apply_button = $('#extend-time-apply');
  const $extend_exit_area = $('#extend-cancel');

  // When the moderator ends a group
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
        localStorage.setItem("isModerator", false);
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

  $extend_button.on('click', event => {
    $("#group-extend-overlay").css("display", "inline");
    $("#group-extend-container").css("display", "inline");
  });

  $extend_exit_area.on('click', event => {
    $("#group-extend-overlay").css("display", "none");
    $("#group-extend-container").css("display", "none");
  });

  $extend_apply_button.on('click', event => {
    const postParameter = {
      hash: getUserSession().hash,
      group: getUserSession().gid,
      duration_hours: $("#extend-duration-hours").val(),
      duration_mins: $("#extend-duration-mins").val(),
    };
    $.post("/extendGroup", postParameter, responseJSON => {
      const responseObject = JSON.parse(responseJSON);

      if (responseObject.status != "success") {
        alert("Couldn't extend the group.");
      }
    });
    $("#group-extend-overlay").css("display", "none");
    $("#group-extend-container").css("display", "none");
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

  // Timer that calculates time remaining every second based on current time and end time
  function updateDuration() {
      const endTime = new Date($('#end-time').html());
      const currTime = new Date();
      const timeDiff = endTime.getTime() - currTime.getTime();
      const totalMins = timeDiff/ 60000;
      displayTime(totalMins);
      t = setTimeout(updateDuration,1000);
  }

  // Redirect to dashboard when button is clicked
  $('#back-to-dashboard').on('click', event => {
      redirectToDashboard();
  });

});

// Converts total numbers of minutes remaining to proper format and adds to HTML
function displayTime(totalMins) {
    const hours = Math.floor(totalMins / 60);
    const mins = Math.floor(totalMins - hours * 60);
    $('#group-duration').html(hours + " hr " + mins + " min remaining");
}
