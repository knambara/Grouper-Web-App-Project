const MESSAGE_TYPE = {
  CONNECT: 0,
  GROUPS: 1,
  MEMBERS: 2,
  UPDATE_DASHBOARD: 3,
  UPDATE_GROUP: 4,
  REDIRECT: 5,
  REDIRECT_USERS: 6,
  REMOVE: 7,
  REMOVE_GROUP: 8,
  RELOAD: 9,
  RELOAD_GROUP: 10
};

let conn;
let myId = -1;

// Setup the WebSocket connection for live updating of groups
const setup_live_groups = () => {
  // TODO Create the WebSocket connection and assign it to `conn`
  if (window.location.pathname.includes("localhost")) {
    conn = new WebSocket("ws://localhost:4567/websocket");
  }
  else {
    // use heroku link
    conn = new WebSocket("ws://localhost:4567/websocket");
  }
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
        myId = data.payload.id;
        break;
      case MESSAGE_TYPE.UPDATE_DASHBOARD:
        // TODO: Update what the dashboard shows
        updateGrid();
        break;
      case MESSAGE_TYPE.UPDATE_GROUP:
        // TODO: Update the members within a group
        // function in group.js
        if (data.payload.gid === localStorage.getItem("gid")) {
            updateGroupContent(data.payload.email, data.payload.img);
        }
        break;
      case MESSAGE_TYPE.REDIRECT_USERS:
        if (data.payload.gid === localStorage.getItem("gid")) {
          redirectToDashboard();
          localStorage.setItem("gid", -1);
        }
        break;
      case MESSAGE_TYPE.REMOVE_GROUP:
        displayedGroups.delete(data.payload.gid);
        $('#' + data.payload.gid).remove();
        break;
      case MESSAGE_TYPE.RELOAD_GROUP:
        if (data.payload.gid === localStorage.getItem("gid")) {
          location.reload();
        }
        break;
    }
    updateCourseList();
  };
}

// Should be called when a user makes or deletes a new group.
const update_dash = () => {
  // Send a GROUPS message to the server using 'conn'
  const p = {"id" : myId};
  const JSONObj = {"type" : MESSAGE_TYPE.GROUPS, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}

// Should be called when a user deletes a group.
const remove_group = (gid) => {
  // Send a GROUPS message to the server using 'conn'
  const p = {"id" : myId, "gid" : gid};
  const JSONObj = {"type" : MESSAGE_TYPE.REMOVE, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}

//Should be called when a user joins or leaves a group
const update_group = (email, gid, img) => {
  // Send a MEMBERS message to the server using 'conn'
  const p = {"id" : myId, "email" : email, "gid" : gid, "img" : img};
  const JSONObj = {"type" : MESSAGE_TYPE.MEMBERS, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}

const redirect_all = (gid) => {
  // Send a REDIRECT message to the server using 'conn'
  const p = {"id" : myId, "gid" : gid};
  const JSONObj = {"type" : MESSAGE_TYPE.REDIRECT, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}

const reload_all = (gid) => {
  // Send a RELOAD message to the server
  const p = {"id" : myId, "gid" : gid};
  const JSONObj = {"type" : MESSAGE_TYPE.RELOAD, "payload" : p};
  conn.send(JSON.stringify(JSONObj));
}
