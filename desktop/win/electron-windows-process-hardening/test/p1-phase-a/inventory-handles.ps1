param(
  [Parameter(Mandatory=$true)][int]$TargetProcessId,
  [Parameter(Mandatory=$true)][string]$OutputFile,
  [int]$HelperProcessId=0
)
$ErrorActionPreference='Stop'
$identity=[Security.Principal.WindowsIdentity]::GetCurrent()
$principal=[Security.Principal.WindowsPrincipal]::new($identity)
if($principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)){throw 'handle inventory requires ordinary token'}
Add-Type -TypeDefinition @'
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.InteropServices;
public static class P1HandleInventory {
 public class Row { public ulong HolderPid,HandleValue; public uint GrantedAccess,Attributes; public ushort ObjectTypeIndex; }
 public class Inventory { public uint ObserverPid,ReturnLength; public long EntryCount; public bool TargetAnchorValidated,Verified; public string Error; public Row[] TargetHandles,HelperHandles; }
 [DllImport("kernel32.dll",SetLastError=true)] static extern IntPtr OpenProcess(uint rights,bool inherit,uint pid);
 [DllImport("kernel32.dll",SetLastError=true)] static extern bool CloseHandle(IntPtr h);
 [DllImport("kernel32.dll")] static extern uint GetCurrentProcessId();
 [DllImport("kernel32.dll")] static extern IntPtr GetCurrentProcess();
 [DllImport("kernel32.dll",SetLastError=true)] static extern bool DuplicateHandle(IntPtr source,IntPtr value,IntPtr target,out IntPtr copy,uint access,bool inherit,uint options);
 [DllImport("kernelbase.dll")] static extern bool CompareObjectHandles(IntPtr first,IntPtr second);
 [DllImport("ntdll.dll")] static extern int NtQuerySystemInformation(int informationClass,IntPtr buffer,uint length,out uint returnLength);
 public static Inventory Find(uint targetPid,uint helperPid) {
  if(IntPtr.Size!=8)throw new Exception("x64 only");
  IntPtr mine=OpenProcess(0x1000,false,targetPid);
  if(mine==IntPtr.Zero)throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error(),"target QLI denied");
  IntPtr buffer=IntPtr.Zero;
  try {
   uint length=1024*1024,needed=0;int status=0;var timer=Stopwatch.StartNew();bool success=false;
   for(int attempt=0;attempt<8&&timer.ElapsedMilliseconds<5000;attempt++){
    if(length>64*1024*1024)throw new Exception("system handle table exceeds 64MiB bound");
    buffer=Marshal.AllocHGlobal((int)length);
    status=NtQuerySystemInformation(64,buffer,length,out needed);
    if(status==unchecked((int)0xC0000004)){
     Marshal.FreeHGlobal(buffer);buffer=IntPtr.Zero;
     ulong grown=Math.Max((ulong)length*2,(ulong)needed+4096);
     if(grown>64UL*1024*1024)throw new Exception("system handle table exceeds 64MiB bound");
     length=(uint)grown;continue;
    }
    if(status!=0)throw new Exception("NtQuerySystemInformation status=0x"+status.ToString("X8"));
    success=true;break;
   }
   if(!success)throw new Exception("system handle query retry/time bound exceeded");
   if(needed<16||needed>length)throw new Exception("system handle returnLength invalid");
   long count=Marshal.ReadInt64(buffer,0);
   if(count<0||count>1000000||count>(needed-16)/40)throw new Exception("system handle table count/layout invalid");
   ulong observerPid=GetCurrentProcessId(),ownHandle=unchecked((ulong)mine.ToInt64());
   ushort processType=0;bool anchored=false;
   for(long i=0;i<count;i++){
    int offset=checked((int)(16+i*40));
    ulong holder=unchecked((ulong)Marshal.ReadInt64(buffer,offset+8));
    ulong value=unchecked((ulong)Marshal.ReadInt64(buffer,offset+16));
    if(holder==observerPid&&value==ownHandle){
     uint access=unchecked((uint)Marshal.ReadInt32(buffer,offset+24));
     ushort type=unchecked((ushort)Marshal.ReadInt16(buffer,offset+30));
     if(access!=0x1000||type==0)throw new Exception("target QLI anchor/layout invalid");
     processType=type;anchored=true;break;
    }
   }
   if(!anchored)throw new Exception("target process object anchor absent");
   var targetRows=new List<Row>();var helperRows=new List<Row>();
   int unresolved=0;
   for(long i=0;i<count;i++){
    if(timer.ElapsedMilliseconds>=5000)throw new Exception("system handle identity verification time bound exceeded");
    int offset=checked((int)(16+i*40));
    ulong holder=unchecked((ulong)Marshal.ReadInt64(buffer,offset+8));
    ushort type=unchecked((ushort)Marshal.ReadInt16(buffer,offset+30));
    if(type!=processType&&holder!=helperPid)continue;
    var row=new Row{HolderPid=holder,HandleValue=unchecked((ulong)Marshal.ReadInt64(buffer,offset+16)),
      GrantedAccess=unchecked((uint)Marshal.ReadInt32(buffer,offset+24)),
      ObjectTypeIndex=unchecked((ushort)Marshal.ReadInt16(buffer,offset+30)),
      Attributes=unchecked((uint)Marshal.ReadInt32(buffer,offset+32))};
    bool matches=false;
    if(type==processType){
     // Redacted kernel addresses are not identities. Duplicate process handles
     // with zero rights and compare objects without requiring QLI on each one.
     if(holder==observerPid&&row.HandleValue==ownHandle)matches=true;
     else {
      IntPtr owner=holder<=uint.MaxValue?OpenProcess(0x40,false,(uint)holder):IntPtr.Zero;
      if(owner==IntPtr.Zero)unresolved++;
      else try {
       IntPtr copy;
       if(!DuplicateHandle(owner,new IntPtr(unchecked((long)row.HandleValue)),GetCurrentProcess(),out copy,0,false,0))unresolved++;
       else try {matches=CompareObjectHandles(mine,copy);} finally {CloseHandle(copy);}
      } finally {CloseHandle(owner);}
     }
    }
    if(matches)targetRows.Add(row);
    if(helperPid!=0&&holder==helperPid)helperRows.Add(row);
   }
   if(targetRows.Count==0)throw new Exception("anchored target object has no rows");
   return new Inventory{ObserverPid=(uint)observerPid,ReturnLength=needed,EntryCount=count,
     TargetAnchorValidated=true,Verified=unresolved==0,
     Error=unresolved==0?null:"process handle identity unverified (holder access denied or handle changed): "+unresolved,
     TargetHandles=targetRows.ToArray(),HelperHandles=helperRows.ToArray()};
  } finally {if(buffer!=IntPtr.Zero)Marshal.FreeHGlobal(buffer);CloseHandle(mine);}
 }
}
'@
try {
  $found=[P1HandleInventory]::Find([uint32]$TargetProcessId,[uint32]$HelperProcessId)
  $convert={param($row) [pscustomobject]@{holderPid=$row.HolderPid;handle=('0x{0:X}' -f $row.HandleValue);grantedAccess=('0x{0:X8}' -f $row.GrantedAccess);attributes=('0x{0:X8}' -f $row.Attributes);objectTypeIndex=$row.ObjectTypeIndex}}
  $target=@($found.TargetHandles | ForEach-Object { & $convert $_ })
  $helper=@($found.HelperHandles | ForEach-Object { & $convert $_ })
  $result=[pscustomobject]@{timestamp=[DateTimeOffset]::Now.ToString('o');targetProcessId=$TargetProcessId;helperProcessId=$HelperProcessId;observerPid=$found.ObserverPid;callerElevated=$false;scope='target_process_object_point_in_time';verified=$found.Verified;error=$found.Error;targetAnchorValidated=$found.TargetAnchorValidated;returnLength=$found.ReturnLength;entryCount=$found.EntryCount;targetHandles=$target;helperHandles=$helper}
  $result | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $OutputFile -Encoding utf8
  if(-not $found.Verified){exit 2}
} catch {
  [pscustomobject]@{timestamp=[DateTimeOffset]::Now.ToString('o');targetProcessId=$TargetProcessId;helperProcessId=$HelperProcessId;callerElevated=$false;scope='target_process_object_point_in_time';verified=$false;error=$_.Exception.Message} | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $OutputFile -Encoding utf8
  exit 2
}
