param([Parameter(Mandatory=$true)][int]$MainPid,[Parameter(Mandatory=$true)][int]$HandlerPid,[Parameter(Mandatory=$true)][string]$OutputDirectory)
$ErrorActionPreference='Stop'
$identity=[Security.Principal.WindowsIdentity]::GetCurrent()
$principal=[Security.Principal.WindowsPrincipal]::new($identity)
if($principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)){throw 'watcher must be unprivileged'}
Add-Type -TypeDefinition @'
using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Threading;
public static class StrictAccessWatcher {
 public sealed class Result {
  public bool callerElevated=false;
  public long attempts,denials,violations,elapsedMs,mainExitObservedElapsedMs;
  public string firstViolation="",mainHandleAccess="0x00101000",mainExitUtc,mainExitFileTimeUtc,mainExitObservedUtc;
 }
 [DllImport("kernel32.dll",SetLastError=true)] static extern IntPtr OpenProcess(uint rights,bool inherit,uint pid);
 [DllImport("kernel32.dll")] static extern bool CloseHandle(IntPtr handle);
 [DllImport("kernel32.dll",SetLastError=true)] static extern uint WaitForSingleObject(IntPtr handle,uint ms);
 [DllImport("kernel32.dll",SetLastError=true)] static extern bool GetProcessTimes(IntPtr h,out long creation,out long exit,out long kernel,out long user);
 static void ObserveExit(IntPtr observer,Result result,Stopwatch clock) {
  if(result.mainExitUtc!=null)return;
  uint wait=WaitForSingleObject(observer,0);
  if(wait==258)return;
  if(wait!=0)throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error(),"Wait main observer");
  result.mainExitObservedUtc=DateTime.UtcNow.ToString("o");result.mainExitObservedElapsedMs=clock.ElapsedMilliseconds;
  long creation,exit,kernel,user;
  if(!GetProcessTimes(observer,out creation,out exit,out kernel,out user))throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error(),"Get main exit time");
  if(exit<=creation)throw new Exception("Invalid kernel process exit time");
  result.mainExitFileTimeUtc=exit.ToString();result.mainExitUtc=DateTime.FromFileTimeUtc(exit).ToString("o");
 }
 public static Result Run(uint main,uint handler,string stop,string ready) {
  IntPtr observer=OpenProcess(0x101000,false,main);
  if(observer==IntPtr.Zero)throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error(),"Open main QLI|SYNC observer");
  var result=new Result();var clock=Stopwatch.StartNew();
  try {
   uint[] pids={main,handler};uint[] masks={0x10,0x20,0x2,0x40,0x40000};
   File.WriteAllText(ready,"ready");
   while(clock.ElapsedMilliseconds<60000 && !File.Exists(stop)) {
    ObserveExit(observer,result,clock);
    foreach(uint pid in pids)foreach(uint mask in masks){
     IntPtr h=OpenProcess(mask,false,pid);int error=Marshal.GetLastWin32Error();result.attempts++;
     if(h!=IntPtr.Zero){result.violations++;CloseHandle(h);if(result.firstViolation.Length==0)result.firstViolation="pid="+pid+",mask="+mask+",ms="+clock.ElapsedMilliseconds;}
     else if(error==5)result.denials++;
    }
    Thread.Sleep(1);
   }
   ObserveExit(observer,result,clock);result.elapsedMs=clock.ElapsedMilliseconds;return result;
  } finally {CloseHandle(observer);}
 }
}
'@
$result=[StrictAccessWatcher]::Run($MainPid,$HandlerPid,(Join-Path $OutputDirectory 'stop-watch'),(Join-Path $OutputDirectory 'watch-ready'))
$result | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $OutputDirectory 'watch.json') -Encoding utf8
$result | ConvertTo-Json -Compress
