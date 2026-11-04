let nextNitroListenerId = 1;
const mockNitroListeners = new Map<
  number,
  (eventName: string, payloadJson: string) => void
>();

const mockHybridObject = {
  setDebugMode: jest.fn(),
  initPush: jest.fn(),
  setupWithConfig: jest.fn(),
  loadJS: jest.fn(),
  stopPush: jest.fn(),
  resumePush: jest.fn(),
  isPushStopped: jest.fn(() => false),
  getRegistrationID: jest.fn(() => '1a0018970a900100200'),
  isNotificationEnabled: jest.fn((cb: (enabled: boolean) => void) => cb(true)),
  addTags: jest.fn(),
  setTags: jest.fn(),
  deleteTags: jest.fn(),
  cleanTags: jest.fn(),
  getAllTags: jest.fn(),
  checkTagBindState: jest.fn(),
  setAlias: jest.fn(),
  deleteAlias: jest.fn(),
  getAlias: jest.fn(),
  setProperties: jest.fn(),
  deleteProperties: jest.fn(),
  cleanProperties: jest.fn(),
  setBadge: jest.fn(),
  setMobileNumber: jest.fn(),
  initCrashHandler: jest.fn(),
  setPowerSaveMode: jest.fn(),
  addLocalNotification: jest.fn(),
  removeLocalNotification: jest.fn(),
  clearLocalNotifications: jest.fn(),
  clearAllNotifications: jest.fn(),
  clearNotificationById: jest.fn(),
  requestPermission: jest.fn(),
  setPushTime: jest.fn(),
  setSilenceTime: jest.fn(),
  setLatestNotificationNumber: jest.fn(),
  setChannel: jest.fn(),
  setChannelAndSound: jest.fn(),
  setLinkMergeEnable: jest.fn(),
  setSmartPushEnable: jest.fn(),
  setDataInsightsEnable: jest.fn(),
  setGeofenceEnable: jest.fn(),
  setBackgroundEnable: jest.fn(),
  setMaxGeofenceNumber: jest.fn(),
  deleteGeofence: jest.fn(),
  pageEnterTo: jest.fn(),
  pageLeave: jest.fn(),
  setCollectControl: jest.fn(),
  setAuth: jest.fn(),
  setCountryCode: jest.fn(),
  enableAutoWakeup: jest.fn(),
  enableSDKLocalLog: jest.fn(),
  readNewLogs: jest.fn(() => '[JCore] test log line'),
  addEventListener: jest.fn(
    (cb: (eventName: string, payloadJson: string) => void) => {
      const id = nextNitroListenerId++;
      mockNitroListeners.set(id, cb);
      return id;
    }
  ),
  removeEventListener: jest.fn((id: number) => {
    mockNitroListeners.delete(id);
  }),
};

const mockListeners = new Map<string, Set<(payload: any) => void>>();

jest.mock('react-native-nitro-modules', () => ({
  NitroModules: {
    createHybridObject: jest.fn(() => mockHybridObject),
  },
}));

jest.mock('react-native', () => ({
  Platform: {
    OS: 'ios',
  },
  NativeModules: {
    JPushModule: {},
    JCoreModule: {},
  },
  DeviceEventEmitter: {
    addListener: jest.fn((eventName: string, handler: (payload: any) => void) => {
      if (!mockListeners.has(eventName)) {
        mockListeners.set(eventName, new Set());
      }
      mockListeners.get(eventName)!.add(handler);
      return {
        remove: jest.fn(() => {
          mockListeners.get(eventName)?.delete(handler);
        }),
      };
    }),
  },
}));

import { DeviceEventEmitter } from 'react-native';
import JPush, {
  DEMO_TEST_APP_KEY,
  DEMO_TEST_PACKAGE_NAME,
  JCore,
  ConnectEvent,
  NotificationEvent,
  TagAliasEvent,
} from '../index';

function emitMockNitroEvent(eventName: string, payload: Record<string, any>) {
  const json = JSON.stringify(payload);
  mockNitroListeners.forEach((cb) => cb(eventName, json));
}

describe('@onekeyfe/react-native-jpush JS & Nitro API contract', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockListeners.clear();
    mockNitroListeners.clear();
  });

  it('exports DEMO_TEST_APP_KEY and DEMO_TEST_PACKAGE_NAME (non-production sandbox fixtures)', () => {
    // Non-production demo test AppKey and sandbox package name registered solely for automated testing.
    expect(DEMO_TEST_APP_KEY).toBe('1f2027d3cfb1b51dac664c42');
    expect(DEMO_TEST_PACKAGE_NAME).toBe('com.devstudio.msgbench');
  });

  it('initializes JPush with DEMO_TEST_APP_KEY and resolves registrationID via Nitro', () => {
    JPush.setLoggerEnable(true);
    expect(mockHybridObject.setDebugMode).toHaveBeenCalledWith(true);

    JPush.init({
      appKey: DEMO_TEST_APP_KEY,
      channel: 'dev',
      production: false,
    });
    expect(mockHybridObject.setupWithConfig).toHaveBeenCalledWith(
      '1f2027d3cfb1b51dac664c42',
      'dev',
      false
    );

    const callback = jest.fn();
    JPush.getRegistrationID(callback);
    expect(callback).toHaveBeenCalledWith({
      registerID: '1a0018970a900100200',
    });

    const notifEnabledCallback = jest.fn();
    JPush.isNotificationEnabled(notifEnabledCallback);
    expect(mockHybridObject.isNotificationEnabled).toHaveBeenCalledTimes(1);
    expect(notifEnabledCallback).toHaveBeenCalledWith(true);
  });

  it('dispatches tag, alias, properties, badge, and mobile number calls to Nitro', () => {
    JPush.addTags({ sequence: 1, tags: ['btc', 'eth'] });
    expect(mockHybridObject.addTags).toHaveBeenCalledWith(1, ['btc', 'eth']);

    JPush.updateTags({ sequence: 2, tags: ['sol'] });
    expect(mockHybridObject.setTags).toHaveBeenCalledWith(2, ['sol']);

    JPush.deleteTag({ sequence: 3, tags: ['eth'] });
    expect(mockHybridObject.deleteTags).toHaveBeenCalledWith(3, ['eth']);

    JPush.deleteTags({ sequence: 4 });
    expect(mockHybridObject.cleanTags).toHaveBeenCalledWith(4);

    JPush.queryTag({ sequence: 5, tag: 'btc' });
    expect(mockHybridObject.checkTagBindState).toHaveBeenCalledWith(5, 'btc');

    JPush.queryTags({ sequence: 6 });
    expect(mockHybridObject.getAllTags).toHaveBeenCalledWith(6);

    JPush.setAlias({ sequence: 7, alias: 'onekey_user_01' });
    expect(mockHybridObject.setAlias).toHaveBeenCalledWith(7, 'onekey_user_01');

    JPush.deleteAlias({ sequence: 8 });
    expect(mockHybridObject.deleteAlias).toHaveBeenCalledWith(8);

    JPush.queryAlias({ sequence: 9 });
    expect(mockHybridObject.getAlias).toHaveBeenCalledWith(9);

    JPush.setProperties({ sequence: 10, pros: { vip: 'true' } });
    expect(mockHybridObject.setProperties).toHaveBeenCalledWith(
      10,
      JSON.stringify({ vip: 'true' })
    );

    JPush.deleteProperties({ sequence: 11, pros: { vip: 'true' } });
    expect(mockHybridObject.deleteProperties).toHaveBeenCalledWith(11, ['vip']);

    JPush.cleanProperties({ sequence: 12 });
    expect(mockHybridObject.cleanProperties).toHaveBeenCalledWith(12);

    JPush.setBadge({ badge: 5, appBadge: 3 });
    expect(mockHybridObject.setBadge).toHaveBeenCalledWith(5, 3);

    JPush.setMobileNumber({ sequence: 13, mobileNumber: '13800138000' });
    expect(mockHybridObject.setMobileNumber).toHaveBeenCalledWith(
      13,
      '13800138000'
    );
  });

  it('dispatches JCore compliance and log methods to Nitro', () => {
    JCore.setAuth(true);
    expect(mockHybridObject.setAuth).toHaveBeenCalledWith(true);

    JCore.setCountryCode({ code: 'CN' });
    expect(mockHybridObject.setCountryCode).toHaveBeenCalledWith('CN');

    JCore.enableAutoWakeup(false);
    expect(mockHybridObject.enableAutoWakeup).toHaveBeenCalledWith(false);

    JCore.enableSDKLocalLog({ enable: true, uploadJgToServer: false });
    expect(mockHybridObject.enableSDKLocalLog).toHaveBeenCalledWith(
      true,
      false
    );

    const logCallback = jest.fn();
    JCore.readNewLogs(logCallback);
    expect(logCallback).toHaveBeenCalledWith({
      logs: '[JCore] test log line',
    });
  });

  it('registers and removes pure Nitro event listeners without DeviceEventEmitter', () => {
    const onConnect = jest.fn();
    const onNotification = jest.fn();
    const onTagAlias = jest.fn();

    JPush.addConnectEventListener(onConnect);
    JPush.addNotificationListener(onNotification);
    JPush.addTagAliasListener(onTagAlias);

    expect(mockHybridObject.addEventListener).toHaveBeenCalledTimes(1);
    expect(mockHybridObject.loadJS).toHaveBeenCalledTimes(1);
    expect(DeviceEventEmitter.addListener).not.toHaveBeenCalled();

    emitMockNitroEvent(ConnectEvent, { connectEnable: true });
    emitMockNitroEvent(NotificationEvent, {
      messageID: '1001',
      title: 'OneKey',
      content: 'Price Alert',
      notificationEventType: 'notificationArrived',
    });
    emitMockNitroEvent(TagAliasEvent, {
      code: 0,
      sequence: 1,
      tags: ['btc'],
    });

    expect(onConnect).toHaveBeenCalledWith({ connectEnable: true });
    expect(onNotification).toHaveBeenCalledWith(
      expect.objectContaining({
        messageID: '1001',
        notificationEventType: 'notificationArrived',
      })
    );
    expect(onTagAlias).toHaveBeenCalledWith({
      code: 0,
      sequence: 1,
      tags: ['btc'],
    });

    JPush.removeListener(onConnect);
    emitMockNitroEvent(ConnectEvent, { connectEnable: false });
    expect(onConnect).toHaveBeenCalledTimes(1);

    JPush.removeListener(onNotification);
    JPush.removeListener(onTagAlias);
    expect(mockHybridObject.removeEventListener).toHaveBeenCalledTimes(1);
  });

  it('buffers cold-start NotificationEvent flushed during addConnectEventListener until addNotificationListener registers', () => {
    const onConnect = jest.fn();
    const onNotification = jest.fn();

    mockHybridObject.addEventListener.mockImplementationOnce(
      (cb: (eventName: string, payloadJson: string) => void) => {
        const id = nextNitroListenerId++;
        mockNitroListeners.set(id, cb);
        // Simulate native flushing a cold-start NotificationEvent when the first Nitro listener (addConnectEventListener) attaches
        cb(
          NotificationEvent,
          JSON.stringify({
            messageID: 'cold_apns_999',
            title: 'Cold Launch',
            content: 'Opened from notification',
            notificationEventType: 'notificationOpened',
          })
        );
        return id;
      }
    );

    JPush.init({
      appKey: DEMO_TEST_APP_KEY,
      channel: 'dev',
      production: false,
    });
    JPush.addConnectEventListener(onConnect);
    expect(onNotification).not.toHaveBeenCalled();

    JPush.addNotificationListener(onNotification);
    expect(onNotification).toHaveBeenCalledWith(
      expect.objectContaining({
        messageID: 'cold_apns_999',
        notificationEventType: 'notificationOpened',
      })
    );

    JPush.removeListener(onConnect);
    JPush.removeListener(onNotification);
  });
});
