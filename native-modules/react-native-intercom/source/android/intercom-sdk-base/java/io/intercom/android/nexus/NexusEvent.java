package io.intercom.android.nexus;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class NexusEvent {
   static final String TOPIC_PREFIX_CONVERSATION = "conversation/";
   static final String EVENT_GUID = "eventGuid";
   static final String EVENT_DATA = "eventData";
   static final String EVENT_NAME = "eventName";
   static final String NX_TOPICS = "nx.Topics";
   static final String NX_TO_USER = "nx.ToUser";
   static final String NX_FROM_USER = "nx.FromUser";
   static final String ADMIN_ID = "adminId";
   static final String ADMIN_NAME = "adminName";
   static final String ADMIN_AVATAR = "adminAvatar";
   static final String ADMIN_TIMESTAMP = "adminTimestamp";
   static final String CONVERSATION_ID = "conversationId";
   static final String ASSIGNEE_ID = "assigneeId";
   static final String TITLE = "title";
   private final NexusEventType eventType;
   private final EventData eventData;
   private final List<String> topics;
   private final String guid;
   private final String userId;

   public NexusEvent(NexusEvent$Builder var1) {
      this.eventType = var1.eventName;
      this.eventData = new EventData();
      if (var1.eventData != null) {
         for (String var4 : var1.eventData.keySet()) {
            Object var5 = var1.eventData.get(var4);
            if (var5 != null) {
               this.eventData.put(var4, var5);
            }
         }
      }

      this.topics = new ArrayList();
      if (var1.topics != null) {
         for (String var7 : var1.topics) {
            if (!TextUtils.isEmpty(var7)) {
               this.topics.add(var7);
            }
         }
      }

      this.guid = UUID.randomUUID().toString();
      this.userId = var1.userId != null ? var1.userId : "";
   }

   public NexusEvent(JSONObject var1) {
      this.eventType = NexusEventType.safeValueOf(var1.optString("eventName"));
      JSONObject var2 = var1.optJSONObject("eventData");
      if (var2 != null && var2.length() > 0) {
         this.eventData = new EventData(var2.length());
         Iterator var3 = var2.keys();

         while (var3.hasNext()) {
            String var4 = (String)var3.next();
            Object var5 = var2.opt(var4);
            if (var5 != null) {
               this.eventData.put(var4, var5);
            }
         }
      } else {
         this.eventData = new EventData();
      }

      this.topics = new ArrayList();
      JSONArray var6 = var1.optJSONArray("nx.Topics");
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length(); var7++) {
            String var8 = var6.optString(var7);
            if (!TextUtils.isEmpty(var8)) {
               this.topics.add(var8);
            }
         }
      }

      this.guid = var1.optString("eventGuid");
      if (var1.has("nx.ToUser")) {
         this.userId = var1.optString("nx.ToUser");
      } else {
         this.userId = var1.optString("nx.FromUser");
      }
   }

   public String toStringEncodedJsonObject() {
      return this.eventType.toStringEncodedJsonObject(this);
   }

   public NexusEventType getEventType() {
      return this.eventType;
   }

   public EventData getEventData() {
      return this.eventData;
   }

   public List getTopics() {
      return this.topics;
   }

   public String getGuid() {
      return this.guid;
   }

   public String getUserId() {
      return this.userId;
   }

   public static NexusEvent getAdminIsTypingEvent(String var0, String var1, String var2, String var3, String var4) {
      EventData var5 = new EventData(4);
      var5.put("conversationId", var0);
      var5.put("adminId", var1);
      var5.put("adminName", var2);
      var5.put("adminAvatar", var3);
      return new NexusEvent$Builder(NexusEventType.AdminIsTyping).withEventData(var5).withUserId(var4).withTopics(conversationTopics(var0)).build();
   }

   public static NexusEvent getAdminIsTypingNoteEvent(String var0, String var1, String var2, String var3, String var4) {
      EventData var5 = new EventData(4);
      var5.put("adminId", var1);
      var5.put("conversationId", var0);
      var5.put("adminName", var2);
      var5.put("adminAvatar", var3);
      return new NexusEvent$Builder(NexusEventType.AdminIsTypingANote).withEventData(var5).withTopics(conversationTopics(var0)).withUserId(var4).build();
   }

   public static NexusEvent getUserIsTypingEvent(String var0, String var1) {
      EventData var2 = new EventData(1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.UserIsTyping).withEventData(var2).withTopics(conversationTopics(var0)).withUserId(var1).build();
   }

   public static NexusEvent getConversationSeenEvent(String var0, String var1) {
      EventData var2 = new EventData(1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.ConversationSeen).withEventData(var2).withTopics(conversationTopics(var0)).withUserId(var1).build();
   }

   public static NexusEvent getConversationSeenAdminEvent(String var0, String var1) {
      EventData var2 = new EventData(1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.UserContentSeenByAdmin).withEventData(var2).withTopics(conversationTopics(var0)).withUserId(var1).build();
   }

   public static NexusEvent getNewCommentEvent(String var0, String var1) {
      EventData var2 = new EventData(1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.NewComment).withEventData(var2).withTopics(conversationTopics(var0)).withUserId(var1).build();
   }

   public static NexusEvent getNewContentEvent(String var0, long var1) {
      EventData var3 = new EventData(2);
      var3.put("entity_type", var1);
      var3.put("entity_id", var0);
      return new NexusEvent$Builder(NexusEventType.NewContent).withEventData(var3).build();
   }

   public static NexusEvent getNewNoteEvent(String var0, String var1) {
      EventData var2 = new EventData(2);
      var2.put("adminId", var1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.NewNote).withEventData(var2).build();
   }

   public static NexusEvent getConversationAssignedEvent(String var0, String var1, String var2) {
      EventData var3 = new EventData(3);
      var3.put("adminId", var1);
      var3.put("conversationId", var0);
      var3.put("assigneeId", var2);
      return new NexusEvent$Builder(NexusEventType.ConversationAssigned).withEventData(var3).build();
   }

   public static NexusEvent getConversationClosedEvent(String var0, String var1) {
      EventData var2 = new EventData(2);
      var2.put("adminId", var1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.ConversationClosed).withEventData(var2).build();
   }

   public static NexusEvent getConversationReopenedEvent(String var0, String var1) {
      EventData var2 = new EventData(2);
      var2.put("adminId", var1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.ConversationReopened).withEventData(var2).build();
   }

   public static NexusEvent getConversationTitleGeneratedEvent(String var0, String var1, String var2) {
      EventData var3 = new EventData(2);
      var3.put("conversationId", var0);
      var3.put("title", var1);
      return new NexusEvent$Builder(NexusEventType.ConversationTitleGenerated)
         .withEventData(var3)
         .withTopics(conversationTopics(var0))
         .withUserId(var2)
         .build();
   }

   public static NexusEvent getConversationPartTokenEvent(String var0, String var1) {
      EventData var2 = new EventData(1);
      var2.put("conversationId", var0);
      return new NexusEvent$Builder(NexusEventType.ConversationPartToken).withEventData(var2).withTopics(conversationTopics(var0)).withUserId(var1).build();
   }

   public static NexusEvent getUserPresenceEvent() {
      return new NexusEvent$Builder(NexusEventType.UserPresence).build();
   }

   public static NexusEvent getPingEvent() {
      return new NexusEvent$Builder(NexusEventType.Ping).build();
   }

   public static NexusEvent getSubscribeEvent(List var0) {
      return new NexusEvent$Builder(NexusEventType.Subscribe).withTopics(var0).build();
   }

   public static NexusEvent getUnsubscribeEvent(List var0) {
      return new NexusEvent$Builder(NexusEventType.Unsubscribe).withTopics(var0).build();
   }

   private static List conversationTopics(String var0) {
      String var1 = var0;
      if (TextUtils.isEmpty(var0)) {
         var1 = "new";
      }

      return Collections.singletonList("conversation/" + var1);
   }
}
