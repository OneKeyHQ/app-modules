const { spawnSync } = require('node:child_process');
const { mkdtempSync, writeFileSync, readFileSync, rmSync } = require('node:fs');
const { tmpdir } = require('node:os');
const { join, resolve } = require('node:path');

const logger = resolve(__dirname, '../../native-logger');
const output = mkdtempSync(join(tmpdir(), 'lite-real-loggers-'));
function run(command, args) {
  const result = spawnSync(command, args, { encoding: 'utf8' });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error(`${command} failed\n${result.stderr}`);
  return result.stdout.trim();
}
// Only external Android/Nitro/SLF4J/logback declarations are adapters. The entire
// production logger, including formatting and all redaction rules, is compiled unchanged.
const stubs = {
  Context: 'package android.content\nclass Context { val cacheDir:java.io.File get()=error("Unexpected cache IO") }',
  Nitro: 'package com.margelo.nitro\nobject NitroModules { val applicationContext:android.content.Context? get()=error("Unexpected cache IO") }',
  Log: `package android.util
object Log { const val DEBUG=3;const val INFO=4;const val WARN=5;const val ERROR=6
 fun w(tag:String,message:String):Int=error("Unexpected logger IO");fun i(tag:String,message:String):Int=error("Unexpected logger IO")
 fun println(level:Int,tag:String,message:String):Int=error("Unexpected logger IO") }`,
  Logger: `package org.slf4j
interface Logger { fun debug(s:String);fun info(s:String);fun warn(s:String);fun error(s:String) }
object LoggerFactory { fun getILoggerFactory():Any=error("Unexpected logger IO") }`,
  Classic: `package ch.qos.logback.classic
class Level { companion object { val DEBUG=Level() } }
class LoggerContext { fun getLogger(name:String)=TestLogger() }
class TestLogger:org.slf4j.Logger {
 var level:Level?=null;var isAdditive=false
 fun getAppender(name:String):ch.qos.logback.core.rolling.RollingFileAppender<*>?=null
 fun detachAppender(name:String){};fun addAppender(a:ch.qos.logback.core.rolling.RollingFileAppender<*>){}
 override fun debug(s:String){};override fun info(s:String){};override fun warn(s:String){};override fun error(s:String){}
}`,
  Encoder: 'package ch.qos.logback.classic.encoder\nclass PatternLayoutEncoder { var context:Any?=null;var charset:java.nio.charset.Charset?=null;var pattern="";fun start(){} }',
  Event: 'package ch.qos.logback.classic.spi\ninterface ILoggingEvent',
  FileSize: 'package ch.qos.logback.core.util\nclass FileSize(val value:Long)',
  Rolling: `package ch.qos.logback.core.rolling
class RollingFileAppender<T> {
 var context:Any?=null;var name="";var file="";var rollingPolicy:Any?=null;var encoder:Any?=null
 var outputStream:java.io.OutputStream?=null
 fun start(){};fun stop(){}
}
class SizeAndTimeBasedRollingPolicy<T> {
 var context:Any?=null;var fileNamePattern="";var maxHistory=0
 fun setMaxFileSize(s:ch.qos.logback.core.util.FileSize){};fun setTotalSizeCap(s:ch.qos.logback.core.util.FileSize){}
 fun setParent(a:RollingFileAppender<*>){};fun start(){}
}`,
};
try {
  const [jniMessages, iosMessages] = process.argv.slice(2);
  if (!jniMessages || !iosMessages) throw new Error('Actual JNI and bridge captures are required');
  run('xcrun', ['swiftc', '-emit-library', '-emit-module', '-module-name', 'CocoaLumberjack',
    join(__dirname, 'logger/CocoaLumberjack.swift'), '-o', join(output, 'libCocoaLumberjack.dylib'),
    '-emit-module-path', join(output, 'CocoaLumberjack.swiftmodule')]);
  // Private Swift access is appended to a temporary full copy; no production
  // function body is extracted, rewritten or duplicated, and configured is not called.
  const swift = join(output, 'main.swift');
  writeFileSync(swift, readFileSync(join(logger, 'ios/OneKeyLog.swift'), 'utf8') + readFileSync(join(__dirname, 'logger/Format.swift'), 'utf8'));
  run('xcrun', ['swiftc', '-I', output, '-L', output, '-lCocoaLumberjack', '-Xlinker', '-rpath', '-Xlinker', output,
    swift, '-o', join(output, 'swift-format')]);
  console.log(run(join(output, 'swift-format'), [iosMessages]));
  const declarations = Object.entries(stubs).map(([name, source]) => {
    const path = join(output, `${name}.kt`); writeFileSync(path, source); return path;
  });
  const jar = join(output, 'kotlin-format.jar');
  run('kotlinc', [...declarations, join(logger, 'android/src/main/java/com/margelo/nitro/nativelogger/OneKeyLog.kt'),
    join(__dirname, 'logger/Format.kt'), '-nowarn', '-include-runtime', '-d', jar]);
  console.log(run('java', ['-jar', jar, jniMessages]));
} finally {
  rmSync(output, { recursive: true, force: true });
}
