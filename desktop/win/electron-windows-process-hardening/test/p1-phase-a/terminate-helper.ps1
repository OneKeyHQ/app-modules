param([Parameter(Mandatory=$true)][int]$TargetProcessId,[Parameter(Mandatory=$true)][string]$ExpectedPath,[Parameter(Mandatory=$true)][string]$OutputFile)
$ErrorActionPreference='Stop'
$identity=[Security.Principal.WindowsIdentity]::GetCurrent()
$principal=[Security.Principal.WindowsPrincipal]::new($identity)
if($principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)){throw 'helper termination must use ordinary token'}
Add-Type -TypeDefinition @'
using System;
using System.Text;
using System.Runtime.InteropServices;
public static class P1TerminateHelper {
 [DllImport("kernel32.dll",SetLastError=true)] public static extern IntPtr OpenProcess(uint rights,bool inherit,uint pid);
 [DllImport("kernel32.dll",CharSet=CharSet.Unicode,SetLastError=true)] public static extern bool QueryFullProcessImageName(IntPtr process,uint flags,StringBuilder name,ref uint length);
 [DllImport("kernel32.dll",SetLastError=true)] public static extern bool TerminateProcess(IntPtr process,uint exitCode);
 [DllImport("kernel32.dll",SetLastError=true)] public static extern bool CloseHandle(IntPtr handle);
 public static int Error(){return Marshal.GetLastWin32Error();}
}
'@
$query=[P1TerminateHelper]::OpenProcess([uint32]0x1000,$false,[uint32]$TargetProcessId)
if($query -eq [IntPtr]::Zero){throw "OpenProcess QLI denied: $([P1TerminateHelper]::Error())"}
try {
 $name=[Text.StringBuilder]::new(32768);$length=[uint32]$name.Capacity
 if(-not [P1TerminateHelper]::QueryFullProcessImageName($query,0,$name,[ref]$length)){throw "QueryFullProcessImageName failed: $([P1TerminateHelper]::Error())"}
 $actual=$name.ToString()
 if(-not [string]::Equals([IO.Path]::GetFullPath($actual),[IO.Path]::GetFullPath($ExpectedPath),[StringComparison]::OrdinalIgnoreCase)){throw "PID identity mismatch: $actual"}
} finally {[void][P1TerminateHelper]::CloseHandle($query)}
$kill=[P1TerminateHelper]::OpenProcess([uint32]1,$false,[uint32]$TargetProcessId)
if($kill -eq [IntPtr]::Zero){throw "OpenProcess TERMINATE denied: $([P1TerminateHelper]::Error())"}
try {if(-not [P1TerminateHelper]::TerminateProcess($kill,[uint32]0xEA11)){throw "TerminateProcess failed: $([P1TerminateHelper]::Error())"}} finally {[void][P1TerminateHelper]::CloseHandle($kill)}
[pscustomobject]@{timestamp=[DateTimeOffset]::Now.ToString('o');targetProcessId=$TargetProcessId;verifiedPath=$actual;requestedRight='PROCESS_TERMINATE';callerElevated=$false} | ConvertTo-Json | Set-Content -LiteralPath $OutputFile -Encoding utf8
