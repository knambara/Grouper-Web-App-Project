const MESSAGE_TYPE = {
  CONNECT: 0,
  GROUPS: 1,
  MEMBERS: 2,
  UPDATE_DASHBOARD: 3,
  UPDATE_GROUP: 4,
  REDIRECT: 5,
  REDIRECT_USERS: 6
};

let conn;
let myId = -1;

// Setup the WebSocket connection for live updating of groups
const setup_live_groups = () => {
  // TODO Create the WebSocket connection and assign it to `conn`
  conn = new WebSocket("ws://localhost:4567/websocket");
  console.log("Websocket has been set.");

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.CONNECT:
        // Assign myId
        console.log("Connected session with server.");
        myId = data.payload.id;
        break;
      case MESSAGE_TYPE.UPDATE_DASHBOARD:
        // TODO: Update what the dashboard shows
        console.log("update dashboard invoked");
        console.log("by session " + data.payload.id);
        updateGrid();
        break;
      case MESSAGE_TYPE.UPDATE_GROUP:
        // TODO: Update the members within a group
        console.log("update group invoked");
        // function in group.js
        updateGroupContent(data.payload.email);
        break;
      case MESSAGE_TYPE.REDIRECT_USERS:
        if (data.payload.gid === localStorage.getItem("gid")) {
          console.log("redirect users invoked");
          redirectToDashboard();
          alert("Moderator ended group session!");
          localStorage.setItem("gid", -1);
        }
        break;
    }
  };
}

// Should be called when a user makes or deletes a new group.
const update_dash = () => {
  // Send a GROUPS message to the server using 'conn'
  const p = {"id" : myId};
  const JSONObj = {"type" : MESSAGE_TYPE.GROUPS, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}

//Should be called when a user joins or leaves a group
const update_group = (email) => {
  // Send a MEMBERS message to the server using 'conn'
  console.log("In update group");
  const p = {"id" : myId, "email" : email};
  const JSONObj = {"type" : MESSAGE_TYPE.MEMBERS, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}

const redirect_all = (gid) => {
  // Send a REDIRECT message to the server using 'conn'
  console.log("redirect called");
  const p = {"id" : myId, "gid" : gid};
  const JSONObj = {"type" : MESSAGE_TYPE.REDIRECT, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}
