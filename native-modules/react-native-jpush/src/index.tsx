import {
  DeviceEventEmitter,
  type EmitterSubscription,
  NativeModules,
  Platform,
} from 'react-native';
import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeJpush } from './ReactNativeJpush.nitro';

/**
 * Non-production demo test AppKey used solely for automated native and JS tests.
 * Never used in production environments.
 */
export const DEMO_TEST_APP_KEY = '1f2027d3cfb1b51dac664c42';

export type Callback<T> = (result: T) => void;

export type Extra = {
  [key: string]: string;
};

export type Sequence = {
  sequence: number;
};

export type Tag = {
  tag: string;
};

export type Tags = {
  tags: string[];
};

export type Alias = {
  alias: string;
};

export type NotificationId = {
  notificationId: string | number;
};

export type JPushInitParams = {
  appKey: string;
  channel: string;
  production: boolean;
  titchannelle?: string;
};

export type JPushConnectEvent = {
  connectEnable: boolean;
};

export type JPushNotificationEvent = {
  messageID: string;
  title: string;
  content: string;
  badge?: string;
  ring?: string;
  extras?: Extra;
  notificationEventType:
    | 'notificationArrived'
    | 'notificationOpened'
    | 'notificationDismissed';
};

export type JPushLocalNotificationEvent = {
  messageID: string;
  title: string;
  content: string;
  extras?: Extra;
  notificationEventType:
    | 'notificationArrived'
    | 'notificationOpened'
    | 'notificationDismissed';
};

export type JPushCustomMessageEvent = {
  messageID: string;
  title?: string;
  content: string;
  extras?: Extra;
};

export type JPushNotifyButtonClickEvent = {
  msgId: string;
  platform: number;
  name: string;
  actionType: number;
  action: string;
  data: string;
};

export type JPushInappMessageEvent = {
  mesageId: string;
  title: string;
  content: string;
  target: string[];
  clickAction: string;
  extras?: Extra;
  inappEventType: 'inappShow' | 'inappClick';
};

export type JPushCommandEvent = {
  command: number;
  commandExtra: string;
  commandMessage: string;
  commandResult: number;
};

export type JPushTagAliasEvent = {
  code: number;
  sequence: number;
  tags?: string[] | string;
  tag?: string;
  tagEnable?: boolean;
  alias?: string;
  pros?: Record<string, string>;
};

export type JPushMobileNumberEvent = {
  code: number;
  sequence: number;
};

export const ConnectEvent = 'ConnectEvent';
export const NotificationEvent = 'NotificationEvent';
export const LocalNotificationEvent = 'LocalNotificationEvent';
export const CustomMessageEvent = 'CustomMessageEvent';
export const NotifyButtonClickEvent = 'NotifyButtonClickEvent';
export const InappMessageEvent = 'InappMessageEvent';
export const TagAliasEvent = 'TagAliasEvent';
export const MobileNumberEvent = 'MobileNumberEvent';
export const CommandEvent = 'CommandEvent';

let cachedHybridObject: ReactNativeJpush | null | undefined;

function getNitroModule(): ReactNativeJpush | null {
  if (cachedHybridObject !== undefined) {
    return cachedHybridObject;
  }
  try {
    cachedHybridObject =
      NitroModules.createHybridObject<ReactNativeJpush>('ReactNativeJpush');
  } catch {
    cachedHybridObject = null;
  }
  return cachedHybridObject;
}

function getLegacyJPushModule(): Record<string, any> | undefined {
  return (NativeModules as Record<string, any>)?.JPushModule;
}

function getLegacyJCoreModule(): Record<string, any> | undefined {
  return (NativeModules as Record<string, any>)?.JCoreModule;
}

const listeners = new Map<
  (...args: any[]) => void,
  EmitterSubscription | { remove: () => void }
>();

export class JCore {
  static setAuth(auth: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setAuth(Boolean(auth));
      return;
    }
    getLegacyJCoreModule()?.setAuth?.(Boolean(auth));
  }

  static setCountryCode(params: { code: string }): void {
    if (!params || typeof params.code !== 'string') {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setCountryCode(params.code);
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJCoreModule()?.setCountryCode?.(params);
    }
  }

  static enableAutoWakeup(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.enableAutoWakeup(Boolean(enable));
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJCoreModule()?.enableAutoWakeup?.(Boolean(enable));
    }
  }

  static enableSDKLocalLog(params: {
    enable: boolean;
    uploadJgToServer: boolean;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.enableSDKLocalLog(
        Boolean(params.enable),
        Boolean(params.uploadJgToServer)
      );
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJCoreModule()?.enableSDKLocalLog?.(params);
    }
  }

  static readNewLogs(callback: Callback<{ logs: string }>): void {
    if (typeof callback !== 'function') {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      const logs = nitro.readNewLogs();
      callback({ logs: logs || '' });
      return;
    }
    if (Platform.OS === 'android' && getLegacyJCoreModule()?.readNewLogs) {
      getLegacyJCoreModule()?.readNewLogs(callback);
    } else {
      callback({ logs: '' });
    }
  }
}

export default class JPush {
  static setLoggerEnable(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setDebugMode(Boolean(enable));
      return;
    }
    getLegacyJPushModule()?.setDebugMode?.(Boolean(enable));
  }

  static init(params?: JPushInitParams): void {
    const appKey = params?.appKey ?? '';
    const channel = params?.channel ?? 'dev';
    const production = Boolean(params?.production ?? false);
    const nitro = getNitroModule();
    if (nitro) {
      if (Platform.OS === 'android') {
        if (appKey) {
          nitro.setupWithConfig(appKey, channel, production);
        } else {
          nitro.initPush();
        }
      } else {
        nitro.setupWithConfig(appKey, channel, production);
      }
      return;
    }
    const legacy = getLegacyJPushModule();
    if (!legacy) {
      return;
    }
    if (Platform.OS === 'android') {
      legacy.init?.();
    } else {
      legacy.setupWithConfig?.(params ?? { appKey, channel, production });
    }
  }

  static getRegistrationID(callback: Callback<{ registerID: string }>): void {
    if (typeof callback !== 'function') {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      const registerID = nitro.getRegistrationID() || '';
      callback({ registerID });
      return;
    }
    const legacy = getLegacyJPushModule();
    if (!legacy) {
      callback({ registerID: '' });
      return;
    }
    if (Platform.OS === 'android') {
      legacy.getRegistrationID?.(callback);
    } else {
      legacy.getRegisterId?.(callback);
    }
  }

  static addTags(params: Sequence & Tags): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.addTags(params.sequence ?? -1, params.tags ?? []);
      return;
    }
    getLegacyJPushModule()?.addTags?.(params);
  }

  static updateTags(params: Sequence & Tags): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setTags(params.sequence ?? -1, params.tags ?? []);
      return;
    }
    getLegacyJPushModule()?.setTags?.(params);
  }

  static deleteTag(
    params: Sequence & (Tags | { tag: string[] | string })
  ): void {
    if (!params) {
      return;
    }
    const rawTags =
      'tags' in params
        ? params.tags
        : Array.isArray(params.tag)
          ? params.tag
          : [params.tag];
    const normalized = {
      sequence: params.sequence ?? -1,
      tags: rawTags ?? [],
    };
    const nitro = getNitroModule();
    if (nitro) {
      nitro.deleteTags(normalized.sequence, normalized.tags);
      return;
    }
    getLegacyJPushModule()?.deleteTags?.(normalized);
  }

  static deleteTags(params: Sequence): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.cleanTags(params.sequence ?? -1);
      return;
    }
    getLegacyJPushModule()?.cleanTags?.(params);
  }

  static queryTag(params: Sequence & Tag): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.checkTagBindState(params.sequence ?? -1, params.tag ?? '');
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'android') {
      legacy?.checkTagBindState?.(params);
    } else {
      legacy?.validTag?.(params);
    }
  }

  static queryTags(params: Sequence): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.getAllTags(params.sequence ?? -1);
      return;
    }
    getLegacyJPushModule()?.getAllTags?.(params);
  }

  static setAlias(params: Sequence & Alias): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setAlias(params.sequence ?? -1, params.alias ?? '');
      return;
    }
    getLegacyJPushModule()?.setAlias?.(params);
  }

  static deleteAlias(params: Sequence): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.deleteAlias(params.sequence ?? -1);
      return;
    }
    getLegacyJPushModule()?.deleteAlias?.(params);
  }

  static queryAlias(params: Sequence): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.getAlias(params.sequence ?? -1);
      return;
    }
    getLegacyJPushModule()?.getAlias?.(params);
  }

  static setProperties(
    params: Sequence & {
      pros?: Record<string, any>;
      properties?: Record<string, any>;
    }
  ): void {
    if (!params) {
      return;
    }
    const propsObj = params.pros ?? params.properties ?? {};
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setProperties(params.sequence ?? -1, JSON.stringify(propsObj));
      return;
    }
    getLegacyJPushModule()?.setProperties?.(params);
  }

  static deleteProperties(
    params: Sequence & {
      pros?: Record<string, any>;
      properties?: Record<string, any>;
    }
  ): void {
    if (!params) {
      return;
    }
    const propsObj = params.pros ?? params.properties ?? {};
    const nitro = getNitroModule();
    if (nitro) {
      nitro.deleteProperties(params.sequence ?? -1, Object.keys(propsObj));
      return;
    }
    getLegacyJPushModule()?.deleteProperties?.(params);
  }

  static cleanProperties(params?: Sequence): void {
    const seq = params?.sequence ?? -1;
    const nitro = getNitroModule();
    if (nitro) {
      nitro.cleanProperties(seq);
      return;
    }
    getLegacyJPushModule()?.cleanProperties?.(params ?? { sequence: seq });
  }

  static pageEnterTo(pageName: string): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.pageEnterTo(pageName ?? '');
      return;
    }
    if (Platform.OS !== 'android') {
      getLegacyJPushModule()?.pageEnterTo?.(pageName);
    }
  }

  static pageLeave(pageName: string): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.pageLeave(pageName ?? '');
      return;
    }
    if (Platform.OS !== 'android') {
      getLegacyJPushModule()?.pageLeave?.(pageName);
    }
  }

  static setMobileNumber(params: { mobileNumber: string } & Sequence): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setMobileNumber(params.sequence ?? -1, params.mobileNumber ?? '');
      return;
    }
    getLegacyJPushModule()?.setMobileNumber?.(params);
  }

  static initCrashHandler(): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.initCrashHandler();
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'android') {
      legacy?.initCrashHandler?.();
    } else {
      legacy?.crashLogON?.(null);
    }
  }

  static setPowerSaveMode(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setPowerSaveMode(Boolean(enable));
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setPowerSaveMode?.(Boolean(enable));
    }
  }

  static isNotificationEnabled(callback: Callback<boolean>): void {
    if (typeof callback !== 'function') {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      callback(Boolean(nitro.isNotificationEnabled()));
      return;
    }
    const legacy = getLegacyJPushModule();
    if (legacy?.isNotificationEnabled) {
      legacy.isNotificationEnabled((res: boolean | number) => {
        callback(Boolean(res));
      });
    } else {
      callback(false);
    }
  }

  static addLocalNotification(params: {
    messageID: string;
    title: string;
    content: string;
    extras?: Extra;
    broadcastTime?: string;
    builderName?: string;
    category?: string;
    priority?: number;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.addLocalNotification(
        params.messageID ?? '',
        params.title ?? '',
        params.content ?? '',
        params.broadcastTime ?? '0',
        params.extras ? JSON.stringify(params.extras) : '{}'
      );
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'android') {
      legacy?.addLocalNotification?.(params);
    } else {
      legacy?.addNotification?.(params);
    }
  }

  static removeLocalNotification(params: { messageID: string }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.removeLocalNotification(params.messageID ?? '');
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'android') {
      legacy?.removeLocalNotification?.(params);
    } else {
      legacy?.removeNotification?.(params);
    }
  }

  static clearLocalNotifications(): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.clearLocalNotifications();
      return;
    }
    getLegacyJPushModule()?.clearLocalNotifications?.();
  }

  static clearAllNotifications(): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.clearAllNotifications();
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.clearAllNotifications?.();
    }
  }

  static clearNotificationById(params: NotificationId): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.clearNotificationById(Number(params.notificationId) || 0);
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.clearNotificationById?.(params);
    }
  }

  static setMaxGeofenceNumber(params: { geoFenceMaxNumber: number }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setMaxGeofenceNumber(Number(params.geoFenceMaxNumber) || 10);
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'android') {
      legacy?.setMaxGeofenceNumber?.(params);
    } else {
      legacy?.setGeofeneceMaxCount?.(params);
    }
  }

  static deleteGeofence(params: { geoFenceID: string }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.deleteGeofence(params.geoFenceID ?? '');
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'android') {
      legacy?.deleteGeofence?.(params);
    } else {
      legacy?.removeGeofenceWithIdentifier?.(params);
    }
  }

  static addConnectEventListener(callback: Callback<JPushConnectEvent>): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(ConnectEvent, (result) => {
        callback(result);
      })
    );
  }

  static addCommandEventListener(callback: Callback<JPushCommandEvent>): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(CommandEvent, (result) => {
        callback(result);
      })
    );
  }

  static addNotificationListener(
    callback: Callback<JPushNotificationEvent>
  ): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(NotificationEvent, (result) => {
        callback(result);
      })
    );
  }

  static addLocalNotificationListener(
    callback: Callback<JPushLocalNotificationEvent>
  ): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(LocalNotificationEvent, (result) => {
        callback(result);
      })
    );
  }

  static addCustomMessageListener(
    callback: Callback<JPushCustomMessageEvent>
  ): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(CustomMessageEvent, (result) => {
        callback(result);
      })
    );
  }

  static addNotifyButtonClickListener(
    callback: Callback<JPushNotifyButtonClickEvent>
  ): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(NotifyButtonClickEvent, (result) => {
        callback(result);
      })
    );
  }

  static addInappMessageListener(
    callback: Callback<JPushInappMessageEvent>
  ): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(InappMessageEvent, (result) => {
        callback(result);
      })
    );
  }

  static addTagAliasListener(callback: Callback<JPushTagAliasEvent>): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(TagAliasEvent, (result) => {
        callback(result);
      })
    );
  }

  static addMobileNumberListener(
    callback: Callback<JPushMobileNumberEvent>
  ): void {
    if (typeof callback !== 'function') {
      return;
    }
    listeners.set(
      callback,
      DeviceEventEmitter.addListener(MobileNumberEvent, (message) => {
        callback(message);
      })
    );
  }

  static removeListener(callback: (...args: any[]) => void): void {
    const sub = listeners.get(callback);
    if (!sub) {
      return;
    }
    sub.remove();
    listeners.delete(callback);
  }

  static requestPermission(): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.requestPermission();
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.requestPermission?.();
    }
  }

  static stopPush(): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.stopPush();
      return;
    }
    getLegacyJPushModule()?.stopPush?.();
  }

  static resumePush(): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.resumePush();
      return;
    }
    getLegacyJPushModule()?.resumePush?.();
  }

  static isPushStopped(callback: Callback<boolean>): void {
    if (typeof callback !== 'function') {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      callback(Boolean(nitro.isPushStopped()));
      return;
    }
    if (Platform.OS === 'android' && getLegacyJPushModule()?.isPushStopped) {
      getLegacyJPushModule()?.isPushStopped(callback);
    } else {
      callback(false);
    }
  }

  static setPushTime(params: {
    pushTimeDays: number[];
    pushTimeStartHour: number;
    pushTimeEndHour: number;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setPushTime(
        params.pushTimeDays ?? [],
        params.pushTimeStartHour ?? 0,
        params.pushTimeEndHour ?? 23
      );
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setPushTime?.(params);
    }
  }

  static setSilenceTime(params: {
    silenceTimeStartHour: number;
    silenceTimeStartMinute: number;
    silenceTimeEndHour: number;
    silenceTimeEndMinute: number;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setSilenceTime(
        params.silenceTimeStartHour ?? 0,
        params.silenceTimeStartMinute ?? 0,
        params.silenceTimeEndHour ?? 0,
        params.silenceTimeEndMinute ?? 0
      );
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setSilenceTime?.(params);
    }
  }

  static setLatestNotificationNumber(params: {
    notificationMaxNumber: number;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setLatestNotificationNumber(params.notificationMaxNumber ?? 5);
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setLatestNotificationNumber?.(params);
    }
  }

  static setChannel(params: { channel: string }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setChannel(params.channel ?? '');
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setChannel?.(params);
    }
  }

  static setChannelAndSound(params: {
    channel: string;
    channelId?: string;
    sound?: string;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setChannelAndSound(
        params.channel ?? '',
        params.channelId ?? '',
        params.sound ?? ''
      );
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setChannelAndSound?.(params);
    }
  }

  static setBadge(params: { badge: number; appBadge?: number }): void {
    if (!params) {
      return;
    }
    const badge = params.badge ?? 0;
    const appBadge = params.appBadge ?? badge;
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setBadge(badge, appBadge);
      return;
    }
    const legacy = getLegacyJPushModule();
    if (Platform.OS === 'ios') {
      legacy?.setBadge?.(params);
    } else if (Platform.OS === 'android') {
      legacy?.setBadgeNumber?.(params);
    }
  }

  static setLinkMergeEnable(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setLinkMergeEnable(Boolean(enable));
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setLinkMergeEnable?.(Boolean(enable));
    }
  }

  static setSmartPushEnable(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setSmartPushEnable(Boolean(enable));
      return;
    }
    getLegacyJPushModule()?.setSmartPushEnable?.(Boolean(enable));
  }

  static setDataInsightsEnable(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setDataInsightsEnable(Boolean(enable));
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setDataInsightsEnable?.(Boolean(enable));
    }
  }

  static setGeofenceEnable(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setGeofenceEnable(Boolean(enable));
      return;
    }
    if (Platform.OS === 'android') {
      getLegacyJPushModule()?.setGeofenceEnable?.(Boolean(enable));
    }
  }

  static setCollectControl(params: {
    cell?: boolean;
    bssid?: boolean;
    imei?: boolean;
    imsi?: boolean;
    mac?: boolean;
    wifi?: boolean;
    ssid?: boolean;
    gps?: boolean;
  }): void {
    if (!params) {
      return;
    }
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setCollectControl(
        params.imei ?? true,
        params.imsi ?? true,
        params.mac ?? true,
        params.wifi ?? true,
        params.bssid ?? true,
        params.ssid ?? true,
        params.cell ?? true,
        params.gps ?? true
      );
      return;
    }
    getLegacyJPushModule()?.setCollectControl?.(params);
  }

  static setBackgroundEnable(enable: boolean): void {
    const nitro = getNitroModule();
    if (nitro) {
      nitro.setBackgroundEnable(Boolean(enable));
      return;
    }
    if (Platform.OS === 'ios') {
      getLegacyJPushModule()?.setBackgroundEnable?.(Boolean(enable));
    }
  }
}

export { JPush };
export type { ReactNativeJpush };
