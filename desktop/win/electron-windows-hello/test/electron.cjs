"use strict";

const api = require("../index.cjs");

if (process.type === "utility") {
  process.parentPort.on("message", async ({ data }) => {
    try {
      const result = data.interactive
        ? await api.requestVerification({
            windowHandle: Buffer.from(data.windowHandle),
            message: "electron-windows-hello integration test",
          })
        : await api.checkAvailability();
      process.parentPort.postMessage({ result });
    } catch (error) {
      process.parentPort.postMessage({ error: String(error) });
    }
  });
} else {
  const { app, BrowserWindow, utilityProcess } = require("electron");
  const assert = require("node:assert/strict");
  const interactive = process.argv.includes("--interactive");
  let worker;
  app.on("window-all-closed", () => app.quit());
  app.on("before-quit", () => worker?.kill());
  app
    .whenReady()
    .then(async () => {
      assert.equal(
        process.versions.electron,
        require("../package.json").devDependencies.electron,
      );
      const owner = new BrowserWindow({
        width: 640,
        height: 360,
        show: interactive,
      });
      await owner.loadURL(
        "data:text/html,<title>Windows Hello integration test</title><h1>Windows Hello integration test</h1><p>Check owner-window focus, cancellation and taskbar presentation.</p>",
      );
      assert.equal(owner.getNativeWindowHandle().length, 8);
      worker = utilityProcess.fork(__filename);
      worker.once("spawn", () => {
        const dispatch = () => {
          if (interactive) {
            console.log(
              JSON.stringify({
                ownerFocusedBeforeRequest: owner.isFocused(),
                foregroundGranted: api.allowSetForegroundWindow(worker.pid),
              }),
            );
          }
          worker.postMessage({
            interactive,
            windowHandle: Array.from(owner.getNativeWindowHandle()),
          });
        };
        if (interactive) {
          owner.show();
          owner.focus();
          if (!owner.isFocused()) {
            console.log("Activate the test window to request Windows Hello");
            owner.once("focus", dispatch);
            return;
          }
        }
        dispatch();
      });
      worker.once("message", (message) => {
        console.log(
          JSON.stringify({
            ...message,
            ownerFocused: owner.isFocused(),
            electron: process.versions.electron,
            node: process.versions.node,
            napi: process.versions.napi,
            arch: process.arch,
          }),
        );
        if (message.error) {
          app.exit(1);
        } else if (!interactive) {
          assert.ok(Object.values(api.Availability).includes(message.result));
          app.quit();
        }
      });
      if (!interactive) {
        setTimeout(() => app.exit(1), 15000).unref();
      }
    })
    .catch((error) => {
      console.error(error);
      app.exit(1);
    });
}
