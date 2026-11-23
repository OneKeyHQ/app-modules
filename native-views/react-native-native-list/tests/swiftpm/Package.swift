// swift-tools-version:5.9
import PackageDescription

// Compile the symlink to the shipping helper, matching other native SwiftPM tests.
// Geometry tests run on macOS; UIKit and complete-app checks are separate.
let package = Package(
  name: "NativeListScrollAlignment",
  platforms: [.macOS(.v12)],
  targets: [
    .target(name: "NativeListScrollAlignment"),
    .testTarget(
      name: "NativeListScrollAlignmentTests",
      dependencies: ["NativeListScrollAlignment"]
    ),
  ]
)
