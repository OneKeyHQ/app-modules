param(
  [Parameter(Mandatory = $true)][string]$CrashpadArchive,
  [Parameter(Mandatory = $true)][string]$DependencyRoot,
  [Parameter(Mandatory = $true)][string]$WorkDirectory,
  [Parameter(Mandatory = $true)][string]$Ninja,
  [Parameter(Mandatory = $true)][string]$Python
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$PSNativeCommandArgumentPassing = 'Standard'

$module = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$source = [IO.Path]::GetFullPath($DependencyRoot)
$archive = [IO.Path]::GetFullPath($CrashpadArchive)
$work = [IO.Path]::GetFullPath($WorkDirectory)
$destination = Join-Path $work 'crashpad'
$sourceParent = Split-Path -Parent $source
$gn = Join-Path $sourceParent 'buildtools\win\gn.exe'
$patch = Join-Path $module 'src\crashpad_wrapper\fault-only-crashpad-ad1827d.patch'

function Assert-Hash([string]$path, [string]$expected) {
  if (-not [IO.File]::Exists($path)) { throw "Missing pinned input: $path" }
  $actual = (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash
  if ($actual -ne $expected) { throw "Pinned input changed: $path actual=$actual expected=$expected" }
}

function Hash-File([string]$path) {
  return (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash.ToLowerInvariant()
}

function Hash-Tree([string]$root) {
  if (-not [IO.Directory]::Exists($root)) { throw "Missing dependency tree: $root" }
  $root = [IO.Path]::GetFullPath($root)
  $entries = [Collections.Generic.List[string]]::new()
  Get-ChildItem -LiteralPath $root -Recurse -File -Force | ForEach-Object {
    $relative = [IO.Path]::GetRelativePath($root, $_.FullName).Replace('\', '/')
    if ($relative -match '(^|/)(\.git|out|__pycache__|\.cache)(/|$)') { return }
    $entries.Add($relative + "`0" + (Hash-File $_.FullName))
  }
  $entries.Sort([StringComparer]::Ordinal)
  return [Convert]::ToHexString([Security.Cryptography.SHA256]::HashData(
    [Text.Encoding]::UTF8.GetBytes([string]::Join("`n", $entries))
  )).ToLowerInvariant()
}

function Assert-Tree([string]$path, [string]$expected) {
  $actual = Hash-Tree $path
  if ($actual -ne $expected) { throw "Pinned dependency tree changed: $path actual=$actual expected=$expected" }
}

function Run-Checked([string]$label, [string]$program, [string[]]$arguments) {
  Write-Output "== $label =="
  & $program @arguments
  if ($LASTEXITCODE -ne 0) { throw "$label failed with exit code $LASTEXITCODE" }
}

if (-not [IO.Directory]::Exists($source)) { throw "Hydrated dependencies missing: $source" }
if (Test-Path -LiteralPath $work) { throw "Choose a new work directory: $work" }
if ($work.StartsWith($source + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
  throw 'Work directory cannot be inside hydrated dependencies'
}
if ($source.StartsWith($work + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
  throw 'Work directory cannot contain hydrated dependencies'
}
foreach ($path in @($Ninja, $Python)) {
  if (-not [IO.File]::Exists($path)) { throw "Required tool missing: $path" }
}

# The archive is Crashpad ad1827ddbc03f1c214030b31b380243ded660b95;
# its DEPS pins the fetched third-party source and compiler revisions.
Assert-Hash $archive '1A6D74BC7F4CA57FE6A4FC1AE47486477179D71D1B1B556E8F919A5149B0565D'
Assert-Hash (Join-Path $source 'DEPS') 'E0C861F45590A190BFDB1281E40632CA0B088C8D33DF946B0B62D160F2AE4BCC'
Assert-Hash (Join-Path $source 'snapshot\win\process_reader_win.h') 'D573366426480000E5C08ABCA1201A016D421CADE91DC6A623AD76E280634C0E'
Assert-Hash (Join-Path $source 'snapshot\win\process_reader_win.cc') 'A1B3C626AEE919AF0861BB642607650F2D993BC79BF2CEDB23E522EAD988F69C'
Assert-Hash (Join-Path $source 'snapshot\win\process_snapshot_win.cc') '308022D46AD805059F16F2CB10592FDD3B1782DBF8B75E714E57CDCD911AFB4F'
Assert-Hash (Join-Path $source 'snapshot\win\thread_snapshot_win.cc') '3325B5D5D4986098244D2D97AC30739CBF2F37798C8F2F277E0941D00022E3B1'
Assert-Hash $gn 'FA111680BAF435088415EBEDF4CDD41D07DB8651C482D944C841E0F7D46CA580'
Assert-Hash (Join-Path $source 'third_party\windows\clang\win-amd64\bin\clang-cl.exe') 'B79EDBEEF7A88A63AE8B76957BAABF95250FDC4B646FA1A8AE4AB12D7ECDD0B6'
Assert-Hash $patch 'F09C9715237BE407E8F54D39D2D86DCC63E493A878E021F9DF1886668ED7D49D'
$dependencyTrees = [ordered]@{
  miniChromium = @{ path = 'third_party\mini_chromium\mini_chromium'; sha256 = '8b15dfbba38479eab934db856192cb64b4b3aa58310bad840cc3f1c1c87f22a7' }
  zlib = @{ path = 'third_party\zlib\zlib'; sha256 = '8be7493a713bdddd690b48906212ad6d8c8ae4eff377d0fcc980949340b7515a' }
  clang = @{ path = 'third_party\windows\clang\win-amd64'; sha256 = '16f227a10626977ff66d66658a4df92b26b7686d1a7a6f02a12f286b6bdfd8fe' }
}
foreach ($name in $dependencyTrees.Keys) {
  Assert-Tree (Join-Path $source $dependencyTrees[$name].path) $dependencyTrees[$name].sha256
}
Assert-Tree (Join-Path $sourceParent 'buildtools') '96df3d27e93e577704734d4fd396d00b8507f0a7d3c6836c68948597570a1dde'

New-Item -ItemType Directory -Path $work -ErrorAction Stop | Out-Null
New-Item -ItemType Directory -Path $destination -ErrorAction Stop | Out-Null
Run-Checked 'Extract fixed Crashpad source' 'tar.exe' @('-xzf', $archive, '--strip-components=1', '-C', $destination)
Assert-Hash (Join-Path $destination 'DEPS') 'E0C861F45590A190BFDB1281E40632CA0B088C8D33DF946B0B62D160F2AE4BCC'
foreach ($name in $dependencyTrees.Keys) {
  $relative = $dependencyTrees[$name].path
  & robocopy.exe (Join-Path $source $relative) (Join-Path $destination $relative) /E /XD out .git /NP /NFL /NDL | Out-Null
  if ($LASTEXITCODE -ge 8) { throw "Pinned $name copy failed with exit code $LASTEXITCODE" }
  Assert-Tree (Join-Path $destination $relative) $dependencyTrees[$name].sha256
}
& robocopy.exe (Join-Path $sourceParent 'buildtools') (Join-Path $work 'buildtools') /E /XD .git /NP /NFL /NDL
if ($LASTEXITCODE -ge 8) { throw "Buildtools copy failed with exit code $LASTEXITCODE" }
Assert-Tree (Join-Path $work 'buildtools') '96df3d27e93e577704734d4fd396d00b8507f0a7d3c6836c68948597570a1dde'
Run-Checked 'Check fixed Crashpad patch' 'git.exe' @('-C', $destination, 'apply', '--check', $patch)
Run-Checked 'Apply fixed Crashpad patch' 'git.exe' @('-C', $destination, 'apply', $patch)
New-Item -ItemType Directory -Path (Join-Path $destination 'onekey_wrapper') -ErrorAction Stop | Out-Null
foreach ($name in @('BUILD.gn', 'capture.cc', 'capture.h')) {
  Copy-Item -LiteralPath (Join-Path $module "src\crashpad_wrapper\$name") -Destination (Join-Path $destination "onekey_wrapper\$name") -ErrorAction Stop
}

Push-Location $destination
try {
  Run-Checked 'Generate fixed x64 Crashpad build' (Join-Path $work 'buildtools\win\gn.exe') @(
    'gen', 'out/x64', '--root-target=//onekey_wrapper:all', "--script-executable=$Python", '--args=target_cpu="x64" is_debug=false'
  )
  Run-Checked 'Build fault-only capture DLL' $Ninja @('-C', 'out/x64', 'onekey_capture_test')
} finally {
  Pop-Location
}

$dll = Join-Path $destination 'out\x64\onekey_capture_test.dll'
$pdb = Join-Path $destination 'out\x64\onekey_capture_test.pdb'
if (-not [IO.File]::Exists($dll) -or -not [IO.File]::Exists($pdb)) { throw 'Expected DLL or PDB missing' }
$ninjaVersion = (& $Ninja --version) -join "`n"
if ($LASTEXITCODE -ne 0) { throw 'Could not read Ninja version' }
$pythonVersion = (& $Python --version) -join "`n"
if ($LASTEXITCODE -ne 0) { throw 'Could not read Python version' }
$clang = Join-Path $source 'third_party\windows\clang\win-amd64\bin\clang-cl.exe'
$clangVersion = (& $clang --version | Select-Object -First 1) -join ''
if ($LASTEXITCODE -ne 0) { throw 'Could not read Clang version' }
$receipt = [ordered]@{
  schema = 'onekey-fault-only-capture-build-v1'
  crashpadRevision = 'ad1827ddbc03f1c214030b31b380243ded660b95'
  crashpadArchiveSha256 = Hash-File $archive
  depsSha256 = Hash-File (Join-Path $destination 'DEPS')
  patchSha256 = Hash-File $patch
  wrapperSha256 = [ordered]@{
    'BUILD.gn' = Hash-File (Join-Path $module 'src\crashpad_wrapper\BUILD.gn')
    'capture.cc' = Hash-File (Join-Path $module 'src\crashpad_wrapper\capture.cc')
    'capture.h' = Hash-File (Join-Path $module 'src\crashpad_wrapper\capture.h')
  }
  toolSha256 = [ordered]@{
    gn = Hash-File $gn
    clang = Hash-File $clang
  }
  dependencyTreeSha256 = [ordered]@{
    miniChromium = Hash-Tree (Join-Path $destination $dependencyTrees.miniChromium.path)
    zlib = Hash-Tree (Join-Path $destination $dependencyTrees.zlib.path)
    clang = Hash-Tree (Join-Path $destination $dependencyTrees.clang.path)
    buildtools = Hash-Tree (Join-Path $work 'buildtools')
  }
  toolVersion = [ordered]@{
    ninja = $ninjaVersion
    python = $pythonVersion
    clang = $clangVersion
  }
  buildArgs = 'target_cpu="x64" is_debug=false; root-target=//onekey_wrapper:all; ninja=onekey_capture_test'
  captureSha256 = Hash-File $dll
  capturePdbSha256 = Hash-File $pdb
}
$receiptPath = "$dll.receipt.json"
$receipt | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $receiptPath -Encoding utf8NoBOM
Write-Output "CAPTURE_DLL=$dll"
Write-Output "CAPTURE_SHA256=$($receipt.captureSha256)"
Write-Output "CAPTURE_PDB=$pdb"
Write-Output "CAPTURE_RECEIPT=$receiptPath"
