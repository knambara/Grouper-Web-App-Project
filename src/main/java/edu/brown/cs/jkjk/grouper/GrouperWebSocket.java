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

@WebSocket
public class GrouperWebSocket {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;

  private static enum MESSAGE_TYPE {
    CONNECT, GROUPS, MEMBERS, UPDATE_DASHBOARD, UPDATE_GROUP
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
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
    System.out.println("test");
    sessions.remove(session);
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject received = GSON.fromJson(message, JsonObject.class);

    // TODO: Handles client request for updated list of groups
    if (received.get("type").getAsInt() == MESSAGE_TYPE.GROUPS.ordinal()) {
      JsonObject payload = received.get("payload").getAsJsonObject();
      String groupIDList = payload.get("groups").getAsString();
    }

    // TODO: Handles client request for updated list of group members
    if (received.get("type").getAsInt() == MESSAGE_TYPE.MEMBERS.ordinal()) {
      JsonObject payload = received.get("payload").getAsJsonObject();
      String groupIDList = payload.get("members").getAsString();
    }

  }
}
