$(document).ready(() => {

  if($("#login-btn").length){
    $("#login-btn").click(startApp());
  }

  if (doesSessionExist() && $("#landing-body").length) {
    redirectToDashboard();
  }

});

/*
 * Apparently this is an acceptable way of sending a redicrect request with POST info.
 */
function redirectToDashboard() {
  var form = $("<form action='/grouper/dashboard' method='POST'>" +
    "<input type='hidden' name='login_email' value ='" + localStorage.getItem("grouper_email") + "'>" +
    "<input type='hidden' name='login_hash' value ='" + localStorage.getItem("grouper_hash") + "'>" +
  "</form>");
  $("body").append(form);
  form.submit();
}

/*
 * Called when the user clicks the button.
 */
 /*
let onLogin = function() {

}*/

// googleUser object containing user information
var googleUser = {};

var startApp = function() {
    gapi.load('auth2', function(){
        // Retrieve the singleton for the GoogleAuth library and set up the client.
        auth2 = gapi.auth2.init({
        client_id: '1074940443639-iqrogopumf2h67nu6h8ku1g5a6veq892.apps.googleusercontent.com',
        cookiepolicy: 'single_host_origin',
        // Request scopes in addition to 'profile' and 'email'
        //scope: 'additional_scope'
        });
        attachSignin(document.getElementById('login-btn'));
    });
};

function attachSignin(element) {
    console.log(element.id);
    auth2.attachClickHandler(element, {},
        (googleUser) => onSuccess(googleUser)
        , function(error) {
            alert(JSON.stringify(error, undefined, 2));
        });
}

function onSuccess(googleUser) {
  console.log("Sign in successful");
  var profile = googleUser.getBasicProfile();

  const postParameter = {name: profile.getName(), email: profile.getEmail(), img: profile.getImageUrl()};
  $.post("/newuser", postParameter, responseJSON => {
    const responseObject = JSON.parse(responseJSON);
    if (responseObject.msg == "success" && responseObject.hash != "") {
      console.log(responseObject.hash);
      // This adds the login email and hash returned by the server into
      // localStorage, which can be accessed later (see session.js).
      addUserSession(profile.getEmail(), responseObject.hash);
      redirectToDashboard();
    } else {
      alert(responseObject.error);
    }
  });

}
