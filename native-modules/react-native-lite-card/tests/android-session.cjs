const { spawnSync } = require('node:child_process');
const { mkdtempSync, writeFileSync, realpathSync, existsSync, rmSync } = require('node:fs');
const { tmpdir } = require('node:os');
const { join, resolve, dirname, delimiter } = require('node:path');

const root = resolve(__dirname, '..');
const output = mkdtempSync(join(tmpdir(), 'lite-card-session-'));
function run(command, args, capture = false) {
  const result = spawnSync(command, args, { stdio: capture ? 'pipe' : 'inherit', encoding: 'utf8' });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error(`${command} exited ${result.status}`);
  return result.stdout?.trim();
}

// Android lifecycle and transport are fakes; session, guard and operation code are production sources.
const stubs = {
  Activity: 'package android.app\nopen class Activity',
  IsoDep: `package android.nfc.tech
class IsoDep(val label: String, val serial: String = label, val newCard: Boolean = true,
             val backedUp: Boolean = false, val failStage: String? = null, val v2: Boolean = true) {
  var isConnected = false
}`,
  Lifecycle: `package androidx.lifecycle
class Lifecycle {
  enum class State { RESUMED; fun isAtLeast(other: State) = true }
  val currentState = State.RESUMED
}`,
  FragmentActivity: `package androidx.fragment.app
class FragmentActivity : android.app.Activity() { val lifecycle = androidx.lifecycle.Lifecycle() }`,
  OneKeyLog: `package com.margelo.nitro.nativelogger
object OneKeyLog {
  val messages = java.util.concurrent.CopyOnWriteArrayList<String>()
  var throwPreflight = false
  fun info(tag: String, message: String) {
    if (message.contains("stage=input_preflight") && throwPreflight) throw IllegalStateException("PRIVATE-LOGGER-EXCEPTION")
    messages.add(message)
  }
  fun error(tag: String, message: String) {}
}`,
  Gson: 'package com.google.gson\nclass Gson',
  NfcUtils: `package com.onekeyfe.reactnativelitecard.nfc
object NfcUtils {
  class Adapter {
    fun enableForegroundDispatch(activity: android.app.Activity, intent: Any?, filters: Any?, tech: Any?) {}
    fun disableForegroundDispatch(activity: android.app.Activity) {}
  }
  val mNfcAdapter: Adapter? = Adapter()
  val mPendingIntent: Any? = null
  val mIntentFilter: Any? = null
  val mTechList: Any? = null
  fun isNfcExits(activity: android.app.Activity) = true
  fun init(activity: android.app.Activity) = mNfcAdapter
}`,
  Utils: `package com.onekeyfe.reactnativelitecard.utils
object LogUtil { fun printLog(tag: String, message: String) {} }
object HexUtils
object NfcPermissionUtils {
  inline fun checkPermission(activity: android.app.Activity, doNext: () -> Unit): Int { doNext(); return 0 }
}`,
  GPChannelNatives: `package org.haobtc.onekey.card.gpchannel
object GPChannelNatives {
  fun nativeGPCFinalize() = com.onekeyfe.reactnativelitecard.onekeyLite.nfc.Probe.finalizeCurrent()
}`,
};
try {
  const compiler = run('which', ['kotlinc'], true);
  const kotlinRoot = dirname(dirname(realpathSync(compiler)));
  const coroutineJar = ['lib', 'libexec/lib']
    .map((directory) => join(kotlinRoot, directory, 'kotlinx-coroutines-core-jvm.jar'))
    .find(existsSync);
  if (!coroutineJar) throw new Error('The Kotlin compiler distribution must include kotlinx-coroutines-core-jvm.jar');
  const fixtures = Object.entries(stubs).map(([name, source]) => {
    const path = join(output, `${name}.kt`);
    writeFileSync(path, source);
    return path;
  });
  const source = join(root, 'android/src/main/java/com/onekeyfe/reactnativelitecard');
  const jar = join(output, 'session-tests.jar');
  run(compiler, [
    ...fixtures,
    join(source, 'onekeyLite/OnekeyLiteCard.kt'),
    join(source, 'onekeyLite/entitys/CardState.kt'),
    join(source, 'onekeyLite/NfcConstant.kt'),
    join(source, 'nfc/Exceptions.kt'),
    join(__dirname, 'kotlin/FakeConnection.kt'),
    join(__dirname, 'kotlin/CardSessionTest.kt'),
    '-nowarn', '-cp', coroutineJar, '-include-runtime', '-d', jar,
  ]);
  run('java', ['-cp', [jar, coroutineJar].join(delimiter), 'com.onekeyfe.reactnativelitecard.onekeyLite.CardSessionTestKt']);
} finally {
  rmSync(output, { recursive: true, force: true });
}
