$(document).ready(() => {
  console.log("New Page");
  if(window.location.href === "http://localhost:4567/grouper"){
    $("#login-btn").click(startApp());
  }

});

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
    console.log(responseObject.msg);
    window.location.href = "http://localhost:4567/grouper/dashboard";

  });

  // TODO?: page is redirects to user's last visited page

}
