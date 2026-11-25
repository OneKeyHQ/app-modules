"use strict";

// Sandboxed preload: receives shared textures and draws them on the canvas.
const { ipcRenderer, sharedTexture } = require("electron");

let drawn = 0;
let dark = 0;
let light = 0;
sharedTexture.setSharedTextureReceiver(async ({ importedSharedTexture }) => {
  let frame;
  try {
    frame = importedSharedTexture.getVideoFrame();
    const canvas = document.getElementById("preview");
    if (canvas) {
      const context = canvas.getContext("2d");
      context.drawImage(frame, 0, 0, canvas.width, canvas.height);
      drawn += 1;
      if (dark <= 50 || light <= 50) {
        const { data } = context.getImageData(0, 0, canvas.width, canvas.height);
        dark = 0;
        light = 0;
        for (let i = 0; i < data.length; i += 4 * 97) {
          if (data[i + 3] !== 255) continue;
          const luma = (data[i] + data[i + 1] + data[i + 2]) / 3;
          if (luma < 60) dark += 1;
          else if (luma > 200) light += 1;
        }
      }
      ipcRenderer.send("preview-drawn", { drawn, dark, light });
    }
  } finally {
    frame?.close();
    importedSharedTexture.release();
  }
});
