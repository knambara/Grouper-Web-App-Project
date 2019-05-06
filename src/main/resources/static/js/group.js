
// Updates the HTML for number of members and members' emails
function updateGroupContent(email, img) {
  const $groupSize = $('#groupSize');
  const $groupMembers = $('#group-members');
  console.log("update content");
  if (document.getElementById(email)) {
    let num = Number($groupSize.html()) - 1;
    $groupSize.html(num.toString());
    var line = $(document.getElementById(email));
    line.remove();
    var circle = $(document.getElementById(img));
    circle.remove();
  } else {
    console.log($groupSize.html());
    let num = Number($groupSize.html()) + 1;
    $groupSize.html(num.toString());
    $groupMembers.html( $groupMembers.html() + "<div id='group-user-info'><img class = 'circle-image small' id='" + img + "' src='" + img + "'/>  <p id='" + email + "'>" + email + "</p></div>");
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
  const $toggle_switch = $('#myToggle');

//   $('#group-duration').change(function() {
//     const time = $('#group-duration').html();
//     console.log(time);
//     if (time.equals("0 hr 0 min remaining")) {
//         // Send mod email to backend
//         const postParameter = {
//         mod: localStorage.getItem("grouper_email"),
//         hash: getUserSession().hash
//         };

//         // Deletes group mod is in and returns the URL corresponding to
//         // that group, which the user is sent to
//         $.post("/deleteGroup", postParameter, responseJSON => {

//             const responseObject = JSON.parse(responseJSON);
//             const msg = responseObject.msg;

//             if (msg == "success") {
//             // Call function in websockets.js
//             remove_group(localStorage.getItem("gid"));
//             // Redirect all other users' page to dashboard
//             redirect_all(localStorage.getItem("gid"));

//             // Redirect current user's page to dashboard and reset gid
//             localStorage.setItem("gid", "-1");
//             localStorage.setItem("isModerator", false);
//             redirectToDashboard();
//             } else {
//             alert("Error deleting group.");
//             }
//         });
//     }
//   });

  // When invisible switch is changed
  $toggle_switch.change(function() {
        if(this.checked) {
            // Invisible mode on
            const postParameter = {invisibility: "on", gid : localStorage.getItem("gid")};
            $.post("/invisibility", postParameter, responseJSON => {
                const responseObject = JSON.parse(responseJSON);
                const msg = responseObject.msg;
                console.log("invisible on " + msg);
            });
            // Call function in websocket.js
            remove_group(localStorage.getItem("gid"));
        } else {
            // Invisible mode off
            const postParameter = {invisibility: "off", gid : localStorage.getItem("gid")};
            $.post("/invisibility", postParameter, responseJSON => {
                const responseObject = JSON.parse(responseJSON);
                const msg = responseObject.msg;
                console.log("invisible off " + msg);
            });
            update_dash();
        }
    });

  // When the moderator ends a group
  $end_button.on('click', event => {

    // Send mod email to backend
    const postParameter = {
      mod: localStorage.getItem("grouper_email"),
      hash: getUserSession().hash
    };

    // Deletes group mod is in and returns the URL corresponding to
    // that group, which the user is sent to
    $.post("/deleteGroup", postParameter, responseJSON => {

        const responseObject = JSON.parse(responseJSON);
        const msg = responseObject.msg;

        if (msg == "success") {
          // Call function in websockets.js
          remove_group(localStorage.getItem("gid"));
          // Redirect all other users' page to dashboard
          redirect_all(localStorage.getItem("gid"));

          // Redirect current user's page to dashboard and reset gid
          localStorage.setItem("gid", "-1");
          localStorage.setItem("isModerator", false);
          redirectToDashboard();
        } else {
          alert("Error deleting group.");
        }

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
      update_group(localStorage.getItem("grouper_email"), localStorage.getItem("gid"), localStorage.getItem("grouper_img"));

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
      // Call function in websockets.js
      reload_all(getUserSession().gid);
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
        update_group(localStorage.getItem("grouper_email"), gid, localStorage.getItem("grouper_img"));
      }
    }
  }

  // Timer that calculates time remaining every second based on current time and end time
  function updateDuration() {
      const endTime = new Date($('#end-time').html());
      const currTime = new Date();
      const timeDiff = endTime.getTime() - currTime.getTime();
      const totalMins = timeDiff/ 60000;

      // If totalMins = 0, delete group and redirect all
      if (totalMins < 0 && document.getElementById("group-option-end")) {
        const postParameter = {
        mod: localStorage.getItem("grouper_email"),
        hash: getUserSession().hash
        };
        // Deletes group mod is in and returns the URL corresponding to
        // that group, which the user is sent to
        $.post("/deleteGroup", postParameter, responseJSON => {

            const responseObject = JSON.parse(responseJSON);
            const msg = responseObject.msg;

            if (msg == "success") {
            // Call function in websockets.js
            remove_group(localStorage.getItem("gid"));
            // Redirect all other users' page to dashboard
            redirect_all(localStorage.getItem("gid"));

            // Redirect current user's page to dashboard and reset gid
            localStorage.setItem("gid", "-1");
            localStorage.setItem("isModerator", false);
            redirectToDashboard();
            } else {
            alert("Error deleting group.");
            }
        });
      }
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
