import Foundation
import UIKit
import UserNotifications
import NitroModules
import ReactNativeNativeLogger

class ReactNativeJpush: HybridReactNativeJpushSpec {
  private static let logTag = "RN-JPush"
  private var localLogEnabled: Bool = false
  private let listenerLock = NSLock()
  private var registeredListenerIds = Set<Int>()

  deinit {
    listenerLock.lock()
    let ids = registeredListenerIds
    registeredListenerIds.removeAll()
    listenerLock.unlock()
    for id in ids {
      RCTJPushModule.unregisterNitroEventListener(id)
    }
  }

  func setDebugMode(enable: Bool) throws {
    if enable {
      JPUSHService.setDebugMode()
    } else {
      JPUSHService.setLogOFF()
    }
    OneKeyLog.info(Self.logTag, "setDebugMode enable=\(enable)")
  }

  func initPush() throws {
    OneKeyLog.info(Self.logTag, "initPush")
    RCTJPushModule.setupOrRefreshJPushNativeState()
    let config = JCOREConfiguration.shared()
    if let appKey = config.appKey, !appKey.isEmpty {
      JPUSHService.setup(
        withOption: nil,
        appKey: appKey,
        channel: config.channel,
        apsForProduction: config.isProduction
      )
    }
    RCTJPushModule.flushQueuedNotifications()
  }

  func setupWithConfig(appKey: String, channel: String, production: Bool) throws {
    let resolvedChannel = channel.isEmpty ? "dev" : channel
    OneKeyLog.info(Self.logTag, "setupWithConfig channel=\(resolvedChannel) production=\(production)")
    RCTJPushModule.setupOrRefreshJPushNativeState()
    if !appKey.isEmpty {
      JPUSHService.setup(
        withOption: nil,
        appKey: appKey,
        channel: resolvedChannel,
        apsForProduction: production
      )
    }
    RCTJPushModule.flushQueuedNotifications()
  }

  func loadJS() throws {
    RCTJPushModule.flushQueuedNotifications()
  }

  func stopPush() throws {
    OneKeyLog.info(Self.logTag, "stopPush")
    JPUSHService.turnOFFPush()
  }

  func resumePush() throws {
    OneKeyLog.info(Self.logTag, "resumePush")
    JPUSHService.turnOnPush()
  }

  func isPushStopped() throws -> Bool {
    return JCOREConfiguration.shared().pushStopped
  }

  func getRegistrationID() throws -> String {
    return JPUSHService.registrationID() ?? ""
  }

  func isNotificationEnabled() throws -> Bool {
    guard let bundleId = Bundle.main.bundleIdentifier, !bundleId.isEmpty else {
      return false
    }
    let semaphore = DispatchSemaphore(value: 0)
    var enabled = false
    UNUserNotificationCenter.current().getNotificationSettings { settings in
      let status = settings.authorizationStatus
      if #available(iOS 14.0, *) {
        enabled = status == .authorized || status == .provisional || status == .ephemeral
      } else {
        enabled = status == .authorized || status == .provisional
      }
      semaphore.signal()
    }
    _ = semaphore.wait(timeout: .now() + .milliseconds(500))
    return enabled
  }

  func addTags(sequence: Double, tags: [String]) throws {
    let seq = Int(sequence)
    let tagSet = Set(tags)
    JPUSHService.addTags(tagSet, completion: { resCode, iTags, resSeq in
      OneKeyLog.info(Self.logTag, "onTagOperatorResult seq=\(resSeq) code=\(resCode)")
      let tagArray = (iTags as? Set<String>).map { Array($0) } ?? tags
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "tags": tagArray
        ]
      )
    }, seq: seq)
  }

  func setTags(sequence: Double, tags: [String]) throws {
    let seq = Int(sequence)
    let tagSet = Set(tags)
    JPUSHService.setTags(tagSet, completion: { resCode, iTags, resSeq in
      OneKeyLog.info(Self.logTag, "onTagOperatorResult seq=\(resSeq) code=\(resCode)")
      let tagArray = (iTags as? Set<String>).map { Array($0) } ?? tags
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "tags": tagArray
        ]
      )
    }, seq: seq)
  }

  func deleteTags(sequence: Double, tags: [String]) throws {
    let seq = Int(sequence)
    let tagSet = Set(tags)
    JPUSHService.deleteTags(tagSet, completion: { resCode, iTags, resSeq in
      OneKeyLog.info(Self.logTag, "onTagOperatorResult seq=\(resSeq) code=\(resCode)")
      let tagArray = (iTags as? Set<String>).map { Array($0) } ?? []
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "tags": tagArray
        ]
      )
    }, seq: seq)
  }

  func cleanTags(sequence: Double) throws {
    let seq = Int(sequence)
    JPUSHService.cleanTags({ resCode, _, resSeq in
      OneKeyLog.info(Self.logTag, "onTagOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq
        ]
      )
    }, seq: seq)
  }

  func getAllTags(sequence: Double) throws {
    let seq = Int(sequence)
    JPUSHService.getAllTags({ resCode, iTags, resSeq in
      OneKeyLog.info(Self.logTag, "onTagOperatorResult seq=\(resSeq) code=\(resCode)")
      let tagArray = (iTags as? Set<String>).map { Array($0) } ?? []
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "tags": tagArray
        ]
      )
    }, seq: seq)
  }

  func checkTagBindState(sequence: Double, tag: String) throws {
    let seq = Int(sequence)
    JPUSHService.validTag(tag, completion: { resCode, _, resSeq, isBind in
      OneKeyLog.info(Self.logTag, "onCheckTagOperatorResult seq=\(resSeq) code=\(resCode) tagEnable=\(isBind)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "tag": tag,
          "tagEnable": isBind
        ]
      )
    }, seq: seq)
  }

  func setAlias(sequence: Double, alias: String) throws {
    let seq = Int(sequence)
    JPUSHService.setAlias(alias, completion: { resCode, iAlias, resSeq in
      OneKeyLog.info(Self.logTag, "onAliasOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "alias": iAlias ?? alias
        ]
      )
    }, seq: seq)
  }

  func deleteAlias(sequence: Double) throws {
    let seq = Int(sequence)
    JPUSHService.deleteAlias({ resCode, _, resSeq in
      OneKeyLog.info(Self.logTag, "onAliasOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq
        ]
      )
    }, seq: seq)
  }

  func getAlias(sequence: Double) throws {
    let seq = Int(sequence)
    JPUSHService.getAlias({ resCode, iAlias, resSeq in
      OneKeyLog.info(Self.logTag, "onAliasOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "alias": iAlias ?? ""
        ]
      )
    }, seq: seq)
  }

  func setProperties(sequence: Double, propertiesJson: String) throws {
    let seq = Int(sequence)
    guard let data = propertiesJson.data(using: .utf8),
          let dict = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] else {
      return
    }
    JPUSHService.setProperties(dict, completion: { resCode, props, resSeq in
      OneKeyLog.info(Self.logTag, "onPropertyOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "pros": props ?? dict
        ]
      )
    }, seq: seq)
  }

  func deleteProperties(sequence: Double, keys: [String]) throws {
    let seq = Int(sequence)
    let keySet = Set(keys)
    JPUSHService.deleteProperties(keySet, completion: { resCode, props, resSeq in
      OneKeyLog.info(Self.logTag, "onPropertyOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "pros": props ?? [:]
        ]
      )
    }, seq: seq)
  }

  func cleanProperties(sequence: Double) throws {
    let seq = Int(sequence)
    JPUSHService.cleanProperties({ resCode, props, resSeq in
      OneKeyLog.info(Self.logTag, "onPropertyOperatorResult seq=\(resSeq) code=\(resCode)")
      RCTJPushModule.emitEvent(
        withName: "TagAliasEvent",
        payload: [
          "code": resCode,
          "sequence": resSeq,
          "pros": props ?? [:]
        ]
      )
    }, seq: seq)
  }

  func setBadge(badge: Double, appBadge: Double) throws {
    let badgeInt = Int(badge)
    let appBadgeInt = Int(appBadge)
    if badgeInt >= 0 {
      _ = JPUSHService.setBadge(badgeInt)
    }
    if appBadgeInt >= 0 {
      DispatchQueue.main.async {
        UIApplication.shared.applicationIconBadgeNumber = appBadgeInt
      }
    }
  }

  func setMobileNumber(sequence: Double, mobileNumber: String) throws {
    let seq = Int(sequence)
    JPUSHService.setMobileNumber(mobileNumber) { error in
      let code = (error as NSError?)?.code ?? 0
      OneKeyLog.info(Self.logTag, "onMobileNumberOperatorResult seq=\(seq) code=\(code)")
      RCTJPushModule.emitEvent(
        withName: "MobileNumberEvent",
        payload: [
          "code": code,
          "sequence": seq
        ]
      )
    }
  }

  func initCrashHandler() throws {
    JPUSHService.crashLogON()
  }

  func setPowerSaveMode(enable: Bool) throws {
    // Android-only; no-op on iOS
  }

  func addLocalNotification(
    messageID: String,
    title: String,
    content: String,
    broadcastTime: String,
    extrasJson: String
  ) throws {
    let notifContent = JPushNotificationContent()
    notifContent.title = title
    notifContent.body = content
    if #available(iOS 15.0, *) {
      notifContent.interruptionLevel = 1
    }
    var userInfo: [String: Any] = [
      "messageID": messageID,
      "title": title,
      "content": content
    ]
    if let data = extrasJson.data(using: .utf8),
       let extras = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any],
       !extras.isEmpty {
      userInfo["extras"] = extras
    }
    notifContent.userInfo = userInfo

    let trigger = JPushNotificationTrigger()
    var fireDate = Date(timeIntervalSinceNow: 1.0)
    if let ms = Double(broadcastTime), ms > 0 {
      fireDate = Date(timeIntervalSince1970: ms / 1000.0)
    }
    let comps = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: fireDate)
    trigger.dateComponents = comps

    let request = JPushNotificationRequest()
    request.requestIdentifier = messageID
    request.content = notifContent
    request.trigger = trigger
    JPUSHService.addNotification(request)
  }

  func removeLocalNotification(messageID: String) throws {
    guard !messageID.isEmpty else { return }
    let identifier = JPushNotificationIdentifier()
    identifier.identifiers = [messageID]
    identifier.delivered = true
    JPUSHService.removeNotification(identifier)
  }

  func clearLocalNotifications() throws {
    JPUSHService.removeNotification(nil)
  }

  func clearAllNotifications() throws {
    JPUSHService.removeNotification(nil)
  }

  func clearNotificationById(notificationId: Double) throws {
    let idStr = String(Int(notificationId))
    let identifier = JPushNotificationIdentifier()
    identifier.identifiers = [idStr]
    identifier.delivered = true
    JPUSHService.removeNotification(identifier)
  }

  func requestPermission() throws {
    JPUSHService.requestNotificationAuthorization(nil)
  }

  func setPushTime(days: [Double], startHour: Double, endHour: Double) throws {
    // Android-only; no-op on iOS
  }

  func setSilenceTime(
    startHour: Double,
    startMinute: Double,
    endHour: Double,
    endMinute: Double
  ) throws {
    // Android-only; no-op on iOS
  }

  func setLatestNotificationNumber(maxNumber: Double) throws {
    // Android-only; no-op on iOS
  }

  func setChannel(channel: String) throws {
    if !channel.isEmpty {
      JCOREConfiguration.shared().channel = channel
    }
  }

  func setChannelAndSound(channel: String, channelId: String, sound: String) throws {
    if !channel.isEmpty {
      JCOREConfiguration.shared().channel = channel
    }
  }

  func setLinkMergeEnable(enable: Bool) throws {
    // Android-only; no-op on iOS
  }

  func setSmartPushEnable(enable: Bool) throws {
    JPUSHService.setSmartPushEnable(enable)
  }

  func setDataInsightsEnable(enable: Bool) throws {
    JPUSHService.setDataInsightsEnable(enable)
  }

  func setGeofenceEnable(enable: Bool) throws {
    JPUSHService.setLocationEanable(enable)
  }

  func setBackgroundEnable(enable: Bool) throws {
    JPUSHService.setBackgroundEnable(enable)
  }

  func setMaxGeofenceNumber(maxNumber: Double) throws {
    JPUSHService.setGeofeneceMaxCount(Int(maxNumber))
  }

  func deleteGeofence(geoFenceID: String) throws {
    JPUSHService.removeGeofence(withIdentifier: geoFenceID)
  }

  func pageEnterTo(pageName: String) throws {
    JPUSHService.pageEnter(to: pageName)
  }

  func pageLeave(pageName: String) throws {
    JPUSHService.pageLeave(pageName)
  }

  func setCollectControl(
    imei: Bool,
    imsi: Bool,
    mac: Bool,
    wifi: Bool,
    bssid: Bool,
    ssid: Bool,
    cell: Bool,
    gps: Bool
  ) throws {
    let control = JPushCollectControl()
    control.bssid = bssid
    control.ssid = ssid
    control.cell = cell
    control.gps = gps
    JPUSHService.setCollectControl(control)
  }

  func setAuth(auth: Bool) throws {
    JGInforCollectionAuth.jCollectionAuth { authInfo in
      authInfo.isAuth = auth
    }
  }

  func setCountryCode(code: String) throws {
    // Stored for diagnostic/config parity
  }

  func enableAutoWakeup(enable: Bool) throws {
    // Android-only; no-op on iOS
  }

  func enableSDKLocalLog(enable: Bool, uploadJgToServer: Bool) throws {
    localLogEnabled = enable
  }

  func readNewLogs() throws -> String {
    let logs = JCOREConfiguration.recentLogs().joined(separator: "\n")
    JCOREConfiguration.clearRecentLogs()
    return logs
  }

  func addEventListener(
    callback: @escaping (_ eventName: String, _ payloadJson: String) -> Void
  ) throws -> Double {
    let listenerId = RCTJPushModule.registerNitroEventListener { eventName, payloadJson in
      callback(eventName, payloadJson)
    }
    listenerLock.lock()
    registeredListenerIds.insert(listenerId)
    listenerLock.unlock()
    return Double(listenerId)
  }

  func removeEventListener(id: Double) throws {
    let listenerId = Int(id)
    listenerLock.lock()
    registeredListenerIds.remove(listenerId)
    listenerLock.unlock()
    RCTJPushModule.unregisterNitroEventListener(listenerId)
  }
}
