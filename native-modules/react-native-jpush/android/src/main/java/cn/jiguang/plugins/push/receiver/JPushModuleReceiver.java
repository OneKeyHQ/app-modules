package cn.jiguang.plugins.push.receiver;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.jiguang.plugins.push.JPushModule;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jiguang.plugins.push.common.JConstants;
import cn.jiguang.plugins.push.helper.JPushHelper;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import cn.jpush.android.api.NotificationCustomButton;

public class JPushModuleReceiver extends JPushMessageReceiver {

  private static String safeId(String value) {
    return value != null ? value : "";
  }

  @Override
  public void onMessage(Context context, CustomMessage customMessage) {
    if (customMessage != null) {
      JLogger.i(
          "onMessage type=customMessage msgId="
              + safeId(customMessage.messageId)
              + " platform="
              + customMessage.platform
      );
    }
    Map<String, Object> map = JPushHelper.convertCustomMessageToMapObject(customMessage);
    JPushHelper.sendEventMap(JConstants.CUSTOM_MESSAGE_EVENT, map);
  }

  @Override
  public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
    if (notificationMessage == null) {
      return;
    }
    String eventType =
        notificationMessage.notificationType == 1 ? "localNotification" : JConstants.NOTIFICATION_ARRIVED;
    JLogger.i(
        "onNotifyMessageArrived type="
            + eventType
            + " notificationType="
            + notificationMessage.notificationType
            + " msgId="
            + safeId(notificationMessage.msgId)
            + " notificationId="
            + notificationMessage.notificationId
    );
    Map<String, Object> map = JPushHelper.convertNotificationToMapObject(JConstants.NOTIFICATION_ARRIVED, notificationMessage);
    if (notificationMessage.notificationType != 1) {
      JPushHelper.sendEventMap(JConstants.NOTIFICATION_EVENT, map);
    } else {
      JPushHelper.sendEventMap(JConstants.LOCAL_NOTIFICATION_EVENT, map);
    }
  }
  @Override
  public void onPropertyOperatorResult(Context context, JPushMessage jPushMessage) {
    if (jPushMessage != null) {
      JLogger.i(
          "onPropertyOperatorResult seq="
              + jPushMessage.getSequence()
              + " code="
              + jPushMessage.getErrorCode()
      );
    }
    Map<String, Object> map = JPushHelper.convertJPushMessageToMapObject(4, jPushMessage);
    JPushHelper.sendEventMap(JConstants.TAG_ALIAS_EVENT, map);
  }
  @Override
  public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
    JPushBroadcastReceiver.NOTIFICATION_BUNDLE = null;
    if (notificationMessage != null) {
      JLogger.i(
          "onNotifyMessageOpened type="
              + JConstants.NOTIFICATION_OPENED
              + " notificationType="
              + notificationMessage.notificationType
              + " msgId="
              + safeId(notificationMessage.msgId)
              + " notificationId="
              + notificationMessage.notificationId
      );
    }
    if (!JPushModule.isAppForeground) {
      JPushHelper.launchApp(context);
    }
    Map<String, Object> map = JPushHelper.convertNotificationToMapObject(JConstants.NOTIFICATION_OPENED, notificationMessage);
    if (notificationMessage != null && notificationMessage.notificationType == 1) {
      JPushHelper.sendEventMap(JConstants.LOCAL_NOTIFICATION_EVENT, map);
    } else {
      JPushHelper.sendEventMap(JConstants.NOTIFICATION_EVENT, map);
    }
  }
  @Override
  public void onInAppMessageShow(Context context, NotificationMessage notificationMessage) {
    if (notificationMessage != null) {
      JLogger.i(
          "onInAppMessageShow type="
              + JConstants.IN_APP_MESSAGE_SHOW
              + " notificationType="
              + notificationMessage.notificationType
              + " msgId="
              + safeId(notificationMessage.msgId)
      );
    }
    if (!JPushModule.isAppForeground) {
      JPushHelper.launchApp(context);
    }
    Map<String, Object> map = JPushHelper.convertInAppMessageToMapObject(JConstants.IN_APP_MESSAGE_SHOW, notificationMessage);
    JPushHelper.sendEventMap(JConstants.INAPP_MESSAGE_EVENT, map);
  }
  @Override
  public void onInAppMessageClick(Context context, NotificationMessage notificationMessage) {
    if (notificationMessage != null) {
      JLogger.i(
          "onInAppMessageClick type="
              + JConstants.IN_APP_MESSAGE_CLICK
              + " notificationType="
              + notificationMessage.notificationType
              + " msgId="
              + safeId(notificationMessage.msgId)
      );
    }
    if (!JPushModule.isAppForeground) {
      JPushHelper.launchApp(context);
    }
    Map<String, Object> map = JPushHelper.convertInAppMessageToMapObject(JConstants.IN_APP_MESSAGE_CLICK, notificationMessage);
    JPushHelper.sendEventMap(JConstants.INAPP_MESSAGE_EVENT, map);
  }

  @Override
  public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
    if (notificationMessage != null) {
      JLogger.i(
          "onNotifyMessageDismiss type="
              + JConstants.NOTIFICATION_DISMISSED
              + " notificationType="
              + notificationMessage.notificationType
              + " msgId="
              + safeId(notificationMessage.msgId)
              + " notificationId="
              + notificationMessage.notificationId
      );
    }
    Map<String, Object> map = JPushHelper.convertNotificationToMapObject(JConstants.NOTIFICATION_DISMISSED, notificationMessage);
    if (notificationMessage != null && notificationMessage.notificationType == 1) {
      JPushHelper.sendEventMap(JConstants.LOCAL_NOTIFICATION_EVENT, map);
    } else {
      JPushHelper.sendEventMap(JConstants.NOTIFICATION_EVENT, map);
    }
  }

  @Override
  public void onNotifyButtonClick(Context context, NotificationCustomButton notificationCustomButton) {
    if (notificationCustomButton != null) {
      JLogger.i(
          "onNotifyButtonClick msgId="
              + safeId(notificationCustomButton.a)
              + " platform="
              + notificationCustomButton.b
              + " actionType="
              + notificationCustomButton.d
      );
    }
    Map<String, Object> map = JPushHelper.convertNotificationCustomButtonToMapObject(notificationCustomButton);
    JPushHelper.sendEventMap(JConstants.NOTIFY_BUTTON_CLICK_EVENT, map);
 }

  @Override
  public void onRegister(Context context, String registrationId) {
    JLogger.i("onRegister registrationId=" + safeId(registrationId));
  }

  @Override
  public void onConnected(Context context, boolean state) {
    JLogger.i("onConnected state=" + state);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(JConstants.CONNECT_ENABLE, Boolean.valueOf(state));
    JPushHelper.sendEventMap(JConstants.CONNECT_EVENT, map);
  }

  @Override
  public void onCommandResult(Context context, CmdMessage message) {
    if (message == null) {
      return;
    }
    JLogger.i("onCommandResult cmd=" + message.cmd + " errorCode=" + message.errorCode);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(JConstants.COMMAND, Integer.valueOf(message.cmd));
    map.put(JConstants.COMMAND_EXTRA, message.extra != null ? message.extra.toString() : "");
    map.put(JConstants.COMMAND_MESSAGE, message.msg != null ? message.msg : "");
    map.put(JConstants.COMMAND_RESULT, Integer.valueOf(message.errorCode));
    JPushHelper.sendEventMap(JConstants.COMMAND_EVENT, map);
  }

  @Override
  public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
    if (jPushMessage != null) {
      JLogger.i(
          "onTagOperatorResult seq="
              + jPushMessage.getSequence()
              + " code="
              + jPushMessage.getErrorCode()
      );
    }
    Map<String, Object> map = JPushHelper.convertJPushMessageToMapObject(1, jPushMessage);
    JPushHelper.sendEventMap(JConstants.TAG_ALIAS_EVENT, map);
  }

  @Override
  public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
    if (jPushMessage != null) {
      JLogger.i(
          "onCheckTagOperatorResult seq="
              + jPushMessage.getSequence()
              + " code="
              + jPushMessage.getErrorCode()
              + " tagEnable="
              + jPushMessage.getTagCheckStateResult()
      );
    }
    Map<String, Object> map = JPushHelper.convertJPushMessageToMapObject(2, jPushMessage);
    JPushHelper.sendEventMap(JConstants.TAG_ALIAS_EVENT, map);
  }

  @Override
  public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
    if (jPushMessage != null) {
      JLogger.i(
          "onAliasOperatorResult seq="
              + jPushMessage.getSequence()
              + " code="
              + jPushMessage.getErrorCode()
      );
    }
    Map<String, Object> map = JPushHelper.convertJPushMessageToMapObject(3, jPushMessage);
    JPushHelper.sendEventMap(JConstants.TAG_ALIAS_EVENT, map);
  }

  @Override
  public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
    if (jPushMessage == null) {
      return;
    }
    JLogger.i(
        "onMobileNumberOperatorResult seq="
            + jPushMessage.getSequence()
            + " code="
            + jPushMessage.getErrorCode()
    );
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(JConstants.CODE, Integer.valueOf(jPushMessage.getErrorCode()));
    map.put(JConstants.SEQUENCE, Integer.valueOf(jPushMessage.getSequence()));
    JPushHelper.sendEventMap(JConstants.MOBILE_NUMBER_EVENT, map);
  }

}
