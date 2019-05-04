function addUserSession(email, hash, img) {
  localStorage.setItem("grouper_email", email);
  localStorage.setItem("grouper_hash", hash);
  localStorage.setItem("grouper_img", img);
  localStorage.setItem("gid", "-1");
}

function setUserSessionGroup(groupID) {
  localStorage.setItem("gid", groupID);
}

function doesSessionExist() {
  return localStorage.getItem("grouper_email") !== null &&
  localStorage.getItem("grouper_hash") !== null;
}

function removeSession() {
  localStorage.removeItem("grouper_email");
  localStorage.removeItem("grouper_hash");
  localStorage.removeItem("grouper_img");
  localStorage.removeItem("gid");
}

function getUserSession() {
  return {
    email: localStorage.getItem("grouper_email"),
    hash: localStorage.getItem("grouper_hash"),
    img: localStorage.getItem("grouper_img"),
    gid: localStorage.getItem("gid")
  };
}
