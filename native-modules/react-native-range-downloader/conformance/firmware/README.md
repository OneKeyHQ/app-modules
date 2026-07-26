# Firmware artifact conformance

This harness covers the native firmware artifact API rather than the legacy
OCDS bundle downloader.

`server.js` starts two HTTPS listeners. The canonical listener uses a runtime
certificate for `firmware.test`; the second listener uses `wrong.test` so the
same test run can prove hostname mismatch and cross-host redirect rejection.
The private keys live under ignored `.certs/` and are generated for each run.

The `small` (4 MiB) and `large` (83,854,948 bytes) profiles are generated from
their absolute byte offsets in 64 KiB chunks. The server never allocates a
payload-sized `Buffer`.

Supported scenarios are `normal`, `range-200`, `416-once`, `etag-change`,
`stall-once`, `429-once`, `503-once`, `disconnect-once`, and
`cross-host-redirect`. Select one with the `scenario` query parameter, the
`X-Firmware-Test-Scenario` header, or `POST /scenario`.

Run native build and logic checks:

```bash
./run-ios.sh build-only
./run-android.sh build-only
```

Full device runs require `FIRMWARE_IOS_DRIVER` or `FIRMWARE_ANDROID_DRIVER`.
Each driver is invoked for a warm device/build three times per profile and must
write phase-separated download, verify/hash, materialization, reader-transfer,
main-Hermes, background-Hermes, and native RSS/PSS measurements under
`FIRMWARE_TEST_REPORT_DIR`. Missing drivers are a hard failure, not a passing
conformance result.

The acceptance calculation uses the median of three trials:

- each Hermes runtime peak minus its own steady baseline must be at most 8 MiB;
- artifact-related native RSS/PSS peak minus baseline must be at most 32 MiB;
- the large-profile native peak median may exceed the small-profile median by
  at most 4 MiB;
- reader/message and transport/hash/ZIP buffers must remain at most 256 KiB.
