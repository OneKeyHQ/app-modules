# Android source build

Prerequisites: Python 3.9+, JDK 24.0.1, and Android SDK Platform 35. SDK Java is
compiled with `javac --release 8`; the ABI restoration tools run on the same
JDK 24.0.1. The exact compiler version reproduces the validated recovered
artifacts, including compiler-generated capture initialization and metadata.
This standalone build does not change the App's JDK or dependencies.

```sh
JAVA_HOME=/path/to/jdk-24.0.1 ANDROID_HOME=/path/to/android-sdk \
  python3 scripts/build_android_source.py
```

`--java-home` and `--android-jar` can select those prerequisites explicitly.
The build downloads ASM, ASM Commons, and ASM Tree 9.7.1 from Maven Central
into `.build/deps/` and verifies the fixed SHA-256 digests in the script.
For an offline build, pass `--asm-cache /path/to/cache` containing
`asm-9.7.1.jar`, `asm-commons-9.7.1.jar`, and `asm-tree-9.7.1.jar`.
Cached files are verified before being copied into `.build/deps/`. With
`--asm-cache`, a missing dependency fails instead of accessing the network.

All generated files stay under `.build/`. The output JARs are:

- `.build/android/artifacts/jpush-android-5.9.0-recovered.jar`
- `.build/android/artifacts/jcore-android-5.2.0-recovered.jar`
- `.build/android/artifacts/jpush-jcore-recovered.jar`

Use either the two individual JARs together or the combined JAR; including
both forms creates duplicate classes. This package does not change any App
dependency or provide React Native integration.

The only SDK inputs are `source/android/java/`, `source/android/compile-only/`,
the five TSV metadata files, and the two Java build tools in `tools/`.
No original SDK JAR or bytecode is read. The compile-only dynamic-provider
declaration is excluded from all output JARs.

The postcompile step restores class/member identities and ABI metadata from
the TSV inputs. The build fails on any missing ABI record, any class-set
difference, incomplete artifact ownership, or a packaged compile-only class.
Only three named compiler-generated enum helper methods may exceed the ABI
metadata; any other additional member fails the build. Annotation processing
is disabled, and an Android platform JAR containing JPush/JCore SDK classes
is rejected.
`.build/android/build.json` records class counts, dependency/output hashes,
and the compile command. Compiler logs and ABI diagnostics are alongside it.
Additional compiler-generated members, when present, are listed in
`.build/android/abi-extra.tsv`.

Compilation and ABI checks do not establish runtime equivalence or complete
provider closure. The source declares eight native methods but supplies no
native `.so` implementations. External dynamic providers, network behavior,
Android manifest/resources, device delivery, and application integration
require separate validation before replacing a running application SDK.
