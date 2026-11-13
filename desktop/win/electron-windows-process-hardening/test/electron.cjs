"use strict";

const assert = require("node:assert/strict");
const { app, BrowserWindow } = require("electron");
const { hardenCurrentProcess } = require("../index.cjs");

const watchdog = setTimeout(() => app.exit(1), 15000);
async function main() {
  assert.equal(process.type, "browser");
  assert.equal(process.versions.electron, require("../package.json").devDependencies.electron);
  // Exercise the synchronous main-process path before the first window exists.
  const result = hardenCurrentProcess();
  assert.equal(result.dacl.status, "applied", JSON.stringify(result));
  assert.equal(result.extensionPoint.status, "applied", JSON.stringify(result));
  await app.whenReady();
  const window = new BrowserWindow({ show: false, width: 320, height: 240 });
  await window.loadURL("data:text/html,<title>Process hardening smoke</title><h1>Renderer ready</h1>");
  const renderer = await window.webContents.executeJavaScript(
    "({ state: document.readyState, title: document.title, body: document.body.textContent })",
  );
  assert.equal(renderer.state, "complete");
  assert.equal(renderer.title, "Process hardening smoke");
  assert.match(renderer.body, /Renderer ready/);
  console.log(JSON.stringify({ ...result, electron: process.versions.electron, arch: process.arch, rendererReady: true }));
  window.close();
  clearTimeout(watchdog);
  app.quit();
}
main().catch((error) => {
  console.error(error);
  clearTimeout(watchdog);
  app.exit(1);
});
