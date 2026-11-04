import type { HybridObject } from 'react-native-nitro-modules';

export interface ReactNativeJpush
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  setDebugMode(enable: boolean): void;
  initPush(): void;
  setupWithConfig(appKey: string, channel: string, production: boolean): void;
  loadJS(): void;
  stopPush(): void;
  resumePush(): void;
  isPushStopped(): boolean;
  getRegistrationID(): string;
  isNotificationEnabled(callback: (enabled: boolean) => void): void;

  addTags(sequence: number, tags: string[]): void;
  setTags(sequence: number, tags: string[]): void;
  deleteTags(sequence: number, tags: string[]): void;
  cleanTags(sequence: number): void;
  getAllTags(sequence: number): void;
  checkTagBindState(sequence: number, tag: string): void;

  setAlias(sequence: number, alias: string): void;
  deleteAlias(sequence: number): void;
  getAlias(sequence: number): void;

  setProperties(sequence: number, propertiesJson: string): void;
  deleteProperties(sequence: number, keys: string[]): void;
  cleanProperties(sequence: number): void;

  setBadge(badge: number, appBadge: number): void;
  setMobileNumber(sequence: number, mobileNumber: string): void;
  initCrashHandler(): void;
  setPowerSaveMode(enable: boolean): void;

  addLocalNotification(
    messageID: string,
    title: string,
    content: string,
    broadcastTime: string,
    extrasJson: string
  ): void;
  removeLocalNotification(messageID: string): void;
  clearLocalNotifications(): void;
  clearAllNotifications(): void;
  clearNotificationById(notificationId: number): void;

  requestPermission(): void;
  setPushTime(
    days: number[],
    startHour: number,
    endHour: number
  ): void;
  setSilenceTime(
    startHour: number,
    startMinute: number,
    endHour: number,
    endMinute: number
  ): void;
  setLatestNotificationNumber(maxNumber: number): void;
  setChannel(channel: string): void;
  setChannelAndSound(channel: string, channelId: string, sound: string): void;

  setLinkMergeEnable(enable: boolean): void;
  setSmartPushEnable(enable: boolean): void;
  setDataInsightsEnable(enable: boolean): void;
  setGeofenceEnable(enable: boolean): void;
  setBackgroundEnable(enable: boolean): void;
  setMaxGeofenceNumber(maxNumber: number): void;
  deleteGeofence(geoFenceID: string): void;
  pageEnterTo(pageName: string): void;
  pageLeave(pageName: string): void;
  setCollectControl(
    imei: boolean,
    imsi: boolean,
    mac: boolean,
    wifi: boolean,
    bssid: boolean,
    ssid: boolean,
    cell: boolean,
    gps: boolean
  ): void;

  setAuth(auth: boolean): void;
  setCountryCode(code: string): void;
  enableAutoWakeup(enable: boolean): void;
  enableSDKLocalLog(enable: boolean, uploadJgToServer: boolean): void;
  readNewLogs(): string;

  addEventListener(
    callback: (eventName: string, payloadJson: string) => void
  ): number;
  removeEventListener(id: number): void;
}
