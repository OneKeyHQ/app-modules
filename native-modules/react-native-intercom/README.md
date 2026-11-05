# @onekeyfe/react-native-intercom

**Recovery in progress. Not a usable SDK or a publishable replacement.**

This repository-template package is restoring the original Intercom native SDK from auditable source, with RN 10.8.1 / Android 18.10.0 / iOS 19.9.1 compatibility. The native implementation, original messenger UI, cards/sheets, media, authentication and identity are required; a vendor binary wrapper or simplified client does not satisfy the work.

- [Compatibility contract](docs/SPEC.md)
- [Actual recovery status](docs/RECOVERY.md)
- [Provenance and binary exclusion rules](docs/PROVENANCE.md)
- [Survey source scope and private ABI limitations](docs/SURVEY_RECOVERY.md)

All 3389 original Android native classes now have source compilation coverage. The normal base build passes ABI, source-method preservation, class-feature/access and D8 min-api 23 checks. Full original SDK behavior remains unverified; see the recovery ledger.

The frozen iOS 290 ObjC / 395 Swift snapshot strictly links on three targets. Normal static/dynamic consumers, selected public/private API compiler probes and 63-resource Host smoke pass. Backend/session, cards/sheets, complete UI behavior, security and private ABI remain unverified.

Build the two own-source Android AARs, retaining their original resource namespaces:

```sh
python3 scripts/android-build-artifacts.py --offline --gradle /absolute/path/to/gradle-8.13/bin/gradle
```

This independent SDK build requires JDK 17, JDK 24 for recovered Java source compilation, Android SDK 36/build tools, and the pinned external artifact/tool caches (omit `--offline` for the initial dependency download). It generates package-local artifacts under `android/maven`; it never publishes remotely. Consumer Gradle uses its existing React Native/toolchain. The bridge preserves the pinned original RCT/TurboModule/EventEmitter transport under the documented template exception.
