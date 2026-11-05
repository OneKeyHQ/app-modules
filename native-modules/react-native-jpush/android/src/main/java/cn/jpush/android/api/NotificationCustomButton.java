package cn.jpush.android.api;

import java.io.Serializable;

public class NotificationCustomButton implements Serializable {
    private static final long serialVersionUID = 1L;

    public String msgId;
    public int platform;
    public String name;
    public int actionType;
    public String action;
    public String data;

    // Public field aliases accessed directly by JPushHelper in jpush-react-native.
    public String a;
    public int b;
    public String c;
    public int d;
    public String e;
    public String f;

    public NotificationCustomButton() {
    }

    public NotificationCustomButton(String msgId, int platform, String name, int actionType, String action, String data) {
        this.msgId = msgId;
        this.platform = platform;
        this.name = name;
        this.actionType = actionType;
        this.action = action;
        this.data = data;
        this.a = msgId;
        this.b = platform;
        this.c = name;
        this.d = actionType;
        this.e = action;
        this.f = data;
    }

    @Override
    public String toString() {
        return "NotificationCustomButton{msgId='" + (msgId != null ? msgId : a)
            + "', platform=" + (platform != 0 ? platform : b)
            + ", name='" + (name != null ? name : c)
            + "', actionType=" + (actionType != 0 ? actionType : d)
            + ", action='" + (action != null ? action : e)
            + "', data=" + (data != null ? data : f) + '}';
    }
}
