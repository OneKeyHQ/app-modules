package cn.jiguang.plugins.push.receiver;

import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

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
    WritableMap writableMap = JPushHelper.convertCustomMessage(customMessage);
    JPushHelper.sendEvent(JConstants.CUSTOM_MESSAGE_EVENT, writableMap);
  }

  @Override
  public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
    if (notificationMessage != null) {
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
    }
    WritableMap writableMap = JPushHelper.convertNotificationToMap(JConstants.NOTIFICATION_ARRIVED, notificationMessage);
    if(notificationMessage.notificationType!=1){
      JPushHelper.sendEvent(JConstants.NOTIFICATION_EVENT, writableMap);
    }else {
      JPushHelper.sendEvent(JConstants.LOCAL_NOTIFICATION_EVENT, writableMap);
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
    WritableMap writableMap = JPushHelper.convertJPushMessageToMap(4, jPushMessage);
    JPushHelper.sendEvent(JConstants.TAG_ALIAS_EVENT, writableMap);
  }
  @Override
  public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
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
    if (JPushModule.reactContext != null) {
      if (!JPushModule.isAppForeground) JPushHelper.launchApp(context);
      WritableMap writableMap = JPushHelper.convertNotificationToMap(JConstants.NOTIFICATION_OPENED, notificationMessage);
      JPushHelper.sendEvent(JConstants.NOTIFICATION_EVENT, writableMap);
    } else {
      super.onNotifyMessageOpened(context, notificationMessage);
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
    if (JPushModule.reactContext != null) {
      if (!JPushModule.isAppForeground) JPushHelper.launchApp(context);
      WritableMap writableMap = JPushHelper.convertInAppMessageToMap(JConstants.IN_APP_MESSAGE_SHOW, notificationMessage);
      JPushHelper.sendEvent(JConstants.INAPP_MESSAGE_EVENT, writableMap);
    } else {
      super.onInAppMessageShow(context, notificationMessage);
    }
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
    if (JPushModule.reactContext != null) {
      if (!JPushModule.isAppForeground) JPushHelper.launchApp(context);
      WritableMap writableMap = JPushHelper.convertInAppMessageToMap(JConstants.IN_APP_MESSAGE_CLICK, notificationMessage);
      JPushHelper.sendEvent(JConstants.INAPP_MESSAGE_EVENT, writableMap);
    } else {
      super.onInAppMessageClick(context, notificationMessage);
    }
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
    WritableMap writableMap = JPushHelper.convertNotificationToMap(JConstants.NOTIFICATION_DISMISSED, notificationMessage);
    JPushHelper.sendEvent(JConstants.NOTIFICATION_EVENT, writableMap);
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
    WritableMap writableMap = JPushHelper.convertNotificationCustomButtonToMap(notificationCustomButton);
    JPushHelper.sendEvent(JConstants.NOTIFY_BUTTON_CLICK_EVENT, writableMap);
 }

  @Override
  public void onRegister(Context context, String registrationId) {
    JLogger.i("onRegister registrationId=" + safeId(registrationId));
  }

  @Override
  public void onConnected(Context context, boolean state) {
    JLogger.i("onConnected state=" + state);
    WritableMap writableMap = Arguments.createMap();
    writableMap.putBoolean(JConstants.CONNECT_ENABLE, state);
    JPushHelper.sendEvent(JConstants.CONNECT_EVENT, writableMap);
  }

  @Override
  public void onCommandResult(Context context, CmdMessage message) {
    if (message != null) {
      JLogger.i("onCommandResult cmd=" + message.cmd + " errorCode=" + message.errorCode);
    }
    WritableMap writableMap = Arguments.createMap();
    writableMap.putInt(JConstants.COMMAND, message.cmd);
    writableMap.putString(JConstants.COMMAND_EXTRA, message.extra.toString());
    writableMap.putString(JConstants.COMMAND_MESSAGE, message.msg);
    writableMap.putInt(JConstants.COMMAND_RESULT, message.errorCode);
    JPushHelper.sendEvent(JConstants.COMMAND_EVENT, writableMap);
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
    WritableMap writableMap = JPushHelper.convertJPushMessageToMap(1, jPushMessage);
    JPushHelper.sendEvent(JConstants.TAG_ALIAS_EVENT, writableMap);
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
    WritableMap writableMap = JPushHelper.convertJPushMessageToMap(2, jPushMessage);
    JPushHelper.sendEvent(JConstants.TAG_ALIAS_EVENT, writableMap);
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
    WritableMap writableMap = JPushHelper.convertJPushMessageToMap(3, jPushMessage);
    JPushHelper.sendEvent(JConstants.TAG_ALIAS_EVENT, writableMap);
  }

  @Override
  public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
    if (jPushMessage != null) {
      JLogger.i(
          "onMobileNumberOperatorResult seq="
              + jPushMessage.getSequence()
              + " code="
              + jPushMessage.getErrorCode()
      );
    }
    WritableMap writableMap = Arguments.createMap();
    writableMap.putInt(JConstants.CODE, jPushMessage.getErrorCode());
    writableMap.putInt(JConstants.SEQUENCE, jPushMessage.getSequence());
    JPushHelper.sendEvent(JConstants.MOBILE_NUMBER_EVENT, writableMap);
  }

}
