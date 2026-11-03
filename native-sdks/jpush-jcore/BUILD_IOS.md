# iOS source build and link gate

Recovered iOS versions: JPush 5.9.0 and JCore 5.2.1.

The source implementations cover the original SDK class and method sets:
271 classes / 5006 methods on device arm64 and 206 / 3690 on both simulator
architectures, including categories. All 34 exported C functions and 77 / 71 / 71
SDK global initializers are present. A passing build receipt establishes source
symbol closure for its exact inputs; it does not establish device behavior.
JCore and JPush are packaged separately according to original SDK ownership.
No original SDK object, archive, disassembly, or diagnostic fixture is an input.

Prerequisites: macOS, Python 3.9+, and Xcode with the iPhoneOS and
iPhoneSimulator SDKs selected through `xcode-select`.

```sh
python3 scripts/build_ios_source.py
```

The script builds every `source/ios/j*/*.m` for iOS arm64, simulator arm64, and
simulator x86_64, targeting iOS 15. It uses ARC and strict compiler warnings
(`-Wall -Wextra -Werror -Wno-unused-parameter`). It restores approved property
metadata from `source/ios/abi/` without changing compiled instructions.
Only this package's sources, headers, ABI metadata, and Apple SDKs participate;
the build never reads or links original JPush/JCore objects or fixture mocks.

Each target produces `.build/ios/<target>/libJCore.a` and `libJPush.a`.
JCore is force-loaded into an isolated executable that includes its public
header. A second public-header consumer force-loads both independent archives
for JPush. Both use `-undefined error`; the consumers are linked, never run.
The framework set is Foundation, CoreFoundation, UIKit, CoreLocation,
UserNotifications, SystemConfiguration, Security, CoreTelephony, CFNetwork,
Network, MobileCoreServices, WebKit, and CoreGraphics. `libresolv` supplies mDNS resolver
functions and zlib supplies compression (`-lresolv -lz`). A missing SDK class,
function, or constant makes the command fail even if archive creation succeeds.
Use JCore alone or JPush together with JCore; each object is packaged once.

All output stays in `.build/`. `.build/ios/build.json` records the source
hashes, metadata adjustments, archive hashes, per-target compile/link status,
and unresolved symbols. Compiler and linker diagnostics are stored next to
each target's archive. This repository does not commit those generated files.

A passing link proves only static symbol closure for these source inputs.
Objective-C selector behavior, transport and push delivery, framework/device
availability, and real application integration require separate validation.
No App dependency or React Native package is changed by this build.

The recovery defines safe failures for original allocation failures, receive
buffer overflow, truncated packet reads, and short crash-backtrace allocations.
Decoded TCP responses preserve their decoded prefix and original packet length;
bytes beyond the decoded data are zero-filled. Network data conversion uses a
bounded heap temporary. CF Create/Copy references are released after use, and
borrowed Wi-Fi interfaces retain their container ownership. These changes bound
original undefined memory behavior; they are not byte-equivalence claims for
out-of-bounds original memory.

The recovered TLS paths retain the original Basic X.509 trust policy, including
its lack of hostname verification. Real device TLS, APNs delivery and App
integration remain separate acceptance work. Host instruction comparisons and
sanitizer checks do not establish those results.

Raw static metadata retains eight inherited-layout differences per architecture
for JCORELightCache, JCORELargeCache and JCORENetDataMoniter. Original binaries
use older superclass sizes; Objective-C runtime ivar sliding produces the same
loaded sizes and offsets as the source. These differences are documented rather
than patched away. Current Apple SDK optional delegate-protocol additions are
framework metadata, separate from SDK-owned protocol contracts.
