"use strict";

// Sandboxed preload: draws shared-texture frames and shows status text.
const { ipcRenderer, sharedTexture } = require("electron");

let drawn = 0;
sharedTexture.setSharedTextureReceiver(async ({ importedSharedTexture }) => {
  const frame = importedSharedTexture.getVideoFrame();
  try {
    const canvas = document.getElementById("preview");
    if (canvas) {
      if (canvas.width !== frame.displayWidth) canvas.width = frame.displayWidth;
      if (canvas.height !== frame.displayHeight) canvas.height = frame.displayHeight;
      canvas.getContext("2d").drawImage(frame, 0, 0, canvas.width, canvas.height);
      drawn += 1;
      ipcRenderer.send("camera-drawn", drawn);
    }
  } finally {
    frame.close();
    importedSharedTexture.release();
  }
});
ipcRenderer.on("camera-status", (_event, text) => {
  const status = document.getElementById("status");
  if (status) status.textContent = text;
});
