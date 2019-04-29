function addUserSession(email, hash) {
  localStorage.setItem("grouper_email", email);
  localStorage.setItem("grouper_hash", hash);
}

function doesSessionExist() {
  return localStorage.getItem("grouper_email") !== null &&
  localStorage.getItem("grouper_hash") !== null;
}

function removeSession() {
  localStorage.removeItem("grouper_email");
  localStorage.removeItem("grouper_hash");
  localStorage.removeItem("gid");
}

function getUserSession() {
  return {
    email: localStorage.getItem("grouper_email"),
    hash: localStorage.getItem("grouper_hash")
  };
}
