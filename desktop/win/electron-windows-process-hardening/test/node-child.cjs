"use strict";

// This file runs only in a disposable child: hardening its DACL is irreversible here.
const { hardenCurrentProcess } = require("../index.cjs");
if (process.argv.includes("--hold")) {
  process.stdout.write(`${JSON.stringify({ phase: "before", pid: process.pid })}\n`);
  let hardened = false;
  process.stdin.on("data", () => {
    if (hardened) {
      process.exit(0);
    }
    hardened = true;
    process.stdout.write(`${JSON.stringify({ phase: "after", pid: process.pid, result: hardenCurrentProcess() })}\n`);
  });
  process.stdin.on("end", () => process.exit(0));
} else {
  process.stdout.write(`${JSON.stringify(hardenCurrentProcess())}\n`);
}
