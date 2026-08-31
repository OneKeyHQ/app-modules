// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BundleStorageSchemaPolicy",
    targets: [
        .target(
            name: "BundleStorageSchemaPolicy",
            path: "ios",
            exclude: ["ReactNativeBundleUpdate.swift"],
            sources: ["BundleStorageSchemaPolicy.swift"]
        ),
        .testTarget(
            name: "BundleStorageSchemaPolicyTests",
            dependencies: ["BundleStorageSchemaPolicy"],
            path: "tests/swift"
        ),
    ]
)
