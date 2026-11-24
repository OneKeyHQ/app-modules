"use strict";

// Sandboxed preload: receives shared textures and draws them on the canvas.
const { ipcRenderer, sharedTexture } = require("electron");

let drawn = 0;
sharedTexture.setSharedTextureReceiver(async ({ importedSharedTexture }) => {
  const frame = importedSharedTexture.getVideoFrame();
  try {
    const canvas = document.getElementById("preview");
    if (canvas) {
      const context = canvas.getContext("2d");
      context.drawImage(frame, 0, 0, canvas.width, canvas.height);
      drawn += 1;
      ipcRenderer.send("preview-drawn", drawn);
    }
  } finally {
    frame.close();
    importedSharedTexture.release();
  }
});
