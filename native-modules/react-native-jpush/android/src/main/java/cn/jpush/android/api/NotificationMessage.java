package cn.jpush.android.api;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;

public class NotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    public String appkey;
    public String msgId;
    public transient Context context;
    public String notificationContent;
    public int notificationAlertType;
    public String notificationTitle;
    public String notificationSmallIcon;
    public String notificationLargeIcon;
    public String notificationExtras;
    public int notificationStyle;
    public String notificationNormalSmallIcon;
    public int notificationBuilderId;
    public String notificationBigText;
    public String notificationBigPicPath;
    public String notificationInbox;
    public int notificationPriority;
    public int notificationImportance;
    public String notificationCategory;
    public int notificationId;
    public String developerArg0;
    public int platform = 0;
    public String appId;
    public int notificationType;
    public String notificationChannelId;
    public String displayForeground;
    public String _webPagePath;
    public ArrayList<String> showResourceList = new ArrayList<String>();
    public boolean isRichPush;
    public int richType;
    public String deeplink;
    public int failedAction;
    public String failedLink;
    public String targetPkgName;
    public String sspWxAppId;
    public String sspWmOriginId;
    public int sspWmType;
    public boolean isWmDeepLink = false;
    public int inAppMsgType = 1;
    public int inAppMsgShowType = 2;
    public int inAppMsgShowPos = 0;
    public String inAppMsgTitle = "";
    public String inAppMsgContentBody = "";
    public int inAppType;
    public String inAppShowTarget;
    public String inAppClickAction;
    public String inAppExtras;
    public transient JSONArray notificationTargetEvent;
    public transient JSONArray customButtons;

    @Override
    public String toString() {
        return "NotificationMessage{notificationId=" + notificationId
            + ", msgId='" + msgId
            + "', notificationTitle='" + notificationTitle
            + "', notificationContent='" + notificationContent
            + "', notificationExtras='" + notificationExtras + "'}";
    }
}
