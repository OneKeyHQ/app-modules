param(
  [Parameter(Mandatory = $true)]
  [uint32]$ProcessId
)

$ErrorActionPreference = 'Stop'
Add-Type -TypeDefinition @'
using System;
using System.Runtime.InteropServices;
public static class ProcessAccessProbe {
    [StructLayout(LayoutKind.Sequential)]
    struct Luid { public uint LowPart; public int HighPart; }
    [StructLayout(LayoutKind.Sequential)]
    struct TokenPrivileges { public uint Count; public Luid Luid; public uint Attributes; }
    [DllImport("kernel32.dll")]
    static extern IntPtr GetCurrentProcess();
    [DllImport("advapi32.dll", SetLastError = true)]
    static extern bool OpenProcessToken(IntPtr process, uint access, out IntPtr token);
    [DllImport("advapi32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
    static extern bool LookupPrivilegeValue(string system, string name, out Luid luid);
    [DllImport("advapi32.dll", SetLastError = true)]
    static extern bool AdjustTokenPrivileges(IntPtr token, bool disableAll, ref TokenPrivileges state,
        uint length, IntPtr previous, IntPtr returned);
    [DllImport("kernel32.dll", SetLastError = true)]
    public static extern IntPtr OpenProcess(uint access, bool inheritHandle, uint processId);
    [DllImport("kernel32.dll", SetLastError = true)]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool CloseHandle(IntPtr handle);
    public static void DisableDebugPrivilege() {
        IntPtr token;
        if (!OpenProcessToken(GetCurrentProcess(), 0x20, out token))
            throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error());
        try {
            Luid luid;
            if (!LookupPrivilegeValue(null, "SeDebugPrivilege", out luid))
                throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error());
            var state = new TokenPrivileges { Count = 1, Luid = luid, Attributes = 0 };
            if (!AdjustTokenPrivileges(token, false, ref state, 0, IntPtr.Zero, IntPtr.Zero))
                throw new System.ComponentModel.Win32Exception(Marshal.GetLastWin32Error());
            int error = Marshal.GetLastWin32Error();
            // ERROR_NOT_ALL_ASSIGNED means this token never had SeDebugPrivilege.
            if (error != 0 && error != 1300)
                throw new System.ComponentModel.Win32Exception(error);
        } finally { CloseHandle(token); }
    }
}
'@

# The probe is a fresh PowerShell process with no impersonation token. Elevated
# CI runners must not bypass the target DACL through SeDebugPrivilege.
[ProcessAccessProbe]::DisableDebugPrivilege()

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
