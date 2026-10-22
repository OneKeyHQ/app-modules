param(
  [Parameter(Mandatory = $true)]
  [uint32]$ProcessId
)

$ErrorActionPreference = 'Stop'
Add-Type -TypeDefinition @'
using System;
using System.Runtime.InteropServices;
public static class ProcessAccessProbe {
    [DllImport("kernel32.dll", SetLastError = true)]
    public static extern IntPtr OpenProcess(uint access, bool inheritHandle, uint processId);
    [DllImport("kernel32.dll", SetLastError = true)]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool CloseHandle(IntPtr handle);
}
'@

$masks = [ordered]@{
  QUERY_LIMITED_INFORMATION = 0x00001000
  SYNCHRONIZE = 0x00100000
  TERMINATE = 0x00000001
  VM_READ = 0x00000010
  VM_WRITE = 0x00000020
  VM_OPERATION = 0x00000008
  CREATE_THREAD = 0x00000002
  DUP_HANDLE = 0x00000040
  SET_INFORMATION = 0x00000200
  WRITE_DAC = 0x00040000
  WRITE_OWNER = 0x00080000
}

$result = [ordered]@{}
foreach ($entry in $masks.GetEnumerator()) {
  $handle = [ProcessAccessProbe]::OpenProcess([uint32]$entry.Value, $false, $ProcessId)
  if ($handle -eq [IntPtr]::Zero) {
    $errorCode = [Runtime.InteropServices.Marshal]::GetLastWin32Error()
    $opened = $false
  } else {
    if (-not [ProcessAccessProbe]::CloseHandle($handle)) {
      throw "CloseHandle failed for $($entry.Key): $([Runtime.InteropServices.Marshal]::GetLastWin32Error())"
    }
    $errorCode = 0
    $opened = $true
  }
  $result[$entry.Key] = @{ opened = $opened; win32Error = $errorCode }
}
$result | ConvertTo-Json -Compress -Depth 4
