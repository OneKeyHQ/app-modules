// swift-tools-version:5.9
import PackageDescription

// Self-contained SwiftPM harness for the DETERMINISTIC range-downloader logic.
//
// The single source file is a SYMLINK to the production
// `ios/RangeDownloadLogic.swift`, so `swift test` compiles and exercises the REAL
// shipping code — no copy to drift. The file is dependency-free (Foundation +
// CommonCrypto only), which is exactly why it could be extracted out of
// `ReactNativeRangeDownloader.swift` (NitroModules / ReactNativeNativeLogger /
// background URLSession) and tested in a plain unit-test process.
//
// Run:  swift test --package-path tests/swiftpm
let package = Package(
  name: "RangeDownloadLogic",
  platforms: [.macOS(.v12)],
  targets: [
    .target(name: "RangeDownloadLogic"),
    .testTarget(
      name: "RangeDownloadLogicTests",
      dependencies: ["RangeDownloadLogic"]
    ),
  ]
)
