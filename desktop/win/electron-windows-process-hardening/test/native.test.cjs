"use strict";

const assert = require("node:assert/strict");
const { spawn, spawnSync } = require("node:child_process");
const test = require("node:test");
const readline = require("node:readline");

const supported = process.platform === "win32" && ["x64", "arm64"].includes(process.arch);

test("native hardening succeeds in an isolated Node child", { skip: !supported }, () => {
  const child = spawnSync(process.execPath, [require.resolve("./node-child.cjs")], {
    encoding: "utf8",
    timeout: 15000,
  });
  assert.equal(child.status, 0, child.stderr || child.error?.message);
  const result = JSON.parse(child.stdout.trim());
  assert.deepEqual(result.dacl.status, "applied", JSON.stringify(result));
  assert.deepEqual(result.extensionPoint.status, "applied", JSON.stringify(result));
});

test("the protected DACL changes actual OpenProcess access in an isolated child", { skip: !supported, timeout: 75000 }, async () => {
  const child = spawn(process.execPath, [require.resolve("./node-child.cjs"), "--hold"], {
    stdio: ["pipe", "pipe", "pipe"],
  });
  const lines = readline.createInterface({ input: child.stdout })[Symbol.asyncIterator]();
  let stderr = "";
  child.stderr.setEncoding("utf8").on("data", (chunk) => { stderr += chunk; });
  function probe(pid) {
    const result = spawnSync("powershell.exe", [
      "-NoProfile", "-ExecutionPolicy", "Bypass", "-File",
      require("node:path").join(__dirname, "probe-open-process.ps1"),
      "-ProcessId", String(pid),
    ], { encoding: "utf8", timeout: 30000 });
    assert.equal(result.status, 0, result.stderr || result.error?.message);
    return JSON.parse(result.stdout.trim());
  }
  try {
    const first = JSON.parse((await lines.next()).value);
    assert.equal(first.phase, "before");
    const before = probe(first.pid);
    for (const [name, access] of Object.entries(before)) {
      assert.equal(access.opened, true, `baseline ${name}: ${JSON.stringify(access)}`);
    }

    child.stdin.write("harden\n");
    const second = JSON.parse((await lines.next()).value);
    assert.equal(second.phase, "after");
    assert.equal(second.result.dacl.status, "applied", JSON.stringify(second.result));
    assert.equal(second.result.extensionPoint.status, "applied", JSON.stringify(second.result));
    const after = probe(second.pid);
    for (const name of ["QUERY_LIMITED_INFORMATION", "SYNCHRONIZE", "TERMINATE"]) {
      assert.equal(after[name].opened, true, `allowed ${name}: ${JSON.stringify(after[name])}`);
    }
    for (const name of ["VM_READ", "VM_WRITE", "VM_OPERATION", "CREATE_THREAD", "DUP_HANDLE", "SET_INFORMATION", "WRITE_DAC", "WRITE_OWNER"]) {
      assert.equal(after[name].opened, false, `restricted ${name}: ${JSON.stringify(after[name])}`);
      assert.equal(after[name].win32Error, 5, `restricted ${name}`);
    }
  } finally {
    if (child.exitCode === null) {
      const exited = new Promise((resolve) => child.once("exit", resolve));
      child.stdin.end();
      let timer;
      await Promise.race([
        exited,
        new Promise((resolve) => {
          timer = setTimeout(() => {
            child.kill();
            resolve();
          }, 2000);
        }),
      ]);
      clearTimeout(timer);
      if (child.exitCode === null) await exited;
    }
    assert.equal(stderr, "", stderr);
  }
});
