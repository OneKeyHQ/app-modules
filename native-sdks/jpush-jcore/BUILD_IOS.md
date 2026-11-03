# iOS Source Build (`JCore` + `JPush`)

## Overview

This directory contains a clean, human-readable Objective-C source implementation of JCore (`5.2.0`) and JPush (`6.0.0`) tailored for OneKey's mobile notification runtime and `jpush-react-native` / `jcore-react-native`.

### Module Layout

- `source/ios/include/`
  - `JGInforCollectionAuth.h`, `JGInforCollectionAuthItems.h` — Collection authorization configuration (`JGInforCollectionAuth`, `JGInforCollectionAuthItems`).
  - `JGCOREAPI.h`, `JCOREService.h` — JCore runtime flags (`JGCOREAPI`) and socket/HTTP controller facade (`JCOREService`, `JPUSHReportPacketConfiguration`).
  - `JGPushNotificationTrigger.h` — Notification trigger/content/sound/identifier/request models.
  - `JPUSHService.h` — Public JPush service, notification entities, delegates, and `kJPFNetwork*` notification constants.
- `source/ios/jcore/` (`jcore-ios-5.2.0.a`)
  - `JCOREConfiguration.{h,m}` — Persistent configuration (`appKey`, `channel`, `uid`, `password`, `registrationID`, `deviceToken`, `voipToken`, live activity tokens, SIS cache, tags, alias, properties, mobileNumber, and HTTP report pipeline).
  - `JCORECrypto.{h,m}` — CommonCrypto + Security.framework + zlib primitives (`generateSeedKey`, `AES-256-CBC` with 32-byte ASCII hex key, `SM4-CBC` 32-round S-Box + key schedule, `RSA-1024 PKCS1/OAEP`, `GZIP`, `MD5`/`SHA1`/`HMAC-SHA256`).
  - `JCOREWireProtocol.{h,m}` — Asymmetric 24-byte Client->Server and 20-byte Server->Client TCP wire framing, 10-byte UDP SIS packet codec (`'U','G'`, `(0x11 << 24) | seed`), HTTP `/v3/report` & `/v3/push/sdk/badge` builders, and binary command payloads (`Register cmd=0 v30`, `Login cmd=1 v27`, `Heartbeat cmd=2 v8`, `PushMessage cmd=3`, `MessageReceipt cmd=4 v3`, `DeviceToken cmd=13 v3`, `CtrlCommand cmd=25 v1`, `MobileNumber cmd=26 v1`, `Tags cmd=28 v1`, `Alias cmd=29 v1`, `VoIPToken cmd=33 v2`, `UserProperties cmd=38 v1`, `LiveActivityToken cmd=39 v1`).
  - `JCORESisResolver.{h,m}` — 10-byte UDP SIS + HTTPS SIS resolver (`NSURLSession` with system TLS and hostname validation), full SIS payload persistence, and fallback endpoint resolution.
  - `JCORETcpSession.{h,m}` — `Network.framework` (`nw_connection_t`) dual-stack TCP/TLS session manager with multi-IP waterfall failover, `fail_ips` blacklist, registered fast-login (`cmd=1`), `statusCode 108/1012` recovery, exponential backoff auto-reconnect, `NWPathMonitor` network recovery, and pre-login FIFO request queue.
- `source/ios/jpush/` (`jpush-ios-6.0.0.a`)
  - `JPUSHModels.m` — Data models (`JPUSHRegisterEntity`, `JPushNotificationIdentifier`, `JPushNotificationSound`, `JPushNotificationContent`, `JPushNotificationTrigger`, `JPushNotificationRequest`, `JPushInAppMessage`, `JPushCollectControl`).
  - `JPUSHNotificationManager.{h,m}` — `UNUserNotificationCenter` delegate forwarding, APNs registration, and local notification scheduling.
  - `JPUSHService.m` — Public `JPUSHService` & `JCOREService` API implementation.

## Build Command

```bash
python3 native-sdks/jpush-jcore/scripts/build_ios_source.py
```

## Verification Command

```bash
python3 native-sdks/jpush-jcore/scripts/verify_clean_sdk.py
```
