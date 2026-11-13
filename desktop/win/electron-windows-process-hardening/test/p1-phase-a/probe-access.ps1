param([Parameter(Mandatory=$true)][int]$TargetProcessId,[Parameter(Mandatory=$true)][string]$OutputFile,[int]$ExpectedThreadId=0)
$ErrorActionPreference='Stop'
$identity=[Security.Principal.WindowsIdentity]::GetCurrent()
$principal=[Security.Principal.WindowsPrincipal]::new($identity)
if($principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)){throw 'probe requires ordinary token'}
Add-Type -TypeDefinition @'
using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
public static class P1AccessProbe {
 [StructLayout(LayoutKind.Sequential)] public struct ThreadEntry { public uint size,usage,threadId,ownerPid,basePriority,deltaPriority,flags; }
 [DllImport("kernel32.dll",SetLastError=true)] public static extern IntPtr OpenProcess(uint rights,bool inherit,uint pid);
 [DllImport("kernel32.dll",SetLastError=true)] public static extern IntPtr OpenThread(uint rights,bool inherit,uint tid);
 [DllImport("kernel32.dll",SetLastError=true)] public static extern bool CloseHandle(IntPtr h);
 [DllImport("advapi32.dll",SetLastError=true)] public static extern bool OpenProcessToken(IntPtr process,uint rights,out IntPtr token);
 [DllImport("advapi32.dll",SetLastError=true)] static extern bool GetTokenInformation(IntPtr token,int infoClass,IntPtr buffer,uint length,out uint needed);
 [DllImport("kernel32.dll",SetLastError=true)] static extern IntPtr CreateToolhelp32Snapshot(uint flags,uint pid);
 [DllImport("kernel32.dll",SetLastError=true)] static extern bool Thread32First(IntPtr snapshot,ref ThreadEntry entry);
 [DllImport("kernel32.dll",SetLastError=true)] static extern bool Thread32Next(IntPtr snapshot,ref ThreadEntry entry);
 public static uint[] Threads(uint pid) {
  var found=new List<uint>(); IntPtr s=CreateToolhelp32Snapshot(4,0);
  if(s==new IntPtr(-1)) throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error());
  try {var e=new ThreadEntry();e.size=(uint)Marshal.SizeOf(typeof(ThreadEntry));
   if(Thread32First(s,ref e)) do {if(e.ownerPid==pid)found.Add(e.threadId);e.size=(uint)Marshal.SizeOf(typeof(ThreadEntry));} while(Thread32Next(s,ref e));
  } finally {CloseHandle(s);} return found.ToArray();
 }
 public static int Error(){return Marshal.GetLastWin32Error();}
 public static string DefaultDaclHash(IntPtr process){
  IntPtr token=IntPtr.Zero,buffer=IntPtr.Zero;
  if(!OpenProcessToken(process,8,out token))return null;
  try {
   uint needed=0;
   GetTokenInformation(token,6,IntPtr.Zero,0,out needed);
   if(needed<8||needed>65536)throw new Exception("TokenDefaultDacl length outside 8..65536");
   buffer=Marshal.AllocHGlobal((int)needed);
   uint actual=0;
   if(!GetTokenInformation(token,6,buffer,needed,out actual))throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error(),"TokenDefaultDacl query failed");
   if(actual<8||actual>needed)throw new Exception("TokenDefaultDacl returned invalid length");
   IntPtr acl=Marshal.ReadIntPtr(buffer,0);
   if(acl==IntPtr.Zero)return null;
   long start=buffer.ToInt64(),pointer=acl.ToInt64();
   if(pointer<start+8||pointer+8>start+actual)throw new Exception("TokenDefaultDacl pointer outside response");
   int aclSize=unchecked((ushort)Marshal.ReadInt16(acl,2));
   if(aclSize<8||aclSize>65536||pointer+aclSize>start+actual)throw new Exception("TokenDefaultDacl ACL size outside response");
   var bytes=new byte[aclSize];Marshal.Copy(acl,bytes,0,aclSize);
   using(var sha=SHA256.Create())return BitConverter.ToString(sha.ComputeHash(bytes)).Replace("-","").ToLowerInvariant();
  } finally {if(buffer!=IntPtr.Zero)Marshal.FreeHGlobal(buffer);CloseHandle(token);}
 }
}
'@
function Test-Handle([string]$kind,[uint32]$mask,[uint32]$id,[IntPtr]$processHandle){
 $h=[IntPtr]::Zero
 if($kind -eq 'process'){$h=[P1AccessProbe]::OpenProcess($mask,$false,$id)}
 elseif($kind -eq 'thread'){$h=[P1AccessProbe]::OpenThread($mask,$false,$id)}
 else { $ok=[P1AccessProbe]::OpenProcessToken($processHandle,$mask,[ref]$h); if(-not $ok){$h=[IntPtr]::Zero} }
 $err=[P1AccessProbe]::Error();$success=$h -ne [IntPtr]::Zero
 if($success -and -not [P1AccessProbe]::CloseHandle($h)){throw 'CloseHandle failed'}
 return [pscustomobject]@{mask=('0x{0:X8}' -f $mask);success=$success;win32Error=$(if($success){0}else{$err})}
}
$processMasks=[ordered]@{
 PROCESS_QUERY_LIMITED_INFORMATION=[uint32]0x1000; SYNCHRONIZE=[uint32]0x100000; PROCESS_TERMINATE=[uint32]1;
 PROCESS_QUERY_INFORMATION=[uint32]0x400; READ_CONTROL=[uint32]0x20000; PROCESS_SET_LIMITED_INFORMATION=[uint32]0x2000;
 PROCESS_SUSPEND_RESUME=[uint32]0x800; PROCESS_ALL_ACCESS=[uint32]0x1fffff; PROCESS_VM_READ=[uint32]0x10;
 PROCESS_VM_WRITE=[uint32]0x20; PROCESS_VM_OPERATION=[uint32]8; PROCESS_CREATE_THREAD=[uint32]2;
 PROCESS_DUP_HANDLE=[uint32]0x40; PROCESS_SET_INFORMATION=[uint32]0x200; WRITE_DAC=[uint32]0x40000; WRITE_OWNER=[uint32]0x80000
}
$threadMasks=[ordered]@{
 THREAD_QUERY_LIMITED_INFORMATION=[uint32]0x800; SYNCHRONIZE=[uint32]0x100000; THREAD_TERMINATE=[uint32]1;
 THREAD_GET_CONTEXT=[uint32]8; THREAD_SET_CONTEXT=[uint32]0x10; THREAD_SUSPEND_RESUME=[uint32]2;
 THREAD_SET_INFORMATION=[uint32]0x20; THREAD_IMPERSONATE=[uint32]0x100; THREAD_DIRECT_IMPERSONATION=[uint32]0x200;
 THREAD_SET_THREAD_TOKEN=[uint32]0x80; WRITE_DAC=[uint32]0x40000; WRITE_OWNER=[uint32]0x80000
}
$tokenMasks=[ordered]@{TOKEN_QUERY=[uint32]8;TOKEN_ADJUST_DEFAULT=[uint32]0x80;WRITE_DAC=[uint32]0x40000;WRITE_OWNER=[uint32]0x80000;TOKEN_DUPLICATE=[uint32]2;TOKEN_ASSIGN_PRIMARY=[uint32]1}
$process=@();foreach($item in $processMasks.GetEnumerator()){$r=Test-Handle process $item.Value ([uint32]$TargetProcessId) ([IntPtr]::Zero);$process += [pscustomobject]@{right=$item.Key;mask=$r.mask;success=$r.success;win32Error=$r.win32Error}}
$threads=@();$ids=@([P1AccessProbe]::Threads([uint32]$TargetProcessId));if($ExpectedThreadId -gt 0 -and $ids -notcontains [uint32]$ExpectedThreadId){throw "expected initial thread $ExpectedThreadId not found"}
foreach($tid in $ids){$rights=@();foreach($item in $threadMasks.GetEnumerator()){$r=Test-Handle thread $item.Value $tid ([IntPtr]::Zero);$rights += [pscustomobject]@{right=$item.Key;mask=$r.mask;success=$r.success;win32Error=$r.win32Error}};$threads += [pscustomobject]@{threadId=$tid;rights=$rights}}
$tokens=@();$defaultDaclHash=$null;$processHandle=[P1AccessProbe]::OpenProcess([uint32]0x1000,$false,[uint32]$TargetProcessId)
$tokenReachable=$processHandle -ne [IntPtr]::Zero
$tokenOpenProcessError=if($tokenReachable){0}else{[P1AccessProbe]::Error()}
if($tokenReachable){try {foreach($item in $tokenMasks.GetEnumerator()){$r=Test-Handle token $item.Value 0 $processHandle;$tokens += [pscustomobject]@{right=$item.Key;mask=$r.mask;success=$r.success;win32Error=$r.win32Error}};$defaultDaclHash=[P1AccessProbe]::DefaultDaclHash($processHandle)}finally{[void][P1AccessProbe]::CloseHandle($processHandle)}}
$result=[pscustomobject]@{timestamp=[DateTimeOffset]::Now.ToString('o');targetProcessId=$TargetProcessId;caller=$identity.Name;callerElevated=$false;processRights=$process;threads=$threads;tokenReachable=$tokenReachable;tokenOpenProcessError=$tokenOpenProcessError;tokenRights=$tokens;tokenDefaultDaclSha256=$defaultDaclHash}
$result | ConvertTo-Json -Depth 7 | Set-Content -LiteralPath $OutputFile -Encoding utf8
