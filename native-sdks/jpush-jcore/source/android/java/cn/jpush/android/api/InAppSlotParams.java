package cn.jpush.android.api;

import org.json.JSONException;
import org.json.JSONObject;

public class InAppSlotParams {
    public int sequence = 0;
    public String adSlot = "";
    public String adEvent = "";

    public static Builder builder() {
        return new Builder();
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        try {
            json.put("sequence", sequence);
            json.put("slot", adSlot);
            json.put("event", adEvent);
        } catch (JSONException ignored) {
        }
        return json.toString();
    }

    public static InAppSlotParams parseJSONString(String raw) {
        InAppSlotParams params = new InAppSlotParams();
        try {
            JSONObject json = new JSONObject(raw);
            params.sequence = json.optInt("sequence", 0);
            params.adSlot = json.optString("slot", "");
            params.adEvent = json.optString("event", "");
        } catch (Throwable ignored) {
        }
        return params;
    }

    @Override
    public String toString() {
        return "InAppSlotParams{sequence=" + sequence + ", adSlot='" + adSlot + "', adEvent='" + adEvent + "'}";
    }

    public static class Builder {
        private final InAppSlotParams params = new InAppSlotParams();

        public Builder setSequence(int sequence) {
            params.sequence = sequence;
            return this;
        }

        public Builder setAdSlot(String adSlot) {
            params.adSlot = adSlot != null ? adSlot : "";
            return this;
        }

        public Builder setAdEvent(String adEvent) {
            params.adEvent = adEvent != null ? adEvent : "";
            return this;
        }

        public InAppSlotParams build() {
            return params;
        }
    }
}
