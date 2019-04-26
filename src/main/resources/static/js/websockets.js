const MESSAGE_TYPE = {
  CONNECT: 0,
  GROUPS: 1,
  MEMBERS: 2,
  UPDATE_DASHBOARD: 3,
  UPDATE_GROUP: 4
};

let conn;
let myId = -1;

// Setup the WebSocket connection for live updating of scores.
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
        console.log("connected");
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

    }
  };
}

// Should be called when a user makes a new group.
const new_group = () => {
  // Send a GROUPS message to the server using `conn
  const p = {"id" : myId};
  const JSONObj = {"type" : MESSAGE_TYPE.GROUPS, "payload" : p};     
  conn.send(JSON.stringify(JSONObj));
}

