package cn.jpush.android.data;

import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class JPushLocalNotification implements Serializable {
    private static final long serialVersionUID = 1472982106750878137L;
    private long broadcastTime = 0L;
    private String content;
    private String title;
    private String extras;
    private long builderId;
    private long notificationId = 1L;
    private int NotificationOnly = 1;
    private String overrideMsgId = "";
    private String contentType = "";
    private int priority = 0;
    private String category = "";

    public String toJSON() {
        JSONObject root = new JSONObject();
        try {
            JSONObject messageContent = new JSONObject();
            if (!TextUtils.isEmpty(this.extras)) {
                messageContent.put("n_extras", new JSONObject(this.extras));
            }
            messageContent.put("n_priority", this.priority);
            putIfNotEmpty("n_category", this.category, messageContent);
            putIfNotEmpty("n_content", this.content, messageContent);
            putIfNotEmpty("n_title", this.title, messageContent);
            messageContent.put("ad_t", 0);
            root.put("m_content", messageContent);
            putIfNotEmpty("msg_id", String.valueOf(this.notificationId), root);
            putIfNotEmpty("content_type", this.contentType, root);
            putIfNotEmpty("override_msg_id", this.overrideMsgId, root);
            root.put("n_only", this.NotificationOnly);
            root.put("n_builder_id", this.builderId);
            root.put("show_type", 3);
            root.put("notificaion_type", 1);
        } catch (JSONException ignored) {
        }
        return root.toString();
    }

    private static void putIfNotEmpty(String key, String value, JSONObject target) throws JSONException {
        if (!TextUtils.isEmpty(value)) {
            target.put(key, value);
        }
    }

    public void setNotificationId(long id) {
        this.notificationId = (int) id;
    }

    public long getNotificationId() {
        return this.notificationId;
    }

    public void setBroadcastTime(long timeMillis) {
        this.broadcastTime = timeMillis;
    }

    public void setBroadcastTime(Date date) {
        if (date != null) {
            this.broadcastTime = date.getTime();
        }
    }

    public void setBroadcastTime(int year, int month, int day, int hour, int minute, int second) {
        if (year >= 0 && month >= 1 && month <= 12 && day >= 1 && day <= 31
            && hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day, hour, minute, second);
            long target = calendar.getTimeInMillis();
            long now = System.currentTimeMillis();
            this.broadcastTime = Math.max(target, now);
        } else {
            Logger.ee("JPushLocalNotification", "Set time fail! Please check your args!");
        }
    }

    public long getBroadcastTime() {
        return this.broadcastTime;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getExtras() {
        return this.extras;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getBuilderId() {
        return this.builderId;
    }

    public void setBuilderId(long builderId) {
        this.builderId = builderId;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JPushLocalNotification other = (JPushLocalNotification) obj;
        return this.notificationId == other.notificationId;
    }

    @Override
    public int hashCode() {
        return (int) (this.notificationId ^ (this.notificationId >>> 32));
    }
}
