// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BundleStorageSchemaPolicy",
    platforms: [.macOS(.v12)],
    targets: [
        .target(
            name: "BundleStorageSchemaPolicy",
            path: "ios",
            exclude: [
                "ReactNativeBundleUpdate.swift", "BundleDiffReconstruction.swift", "BundleVerificationRun.swift",
                "BundleDiffZipExtractor.h", "BundleDiffZipExtractor.mm", "tests",
            ],
            sources: ["BundleStorageSchemaPolicy.swift"]
        ),
        .target(
            name: "BundleDiffReconstruction",
            path: "ios",
            exclude: [
                "ReactNativeBundleUpdate.swift", "BundleStorageSchemaPolicy.swift",
                "BundleDiffZipExtractor.h", "BundleDiffZipExtractor.mm", "tests",
            ],
            sources: ["BundleDiffReconstruction.swift", "BundleVerificationRun.swift"]
        ),
        .testTarget(
            name: "BundleStorageSchemaPolicyTests",
            dependencies: ["BundleStorageSchemaPolicy", "BundleDiffReconstruction"],
            path: "tests/swift"
        ),
    ]
)
