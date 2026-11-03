# Android Source Build (`JCore` + `JPush`)

## Overview

This directory contains a clean, human-readable Java source implementation of `jcore-android-5.2.0.jar` and `jpush-android-5.9.0.jar` for OneKey's Android notification runtime (`BaseMainApplication`, `JPushMethodVisitor`, `jcore-react-native`, and `jpush-react-native`).

### Module Layout

- `source/android/java/cn/jiguang/core/`
  - `JCoreStorage.java` — `SharedPreferences`, `MultiSpHelper`, and in-memory state store for registration credentials, SIS cache, tags, alias, properties, local SDK logs, and collection auth.
  - `JCoreCrypto.java` — `generateSeedKey` (`MD5("JCKP" + seed)`), `AES/CBC/PKCS5Padding` (32-byte ASCII hex key + 16-byte ASCII IV), `SM4-CBC` (full 32-round S-Box + key schedule), `RSA/ECB/PKCS1Padding` & `OAEP`, `MD5`, `SHA-1`, `HmacSHA256`, and `GZIP` helpers.
  - `JCoreWireCodec.java` — Asymmetric 24-byte Client->Server and 20-byte Server->Client TCP wire codec, 10-byte UDP SIS packet codec (`'U','G'`, `(0x11 << 24) | seed`), HTTP `/v3/report` auth/encryption builder, and binary command payloads (`Register cmd=0 ver=29`, `Login cmd=1 ver=27`, `Heartbeat cmd=2 ver=8`, `PushMessage cmd=3`, `MessageReceipt cmd=4 ver=3`, `DeviceToken cmd=13 ver=3`, `CtrlCommand cmd=25 ver=1`, `MobileNumber cmd=26 ver=1`, `ThirdToken cmd=27 ver=2`, `TagOperation cmd=28 ver=1`, `AliasOperation cmd=29 ver=1`, `VoIPToken cmd=33 ver=2`, `PropertyOperation cmd=38 ver=1`, `LiveActivityToken cmd=39 ver=1`).
  - `JCoreTcpEngine.java` — Background executor, 10-byte UDP & HTTPS SIS resolver, multi-IP waterfall failover (`fail_ips`), registered fast-login (`cmd=1`), `statusCode 108/1012` recovery, exponential backoff auto-reconnect, `ConnectivityManager` network recovery, pre-login FIFO request queue, HTTP `/v3/report` channel, and strict `HttpsURLConnection.getDefaultHostnameVerifier()` TLS verification.
- `source/android/java/cn/jiguang/internal/`
  - `JConstants.java`, `JCoreHelperAction.java`, `JCoreInternalHelper.java` (preserving `directHandle(Context, String, String, Bundle)` for `JPushMethodVisitor`), `ActionManager.java`.
- `source/android/java/cn/jiguang/api/` & `cn/jiguang/net/`
  - `JCoreManager.java`, `JCoreInterface.java`, `JCollectionAuth.java`, `DefaultHostVerifier.java` (validating target hostname and peer certificate verification via `HttpsURLConnection.getDefaultHostnameVerifier()`), `HttpUtils.java`.
- `source/android/java/cn/jpush/android/`
  - `api/` — `JPushInterface`, `CustomMessage`, `NotificationMessage`, `JPushMessage`, `CmdMessage`, `NotificationCustomButton`, `InAppSlotParams`, `BasicPushNotificationBuilder`, `DefaultPushNotificationBuilder`, `CustomPushNotificationBuilder`, `MultiActionsNotificationBuilder`, `JThirdPlatFormInterface`.
  - `data/` — `JPushLocalNotification`, `JPushCollectControl` (`JPushCollectControl.Builder`), `JPushConfig`, `JPushConfigEx`.
  - `local/` & `service/` — `ActionHelper` (Android `NotificationManager` posting with `NotificationChannel` creation on Android O+ and `JPushMessageReceiver` broadcast/callback dispatch), `JPushMessageReceiver`, `JCommonService`, `DataShare` (`:pushcore` cross-process `IDataShare.Stub` IPC), `DownloadProvider`, `DataProvider`, `PushService`, `PushReceiver`, `AlarmReceiver`, `SchedulerReceiver`, `InitProvider`, `JNotifyActivity`, `DActivity`.
  - `ui/` — `PushActivity`, `PopWinActivity`, `RoundedImageView`, `ShadowViewCard`, `FullScreenView`.

## Build Command

```bash
python3 native-sdks/jpush-jcore/scripts/build_android_source.py
```

## End-to-End Automated Test Command

```bash
python3 native-sdks/jpush-jcore/scripts/test_android_sdk.py
```
