package com.margelo.nitro.reactnativejpush

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.NitroModules
import cn.jiguang.api.JCoreInterface
import cn.jiguang.api.utils.JCollectionAuth
import cn.jiguang.core.JCoreStorage
import cn.jiguang.plugins.core.JCoreModule
import cn.jiguang.plugins.push.JPushModule
import cn.jiguang.plugins.push.common.JConstants
import cn.jiguang.plugins.push.common.JLogger
import cn.jiguang.plugins.push.helper.JPushHelper
import cn.jiguang.plugins.push.receiver.JPushBroadcastReceiver
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.data.JPushCollectControl
import cn.jpush.android.data.JPushLocalNotification
import org.json.JSONObject

@DoNotStrip
class ReactNativeJpush : HybridReactNativeJpushSpec() {
  init {
    getReactContext()
  }

  private fun getReactContext(): ReactApplicationContext? {
    val ctx = NitroModules.applicationContext ?: JPushModule.reactContext ?: JCoreModule.reactContext
    if (ctx != null) {
      if (JPushModule.reactContext == null) {
        JPushModule.reactContext = ctx
      }
      if (JCoreModule.reactContext == null) {
        JCoreModule.reactContext = ctx
      }
    }
    return ctx
  }

  private fun flushQueuedNotificationBundle() {
    val bundle = JPushBroadcastReceiver.NOTIFICATION_BUNDLE ?: return
    val writableMap = JPushHelper.convertNotificationBundleToMap(JConstants.NOTIFICATION_OPENED, bundle)
    JPushHelper.sendEvent(JConstants.NOTIFICATION_EVENT, writableMap)
    JPushBroadcastReceiver.NOTIFICATION_BUNDLE = null
  }

  override fun setDebugMode(enable: Boolean) {
    JPushInterface.setDebugMode(enable)
    JLogger.setLoggerEnable(enable)
  }

  override fun initPush() {
    val ctx = getReactContext() ?: return
    JPushInterface.init(ctx)
    flushQueuedNotificationBundle()
    JPushInterface.setNotificationCallBackEnable(ctx, true)
  }

  override fun setupWithConfig(appKey: String, channel: String, production: Boolean) {
    val ctx = getReactContext() ?: return
    if (appKey.isNotEmpty()) {
      JCoreStorage.setAppKey(ctx, appKey)
    }
    if (channel.isNotEmpty()) {
      JPushInterface.setChannel(ctx, channel)
    }
    JPushInterface.init(ctx)
    flushQueuedNotificationBundle()
    JPushInterface.setNotificationCallBackEnable(ctx, true)
  }

  override fun loadJS() {
    flushQueuedNotificationBundle()
  }

  override fun stopPush() {
    val ctx = getReactContext() ?: return
    JPushInterface.stopPush(ctx)
  }

  override fun resumePush() {
    val ctx = getReactContext() ?: return
    JPushInterface.resumePush(ctx)
  }

  override fun isPushStopped(): Boolean {
    val ctx = getReactContext() ?: return false
    return JPushInterface.isPushStopped(ctx)
  }

  override fun getRegistrationID(): String {
    val ctx = getReactContext() ?: return ""
    return JPushInterface.getRegistrationID(ctx) ?: ""
  }

  override fun isNotificationEnabled(): Boolean {
    val ctx = getReactContext() ?: return false
    return JPushInterface.isNotificationEnabled(ctx) == 1
  }

  override fun addTags(sequence: Double, tags: Array<String>) {
    val ctx = getReactContext() ?: return
    JPushInterface.addTags(ctx, sequence.toInt(), tags.toSet())
  }

  override fun setTags(sequence: Double, tags: Array<String>) {
    val ctx = getReactContext() ?: return
    JPushInterface.setTags(ctx, sequence.toInt(), tags.toSet())
  }

  override fun deleteTags(sequence: Double, tags: Array<String>) {
    val ctx = getReactContext() ?: return
    JPushInterface.deleteTags(ctx, sequence.toInt(), tags.toSet())
  }

  override fun cleanTags(sequence: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.cleanTags(ctx, sequence.toInt())
  }

  override fun getAllTags(sequence: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.getAllTags(ctx, sequence.toInt())
  }

  override fun checkTagBindState(sequence: Double, tag: String) {
    val ctx = getReactContext() ?: return
    JPushInterface.checkTagBindState(ctx, sequence.toInt(), tag)
  }

  override fun setAlias(sequence: Double, alias: String) {
    val ctx = getReactContext() ?: return
    JPushInterface.setAlias(ctx, sequence.toInt(), alias)
  }

  override fun deleteAlias(sequence: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.deleteAlias(ctx, sequence.toInt())
  }

  override fun getAlias(sequence: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.getAlias(ctx, sequence.toInt())
  }

  override fun setProperties(sequence: Double, propertiesJson: String) {
    val ctx = getReactContext() ?: return
    val map = HashMap<String, Any>()
    if (propertiesJson.isNotEmpty()) {
      try {
        val json = JSONObject(propertiesJson)
        val keys = json.keys()
        while (keys.hasNext()) {
          val k = keys.next()
          val v = json.opt(k)
          if (v != null && v != JSONObject.NULL) {
            map[k] = v
          }
        }
      } catch (_: Throwable) {
      }
    }
    JPushInterface.setProperties(ctx, sequence.toInt(), map)
  }

  override fun deleteProperties(sequence: Double, keys: Array<String>) {
    val ctx = getReactContext() ?: return
    val map = HashMap<String, Any>()
    for (k in keys) {
      map[k] = ""
    }
    JPushInterface.deleteProperties(ctx, sequence.toInt(), map)
  }

  override fun cleanProperties(sequence: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.cleanProperties(ctx, sequence.toInt())
  }

  override fun setBadge(badge: Double, appBadge: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.setBadgeNumber(ctx, badge.toInt())
  }

  override fun setMobileNumber(sequence: Double, mobileNumber: String) {
    val ctx = getReactContext() ?: return
    JPushInterface.setMobileNumber(ctx, sequence.toInt(), mobileNumber)
  }

  override fun initCrashHandler() {
    val ctx = getReactContext() ?: return
    JPushInterface.initCrashHandler(ctx)
  }

  override fun setPowerSaveMode(enable: Boolean) {
    val ctx = getReactContext() ?: return
    JPushInterface.setPowerSaveMode(ctx, enable)
  }

  override fun addLocalNotification(
    messageID: String,
    title: String,
    content: String,
    broadcastTime: String,
    extrasJson: String
  ) {
    val ctx = getReactContext() ?: return
    val id = messageID.toLongOrNull() ?: 0L
    val notification = JPushLocalNotification()
    notification.notificationId = id
    notification.title = title.ifEmpty { ctx.packageName }
    notification.content = content.ifEmpty { ctx.packageName }
    val timeMs = broadcastTime.toLongOrNull() ?: 0L
    if (timeMs > 0L) {
      notification.setBroadcastTime(timeMs)
    }
    if (extrasJson.isNotEmpty() && extrasJson != "{}") {
      notification.extras = extrasJson
    }
    JPushInterface.addLocalNotification(ctx, notification)
  }

  override fun removeLocalNotification(messageID: String) {
    val ctx = getReactContext() ?: return
    val id = messageID.toLongOrNull() ?: return
    JPushInterface.removeLocalNotification(ctx, id)
  }

  override fun clearLocalNotifications() {
    val ctx = getReactContext() ?: return
    JPushInterface.clearLocalNotifications(ctx)
  }

  override fun clearAllNotifications() {
    val ctx = getReactContext() ?: return
    JPushInterface.clearAllNotifications(ctx)
  }

  override fun clearNotificationById(notificationId: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.clearNotificationById(ctx, notificationId.toInt())
  }

  override fun requestPermission() {
    val ctx = getReactContext() ?: return
    JPushInterface.requestPermission(ctx)
  }

  override fun setPushTime(days: DoubleArray, startHour: Double, endHour: Double) {
    val ctx = getReactContext() ?: return
    val daySet = HashSet<Int>()
    for (d in days) {
      val dayInt = d.toInt()
      if (dayInt in 0..6) {
        daySet.add(dayInt)
      }
    }
    JPushInterface.setPushTime(ctx, daySet, startHour.toInt(), endHour.toInt())
  }

  override fun setSilenceTime(
    startHour: Double,
    startMinute: Double,
    endHour: Double,
    endMinute: Double
  ) {
    val ctx = getReactContext() ?: return
    JPushInterface.setSilenceTime(
      ctx,
      startHour.toInt(),
      startMinute.toInt(),
      endHour.toInt(),
      endMinute.toInt()
    )
  }

  override fun setLatestNotificationNumber(maxNumber: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.setLatestNotificationNumber(ctx, maxNumber.toInt())
  }

  override fun setChannel(channel: String) {
    val ctx = getReactContext() ?: return
    if (channel.isNotEmpty()) {
      JPushInterface.setChannel(ctx, channel)
    }
  }

  override fun setChannelAndSound(channel: String, channelId: String, sound: String) {
    val ctx = getReactContext() ?: return
    if (channel.isEmpty() || channelId.isEmpty()) {
      return
    }
    try {
      if (Build.VERSION.SDK_INT >= 26) {
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notifChannel = NotificationChannel(
          channelId,
          channel,
          NotificationManager.IMPORTANCE_HIGH
        )
        if (sound.isNotEmpty()) {
          notifChannel.setSound(
            Uri.parse("android.resource://${ctx.packageName}/raw/$sound"),
            null
          )
        }
        manager?.createNotificationChannel(notifChannel)
      }
      JPushInterface.setChannel(ctx, channel)
    } catch (_: Throwable) {
    }
  }

  override fun setLinkMergeEnable(enable: Boolean) {
    val ctx = getReactContext() ?: return
    JPushInterface.setLinkMergeEnable(ctx, enable)
  }

  override fun setSmartPushEnable(enable: Boolean) {
    val ctx = getReactContext() ?: return
    JPushInterface.setSmartPushEnable(ctx, enable)
  }

  override fun setDataInsightsEnable(enable: Boolean) {
    val ctx = getReactContext() ?: return
    JPushInterface.setDataInsightsEnable(ctx, enable)
  }

  override fun setGeofenceEnable(enable: Boolean) {
    val ctx = getReactContext() ?: return
    JPushInterface.setGeofenceEnable(ctx, enable)
  }

  override fun setBackgroundEnable(enable: Boolean) {
    // iOS-only; no-op on Android
  }

  override fun setMaxGeofenceNumber(maxNumber: Double) {
    val ctx = getReactContext() ?: return
    JPushInterface.setMaxGeofenceNumber(ctx, maxNumber.toInt())
  }

  override fun deleteGeofence(geoFenceID: String) {
    val ctx = getReactContext() ?: return
    JPushInterface.deleteGeofence(ctx, geoFenceID)
  }

  override fun pageEnterTo(pageName: String) {
    // Handled on iOS; no-op on Android
  }

  override fun pageLeave(pageName: String) {
    // Handled on iOS; no-op on Android
  }

  override fun setCollectControl(
    imei: Boolean,
    imsi: Boolean,
    mac: Boolean,
    wifi: Boolean,
    bssid: Boolean,
    ssid: Boolean,
    cell: Boolean,
    gps: Boolean
  ) {
    val ctx = getReactContext() ?: return
    val control = JPushCollectControl.Builder()
      .imei(imei)
      .imsi(imsi)
      .mac(mac)
      .wifi(wifi)
      .bssid(bssid)
      .ssid(ssid)
      .cell(cell)
      .build()
    JPushInterface.setCollectControl(ctx, control)
  }

  override fun setAuth(auth: Boolean) {
    val ctx = getReactContext()
    JCollectionAuth.setAuth(ctx, auth)
  }

  override fun setCountryCode(code: String) {
    val ctx = getReactContext() ?: return
    if (code.isNotEmpty()) {
      JCoreInterface.setCountryCode(ctx, code)
    }
  }

  override fun enableAutoWakeup(enable: Boolean) {
    val ctx = getReactContext()
    JCollectionAuth.enableAutoWakeup(ctx, enable)
  }

  override fun enableSDKLocalLog(enable: Boolean, uploadJgToServer: Boolean) {
    val ctx = getReactContext() ?: return
    JCoreInterface.enableSDKLocalLog(ctx, enable, uploadJgToServer)
  }

  override fun readNewLogs(): String {
    val ctx = getReactContext() ?: return ""
    return JCoreInterface.readNewLogs(ctx) ?: ""
  }
}
