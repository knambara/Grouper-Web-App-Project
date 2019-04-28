$(document).ready(() => {

  const $end_button = $('#end_button');

  $end_button.on('click', event => {
    // Send mod email to backend
    const postParameter = {mod: localStorage.getItem("grouper_email")};

    // Deletes group mod is in and returns the URL corresponding to
    // that group, which the user is sent to
    $.post("/deleteGroup", postParameter, responseJSON => {

        const responseObject = JSON.parse(responseJSON);
        const url = responseObject.groupurl;
        console.log(url);
        window.location.href = url;
    });

    // Call function in websockets.js
    update_dash();
  });

});