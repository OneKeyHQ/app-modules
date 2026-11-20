"use strict";

function stage(status, message, win32Error) {
  return {
    status,
    ...(win32Error === undefined ? {} : { win32Error }),
    ...(message === undefined ? {} : { message }),
  };
}

function hardenCurrentProcess() {
  if (process.type !== undefined && process.type !== "browser") {
    throw Object.assign(new Error("Call only from Node or the Electron main process"), {
      code: "ERR_PROCESS_HARDENING_CONTEXT",
    });
  }
  if (process.platform !== "win32" || !["x64", "arm64"].includes(process.arch)) {
    return { dacl: stage("unsupported"), extensionPoint: stage("unsupported") };
  }
  try {
    const native = require(`./electron-windows-process-hardening.win32-${process.arch}-msvc.node`);
    return native.hardenCurrentProcess();
  } catch (error) {
    const failed = stage("failed", `Native binding unavailable: ${String(error)}`);
    return { dacl: { ...failed }, extensionPoint: { ...failed } };
  }
}

exports.hardenCurrentProcess = hardenCurrentProcess;

function startLocalCrashHandler() {
  if (process.type !== "browser" || process.platform !== "win32" ||
      process.arch !== "x64" || process.versions.electron !== "44.5.1") {
    return { status: "unsupported" };
  }
  if (process.env.DESKTOP_E2E_MODE !== "true" || process.env.ONEKEY_TEST_LOCAL_CRASH_HANDLER !== "1") {
    return { status: "disabled" };
  }
  try {
    const path = require("node:path");
    const { app } = require("electron");
    const native = require(`./electron-windows-process-hardening.win32-${process.arch}-msvc.node`);
    const handler = path.join(__dirname, `onekey-crash-prototype.win32-${process.arch}-msvc.exe`);
    return native.startLocalCrashHandler(handler, app.getPath("userData"), app.getVersion());
  } catch {
    return { status: "failed", message: "handler_start" };
  }
}

exports.startLocalCrashHandler = startLocalCrashHandler;

function triggerLocalCrashForTest() {
  if (process.type !== "browser" || process.platform !== "win32" ||
      process.arch !== "x64" || process.versions.electron !== "44.5.1" ||
      process.env.DESKTOP_E2E_MODE !== "true" || process.env.ONEKEY_TEST_LOCAL_CRASH_HANDLER !== "1") {
    throw new Error("Local crash trigger is unavailable");
  }
  const native = require(`./electron-windows-process-hardening.win32-${process.arch}-msvc.node`);
  native.triggerLocalCrashForTest();
}

exports.triggerLocalCrashForTest = triggerLocalCrashForTest;
