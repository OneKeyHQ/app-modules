"use strict";

const availability = Object.freeze({
  Available: "available",
  DeviceNotPresent: "device_not_present",
  NotConfiguredForUser: "not_configured_for_user",
  DisabledByPolicy: "disabled_by_policy",
  DeviceBusy: "device_busy",
});
const verificationResult = Object.freeze({
  Verified: "verified",
  DeviceNotPresent: "device_not_present",
  NotConfiguredForUser: "not_configured_for_user",
  DisabledByPolicy: "disabled_by_policy",
  DeviceBusy: "device_busy",
  RetriesExhausted: "retries_exhausted",
  Canceled: "canceled",
});
let nativeBinding;
let verifying = false;

function errorWithCode(code, message) {
  return Object.assign(new Error(message), { code });
}

function supportedPlatform() {
  return (
    process.platform === "win32" && ["x64", "arm64"].includes(process.arch)
  );
}

function binding() {
  if (!supportedPlatform()) {
    throw errorWithCode(
      "ERR_WINDOWS_HELLO_PLATFORM",
      "Windows Hello requires Windows x64 or ARM64",
    );
  }
  nativeBinding ??= require(`./electron-windows-hello.win32-${process.arch}-msvc.node`);
  return nativeBinding;
}

function mapResult(values, value) {
  if (!Number.isInteger(value) || value < 0 || value >= values.length) {
    throw errorWithCode(
      "ERR_WINDOWS_HELLO_RESULT",
      "Windows returned an unknown verification result",
    );
  }
  return values[value];
}

exports.Availability = availability;
exports.VerificationResult = verificationResult;
exports.checkAvailability = async function checkAvailability() {
  if (!supportedPlatform()) return availability.DeviceNotPresent;
  return mapResult(
    Object.values(availability),
    await binding().checkAvailability(),
  );
};
exports.requestVerification = async function requestVerification(options) {
  if (
    !options ||
    !Buffer.isBuffer(options.windowHandle) ||
    options.windowHandle.length !== 8
  ) {
    throw errorWithCode(
      "ERR_WINDOWS_HELLO_HANDLE",
      "windowHandle must be the 8-byte Buffer returned by BrowserWindow.getNativeWindowHandle()",
    );
  }
  if (
    typeof options.message !== "string" ||
    options.message.trim().length === 0
  ) {
    throw errorWithCode(
      "ERR_WINDOWS_HELLO_MESSAGE",
      "A non-empty verification message is required",
    );
  }
  if (verifying) return verificationResult.DeviceBusy;
  const native = binding();
  verifying = true;
  try {
    return mapResult(
      Object.values(verificationResult),
      await native.requestVerification(
        Buffer.from(options.windowHandle),
        options.message,
      ),
    );
  } finally {
    verifying = false;
  }
};
exports.allowSetForegroundWindow = function allowSetForegroundWindow(
  processId,
) {
  if (
    !Number.isInteger(processId) ||
    processId <= 0 ||
    processId >= 0xffffffff
  ) {
    throw errorWithCode(
      "ERR_WINDOWS_HELLO_PROCESS",
      "A specific positive process ID is required",
    );
  }
  if (!supportedPlatform()) return false;
  return binding().allowSetForegroundWindow(processId);
};
