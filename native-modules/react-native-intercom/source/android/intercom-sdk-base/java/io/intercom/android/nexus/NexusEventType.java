package io.intercom.android.nexus;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: nexus-client-android-6.4.0.aar:classes.jar:io/intercom/android/nexus/NexusEventType.class */
public enum NexusEventType {
    NewComment { // from class: io.intercom.android.nexus.NexusEventType.1
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    NewContent { // from class: io.intercom.android.nexus.NexusEventType.2
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    ConversationSeen { // from class: io.intercom.android.nexus.NexusEventType.3
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("nx.FromUser", nexusEvent.getUserId());
            return event;
        }
    },
    UserIsTyping { // from class: io.intercom.android.nexus.NexusEventType.4
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("nx.FromUser", nexusEvent.getUserId());
            return event;
        }
    },
    AdminIsTyping { // from class: io.intercom.android.nexus.NexusEventType.5
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminName", nexusEvent.getEventData().optString("adminName"));
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            eventData.put("adminAvatar", nexusEvent.getEventData().optString("adminAvatar"));
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    AdminIsTypingANote { // from class: io.intercom.android.nexus.NexusEventType.6
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminName", nexusEvent.getEventData().optString("adminName"));
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            eventData.put("adminAvatar", nexusEvent.getEventData().optString("adminAvatar"));
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    NewNote { // from class: io.intercom.android.nexus.NexusEventType.7
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            return event;
        }
    },
    ConversationAssigned { // from class: io.intercom.android.nexus.NexusEventType.8
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            eventData.put("assigneeId", nexusEvent.getEventData().optString("assigneeId"));
            return event;
        }
    },
    ConversationClosed { // from class: io.intercom.android.nexus.NexusEventType.9
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            return event;
        }
    },
    ConversationReopened { // from class: io.intercom.android.nexus.NexusEventType.10
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.getJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            return event;
        }
    },
    ConversationTitleGenerated { // from class: io.intercom.android.nexus.NexusEventType.11
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.getJSONObject("eventData");
            eventData.put("title", nexusEvent.getEventData().optString("title"));
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    UserPresence { // from class: io.intercom.android.nexus.NexusEventType.12
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = new JSONObject();
            JSONObject eventData = new JSONObject();
            event.put("eventGuid", UUID.randomUUID().toString());
            event.put("eventName", "nx." + name());
            event.put("eventData", eventData);
            return event;
        }
    },
    Ping { // from class: io.intercom.android.nexus.NexusEventType.13
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = new JSONObject();
            JSONObject eventData = new JSONObject();
            event.put("eventGuid", UUID.randomUUID().toString());
            event.put("eventName", "nx." + name());
            event.put("eventData", eventData);
            return event;
        }
    },
    UserContentSeenByAdmin { // from class: io.intercom.android.nexus.NexusEventType.14
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    Subscribe { // from class: io.intercom.android.nexus.NexusEventType.15
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("eventName", "nx." + name());
            return event;
        }
    },
    Unsubscribe { // from class: io.intercom.android.nexus.NexusEventType.16
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("eventName", "nx." + name());
            return event;
        }
    },
    ThreadAssigned { // from class: io.intercom.android.nexus.NexusEventType.17
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            eventData.put("assigneeId", nexusEvent.getEventData().optString("assigneeId"));
            return event;
        }
    },
    ThreadClosed { // from class: io.intercom.android.nexus.NexusEventType.18
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.optJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            return event;
        }
    },
    ThreadReopened { // from class: io.intercom.android.nexus.NexusEventType.19
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.getJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            return event;
        }
    },
    ThreadUpdated { // from class: io.intercom.android.nexus.NexusEventType.20
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.getJSONObject("eventData");
            eventData.put("adminId", nexusEvent.getEventData().optString("adminId"));
            return event;
        }
    },
    ThreadCreated { // from class: io.intercom.android.nexus.NexusEventType.21
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            event.put("nx.ToUser", nexusEvent.getUserId());
            return event;
        }
    },
    ConversationPartToken { // from class: io.intercom.android.nexus.NexusEventType.22
        @Override // io.intercom.android.nexus.NexusEventType
        protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
            JSONObject event = super.toJsonObject(nexusEvent);
            JSONObject eventData = event.getJSONObject("eventData");
            eventData.put("blocks", nexusEvent.getEventData().get("blocks"));
            eventData.put("partType", nexusEvent.getEventData().optString("partType"));
            eventData.put("clientAssignedUuid", nexusEvent.getEventData().optString("clientAssignedUuid"));
            eventData.put("tokenSequenceIndex", nexusEvent.getEventData().optString("tokenSequenceIndex"));
            return event;
        }
    },
    UNKNOWN { // from class: io.intercom.android.nexus.NexusEventType.23
        @Override // io.intercom.android.nexus.NexusEventType
        protected String toStringEncodedJsonObject(NexusEvent nexusEvent) {
            return "";
        }
    };

    protected String toStringEncodedJsonObject(NexusEvent nexusEvent) {
        try {
            return toJsonObject(nexusEvent).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    protected JSONObject toJsonObject(NexusEvent nexusEvent) throws JSONException {
        JSONObject event = new JSONObject();
        JSONObject eventData = new JSONObject();
        eventData.put("conversationId", nexusEvent.getEventData().optString("conversationId"));
        event.put("eventGuid", nexusEvent.getGuid());
        event.put("eventName", name());
        event.put("eventData", eventData);
        List<String> topics = nexusEvent.getTopics();
        if (!topics.isEmpty()) {
            event.put("nx.Topics", new JSONArray((Collection) topics));
        }
        return event;
    }

    public static NexusEventType safeValueOf(String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
