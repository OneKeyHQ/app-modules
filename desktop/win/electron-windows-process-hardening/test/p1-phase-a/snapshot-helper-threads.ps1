param([Parameter(Mandatory=$true)][int]$TargetProcessId,[Parameter(Mandatory=$true)][string]$OutputFile)
$ErrorActionPreference='Stop'
$identity=[Security.Principal.WindowsIdentity]::GetCurrent()
$principal=[Security.Principal.WindowsPrincipal]::new($identity)
if($principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)){throw 'thread snapshot requires ordinary token'}
Add-Type -TypeDefinition @'
using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
public static class P1SystemThreads {
 public struct Row { public uint ThreadId,State,WaitReason; public string CreationTimeFileTimeUtc,CountQueryStatus; public uint? SuspendCount; public int OpenThreadError; }
 [DllImport("kernel32.dll",SetLastError=true)] static extern IntPtr OpenThread(uint access,bool inherit,uint tid);
 [DllImport("kernel32.dll")] static extern bool CloseHandle(IntPtr h);
 [DllImport("ntdll.dll")] static extern int NtQueryInformationThread(IntPtr h,uint cls,out uint value,uint len,out uint returned);
 public static Row[] WithSuspendCounts(Row[] rows) {
  for(int i=0;i<rows.Length;i++){
   IntPtr h=OpenThread(0x800,false,rows[i].ThreadId);
   if(h==IntPtr.Zero){rows[i].OpenThreadError=Marshal.GetLastWin32Error();rows[i].CountQueryStatus="open_failed";continue;}
   try {uint value,returned;int status=NtQueryInformationThread(h,35,out value,4,out returned);
    rows[i].CountQueryStatus="0x"+status.ToString("X8");
    if(status==0&&returned==4)rows[i].SuspendCount=value;
    else if(status==0)rows[i].CountQueryStatus="invalid_length";
   } finally {CloseHandle(h);}
  }
  return rows;
 }
 [DllImport("ntdll.dll")] static extern int NtQuerySystemInformation(int infoClass,IntPtr buffer,uint length,out uint needed);
 public static Row[] ForProcess(uint pid) {
  if(IntPtr.Size!=8)throw new Exception("x64 only");
  IntPtr buffer=IntPtr.Zero;uint length=1024*1024,needed=0;
  try {
   for(;;){
    if(length>64*1024*1024)throw new Exception("process table exceeds 64MiB bound");
    buffer=Marshal.AllocHGlobal((int)length);
    int status=NtQuerySystemInformation(5,buffer,length,out needed);
    if(status==unchecked((int)0xC0000004)){Marshal.FreeHGlobal(buffer);buffer=IntPtr.Zero;length=Math.Max(length*2,needed+4096);continue;}
    if(status!=0)throw new Exception("NtQuerySystemInformation status=0x"+status.ToString("X8"));
    break;
   }
   long offset=0;
   while(offset+256<=length){
    int next=Marshal.ReadInt32(buffer,(int)offset);
    int count=Marshal.ReadInt32(buffer,(int)offset+4);
    ulong processId=unchecked((ulong)Marshal.ReadInt64(buffer,(int)offset+80));
    if(processId==pid){
     if(count<0||count>4096)throw new Exception("invalid thread count");
     long entryEnd=next==0?length:offset+next;
     if(offset+256L+count*80L>entryEnd)throw new Exception("thread entries outside process record");
     var rows=new List<Row>();
     for(int i=0;i<count;i++){
      int item=checked((int)(offset+256L+i*80L));
      rows.Add(new Row{CreationTimeFileTimeUtc=Marshal.ReadInt64(buffer,item+16).ToString(),ThreadId=unchecked((uint)Marshal.ReadInt64(buffer,item+48)),State=unchecked((uint)Marshal.ReadInt32(buffer,item+68)),WaitReason=unchecked((uint)Marshal.ReadInt32(buffer,item+72))});
     }
     return rows.ToArray();
    }
    if(next<=0)break;
    offset+=next;
   }
   return new Row[0];
  } finally {if(buffer!=IntPtr.Zero)Marshal.FreeHGlobal(buffer);}
 }
}
'@
$snapshots=@();$confirmed=0;$previousIdentity=$null;$deadline=[DateTime]::UtcNow.AddSeconds(3)
do {
 $rows=@([P1SystemThreads]::WithSuspendCounts([P1SystemThreads]::ForProcess([uint32]$TargetProcessId)))
 $after=@([P1SystemThreads]::ForProcess([uint32]$TargetProcessId))
 $identityBefore=(@($rows | Sort-Object ThreadId | ForEach-Object {"$($_.ThreadId):$($_.CreationTimeFileTimeUtc)"}) -join ',')
 $identityAfter=(@($after | Sort-Object ThreadId | ForEach-Object {"$($_.ThreadId):$($_.CreationTimeFileTimeUtc)"}) -join ',')
 $stable=$identityBefore -eq $identityAfter
 $allSuspended=[bool]($rows.Count -gt 0 -and $stable -and @($rows | Where-Object {$_.State -ne 5 -or $_.CountQueryStatus -ne '0x00000000' -or $null -eq $_.SuspendCount -or $_.SuspendCount -le 0}).Count -eq 0 -and @($after | Where-Object {$_.State -ne 5}).Count -eq 0)
 $sample=[pscustomobject]@{timestamp=[DateTimeOffset]::UtcNow.ToString('o');threads=$rows;threadSetStable=$stable;allSuspended=$allSuspended}
 $snapshots+=$sample
 if($allSuspended){if($identityBefore -eq $previousIdentity){$confirmed++}else{$confirmed=1};$previousIdentity=$identityBefore}else{$confirmed=0;$previousIdentity=$null}
 Start-Sleep -Milliseconds 150
} while([DateTime]::UtcNow -lt $deadline)
[pscustomobject]@{targetProcessId=$TargetProcessId;callerElevated=$false;source='SystemProcessInformation + NtQueryInformationThread(ThreadSuspendCount)';threadAccess='0x00000800';requiredState=5;requiredSuspendCount='positive';stableConsecutiveSamples=2;observationWindowSeconds=3;confirmed=($confirmed -ge 2);snapshots=$snapshots} | ConvertTo-Json -Depth 7 | Set-Content -LiteralPath $OutputFile -Encoding utf8
