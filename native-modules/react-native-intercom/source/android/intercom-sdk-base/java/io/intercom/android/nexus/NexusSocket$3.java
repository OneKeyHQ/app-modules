package io.intercom.android.nexus;

import java.util.List;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.json.JSONException;
import org.json.JSONObject;

class NexusSocket$3 extends WebSocketListener {
   // $VF: synthetic field
   final NexusSocket this$0;

   NexusSocket$3(NexusSocket var1) {
      this.this$0 = var1;
   }

   @Override
   public void onOpen(WebSocket var1, Response var2) {
      NexusSocket.access$200(this.this$0).internal("onOpen: " + var2.message());
      NexusSocket.access$302(this.this$0, var1);
      NexusSocket.access$400(this.this$0);
      List var3 = NexusSocket.access$500(this.this$0).getTopics();
      if (!var3.isEmpty()) {
         this.this$0.fire(NexusEvent.getSubscribeEvent(var3).toStringEncodedJsonObject());
      }

      if (NexusSocket.access$600(this.this$0)) {
         this.this$0.fire(NexusEvent.getPingEvent().toStringEncodedJsonObject());
      }

      NexusSocket.access$700(this.this$0).onConnect();
   }

   @Override
   public void onMessage(WebSocket var1, String var2) {
      NexusSocket.access$400(this.this$0);
      this.parseJsonString(var2);
   }

   private void parseJsonString(String var1) {
      if (!var1.isEmpty() && !var1.equals(" ") && !var1.endsWith("|")) {
         try {
            JSONObject var2 = new JSONObject(var1);
            String var3 = var2.optString("eventName");
            if (!var3.isEmpty() && !var3.equals("ACK")) {
               NexusSocket.access$200(this.this$0).internal("onMessage TEXT: " + var1);
               NexusSocket.access$700(this.this$0).notifyEvent(new NexusEvent(var2));
            } else {
               NexusSocket.access$200(this.this$0).internal("onMessage ACK: " + var1);
            }
         } catch (JSONException var4) {
            NexusSocket.access$200(this.this$0).internal("onMessage: json parse exception for message: '" + var1 + " " + var4);
         }
      }
   }

   @Override
   public void onMessage(WebSocket var1, ByteString var2) {
      NexusSocket.access$200(this.this$0).internal("Received bytes message " + var2 + ", resetting timeout");
      NexusSocket.access$400(this.this$0);
   }

   @Override
   public void onClosing(WebSocket var1, int var2, String var3) {
      NexusSocket.access$200(this.this$0).internal("Server requested close:  " + var2 + " - '" + var3 + "'");
      var1.close(var2, var3);
   }

   @Override
   public void onClosed(WebSocket var1, int var2, String var3) {
      switch (var2) {
         case 4000:
            NexusSocket.access$800(this.this$0);
            break;
         default:
            NexusSocket.access$900(this.this$0);
      }

      NexusSocket.access$200(this.this$0).internal("onClose code: " + var2 + " reason: " + var3);
   }

   @Override
   public void onFailure(WebSocket var1, Throwable var2, Response var3) {
      if (NexusSocket.shouldReconnectFromFailure(var2, var3)) {
         NexusSocket.access$900(this.this$0);
      } else {
         NexusSocket.access$800(this.this$0);
      }

      NexusSocket.access$200(this.this$0).internal("onFailure: " + var2.getMessage());
      NexusSocket.access$700(this.this$0).onConnectFailed();
   }
}
