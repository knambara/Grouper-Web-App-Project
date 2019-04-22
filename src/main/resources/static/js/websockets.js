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
  conn = new WebSocket("ws://localhost:4567/scores");

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
        console.log("update dash");
        groupID = data.payload.id;
        if (document.getElementById(myPlayerId)) {
            $('#' + myPlayerId).html("<td>"+myPlayerId+"</td><td>"+data.payload.score+"</td>");
        } else {
            $("#myTable").html( $("#myTable").html() + "<tr id="+myPlayerId+"><td>"+myPlayerId+"</td><td>"+data.payload.score+"</td></tr>");     
        }
        break;
      case MESSAGE_TYPE.UPDATE_GROUP:
        // TODO: Update the members within a group
        console.log("update group")

    }
  };
}

// Should be called when a user makes a new group.
// `group_info` should be an array containing information of the newly created group .
const new_group = group_info => {
  // Send a GROUPS message to the server using `conn
  const p = {"id" : myId, "board" : $("#boardString").html().trim(), "text" : guesses};
  const JSONObj = {"type" : MESSAGE_TYPE.GROUPS, "payload" : p};
  console.log(JSON.stringify(JSONObj));
  conn.send(JSON.stringify(JSONObj));
}

// Should be called when a user deletes a new group.
const remove_group = group_info => {

}

// Should be called when a user joins a group.
const new_member = member_info => {

}

// Should be called when a user leaves a group.
const remove_member = member_info => {

}

