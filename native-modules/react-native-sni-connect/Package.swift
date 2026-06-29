// swift-tools-version: 5.9

import PackageDescription

let package = Package(
  name: "SniConnectUnitTests",
  platforms: [
    .iOS(.v15),
    .macOS(.v13),
  ],
  targets: [
    .target(
      name: "SniConnectValidationCore",
      path: "ios",
      exclude: [
        "SniConnect-Bridging-Header.h",
        "SniConnect.swift",
        "SniConnect.mm",
        "SniConnectClient.swift",
        "SniConnectLog.swift",
        "Tests",
      ],
      sources: ["SniConnectValidation.swift", "SniConnectCore.swift"]
    ),
    .testTarget(
      name: "SniConnectValidationTests",
      dependencies: ["SniConnectValidationCore"],
      path: "ios/Tests/SniConnectValidationTests"
    ),
  ]
)
