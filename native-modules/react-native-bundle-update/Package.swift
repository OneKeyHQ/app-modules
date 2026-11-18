// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BundleStorageSchemaPolicy",
    targets: [
        .target(
            name: "BundleStorageSchemaPolicy",
            path: "ios",
            exclude: ["ReactNativeBundleUpdate.swift", "BundleDiffReconstruction.swift"],
            sources: ["BundleStorageSchemaPolicy.swift"]
        ),
        .target(
            name: "BundleDiffReconstruction",
            path: "ios",
            exclude: ["ReactNativeBundleUpdate.swift", "BundleStorageSchemaPolicy.swift"],
            sources: ["BundleDiffReconstruction.swift"]
        ),
        .testTarget(
            name: "BundleStorageSchemaPolicyTests",
            dependencies: ["BundleStorageSchemaPolicy", "BundleDiffReconstruction"],
            path: "tests/swift"
        ),
    ]
)
