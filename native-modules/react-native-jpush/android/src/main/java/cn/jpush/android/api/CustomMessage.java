package cn.jpush.android.api;

import java.io.Serializable;

public class CustomMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public String messageId;
    public String extra;
    public String message;
    public String contentType;
    public String title;
    public String senderId;
    public String appId;
    public byte platform;

    @Override
    public String toString() {
        return "CustomMessage{messageId='" + messageId
            + "', extra='" + extra
            + "', message='" + message
            + "', contentType='" + contentType
            + "', title='" + title
            + "', senderId='" + senderId
            + "', appId='" + appId
            + "', platform='" + platform + "'}";
    }
}
