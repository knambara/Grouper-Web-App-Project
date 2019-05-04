package edu.brown.cs.jkjk.grouper;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Handles receiving and sending messages to websockets for dynamic page upadtes.
 * 
 * @author Kento
 *
 */
@WebSocket
public class GrouperWebSocket {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;

  private static enum MESSAGE_TYPE {
    CONNECT, GROUPS, MEMBERS, UPDATE_DASHBOARD, UPDATE_GROUP, REDIRECT, REDIRECT_USERS, REMOVE, REMOVE_GROUP
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    System.out.println("New session!");
    // Add the session to the queue
    sessions.add(session);
    // Build the CONNECT message
    JsonObject msg = new JsonObject();
    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId);

    msg.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    msg.add("payload", payload);
    // TODO Send the CONNECT message
    session.getRemote().sendString(GSON.toJson(msg));
    nextId++;
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO Remove the session from the queue
    System.out.println("session closed");
    sessions.remove(session);
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject received = GSON.fromJson(message, JsonObject.class);

    // Handles client request for updated list of groups
    if (received.get("type").getAsInt() == MESSAGE_TYPE.GROUPS.ordinal()) {
      // Get session id from payload
      JsonObject payload = received.get("payload").getAsJsonObject();
      int id = payload.get("id").getAsInt();

      JsonObject updateMsg = new JsonObject();
      JsonObject updatePayload = new JsonObject();
      updatePayload.addProperty("id", id);

      updateMsg.addProperty("type", MESSAGE_TYPE.UPDATE_DASHBOARD.ordinal());
      updateMsg.add("payload", updatePayload);

      for (Session s : sessions) {
        s.getRemote().sendString(GSON.toJson(updateMsg));
      }
    }

    // TODO: Handles client request for updated list of group members
    if (received.get("type").getAsInt() == MESSAGE_TYPE.MEMBERS.ordinal()) {
      // Get session id from payload
      JsonObject payload = received.get("payload").getAsJsonObject();
      int id = payload.get("id").getAsInt();
      String email = payload.get("email").getAsString();

      JsonObject updateMsg = new JsonObject();
      JsonObject updatePayload = new JsonObject();
      updatePayload.addProperty("id", id);
      updatePayload.addProperty("email", email);

      updateMsg.addProperty("type", MESSAGE_TYPE.UPDATE_GROUP.ordinal());
      updateMsg.add("payload", updatePayload);

      for (Session s : sessions) {
        if (s != session) {
          s.getRemote().sendString(GSON.toJson(updateMsg));
        }
      }
    }

    // TODO: Handles redirecting users to dashboard page upon end of group
    if (received.get("type").getAsInt() == MESSAGE_TYPE.REDIRECT.ordinal()) {
      // Get session id from payload
      JsonObject payload = received.get("payload").getAsJsonObject();
      int id = payload.get("id").getAsInt();
      String gid = payload.get("gid").getAsString();

      JsonObject updateMsg = new JsonObject();
      JsonObject updatePayload = new JsonObject();
      updatePayload.addProperty("id", id);
      updatePayload.addProperty("gid", gid);

      updateMsg.addProperty("type", MESSAGE_TYPE.REDIRECT_USERS.ordinal());
      updateMsg.add("payload", updatePayload);

      for (Session s : sessions) {
        if (s != session) {
          s.getRemote().sendString(GSON.toJson(updateMsg));
        }
      }
    }

    // TODO: Handles removing group from dashboard
    if (received.get("type").getAsInt() == MESSAGE_TYPE.REMOVE.ordinal()) {
      // Get session id from payload
      JsonObject payload = received.get("payload").getAsJsonObject();
      int id = payload.get("id").getAsInt();
      String gid = payload.get("gid").getAsString();

      JsonObject updateMsg = new JsonObject();
      JsonObject updatePayload = new JsonObject();
      updatePayload.addProperty("id", id);
      updatePayload.addProperty("gid", gid);

      updateMsg.addProperty("type", MESSAGE_TYPE.REMOVE_GROUP.ordinal());
      updateMsg.add("payload", updatePayload);

      for (Session s : sessions) {
        s.getRemote().sendString(GSON.toJson(updateMsg));
      }
    }
  }
}
