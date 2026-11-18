package com.margelo.nitro.reactnativebundleupdate

import java.io.File
import java.nio.file.Files
import java.security.MessageDigest

// Dependency-free JVM tests compile the shipping BundleDiffReconstruction.kt directly.
fun main() {
    var assertions = 0
    fun expect(condition: Boolean, scenario: String) {
        check(condition) { "Assertion failed: $scenario" }
        assertions += 1
    }

    fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun sha256OfFile(path: String): String? {
        val f = File(path)
        if (!f.exists() || !f.isFile) return null
        return sha256Hex(f.readBytes())
    }

    // 1. isUsableDiffParams checks
    val hashA = "a".repeat(64)
    val hashB = "b".repeat(64)
    val hashMeta = "c".repeat(64)
    expect(
        BundleDiffReconstruction.isUsableDiffParams(
            "https://cdn.example.com/diff.zip",
            hashA,
            "-----BEGIN PGP SIGNED MESSAGE-----",
            false,
        ),
        "usable diff params accepted",
    )
    expect(
        !BundleDiffReconstruction.isUsableDiffParams(
            "https://cdn.example.com/diff.zip",
            hashA,
            "sig",
            true,
        ),
        "fallback-marked version rejected",
    )
    expect(
        !BundleDiffReconstruction.isUsableDiffParams(
            "http://cdn.example.com/diff.zip",
            hashA,
            "sig",
            false,
        ),
        "non-HTTPS diff url rejected",
    )
    expect(
        !BundleDiffReconstruction.isUsableDiffParams(
            "https://cdn.example.com/diff.zip",
            "badhash",
            "sig",
            false,
        ),
        "non-64-hex sha256 rejected",
    )

    // 2. parseAndValidateManifest checks
    val validJson = """
        {
          "schemaVersion": 1,
          "appType": "android",
          "appVersion": "6.8.0",
          "baseAppVersion": "6.8.0",
          "baseMetadataSha256": "$hashA",
          "targetBundleVersion": "20260102",
          "targetMetadataSha256": "${hashMeta.uppercase()}",
          "hashAlgorithm": "sha256",
          "changedFiles": {
            "main.jsbundle.hbc": "$hashA"
          },
          "reusedFiles": {
            "segments/chunk.seg.hbc": "$hashB"
          },
          "deletedFiles": ["segments/old.seg.hbc"]
        }
    """.trimIndent()

    val parsed = BundleDiffReconstruction.parseAndValidateManifest(
        jsonContent = validJson,
        expectedAppType = "android",
        expectedAppVersion = "6.8.0",
        expectedBundleVersion = "20260102",
    )
    expect(parsed.targetMetadataSha256 == hashMeta, "targetMetadataSha256 lowercased")
    expect(parsed.changedFiles["main.jsbundle.hbc"] == hashA, "changedFiles parsed")
    expect(parsed.reusedFiles["segments/chunk.seg.hbc"] == hashB, "reusedFiles parsed")
    expect(parsed.deletedFiles == listOf("segments/old.seg.hbc"), "deletedFiles parsed")

    val wrongAppType = runCatching {
        BundleDiffReconstruction.parseAndValidateManifest(
            jsonContent = validJson.replace("\"android\"", "\"ios\""),
            expectedAppType = "android",
            expectedAppVersion = "6.8.0",
            expectedBundleVersion = "20260102",
        )
    }.exceptionOrNull()
    expect(wrongAppType is BundleDiffReconstructionException.IntegrityFailed, "appType mismatch -> IntegrityFailed")

    val wrongSchema = runCatching {
        BundleDiffReconstruction.parseAndValidateManifest(
            jsonContent = validJson.replace("\"schemaVersion\": 1", "\"schemaVersion\": 2"),
            expectedAppType = "android",
            expectedAppVersion = "6.8.0",
            expectedBundleVersion = "20260102",
        )
    }.exceptionOrNull()
    expect(wrongSchema is BundleDiffReconstructionException.FallbackRequired, "schemaVersion != 1 -> FallbackRequired")

    val traversal = runCatching {
        BundleDiffReconstruction.parseAndValidateManifest(
            jsonContent = validJson.replace("segments/chunk.seg.hbc", "../evil.js"),
            expectedAppType = "android",
            expectedAppVersion = "6.8.0",
            expectedBundleVersion = "20260102",
        )
    }.exceptionOrNull()
    expect(traversal is BundleDiffReconstructionException.SecurityRejected, "path traversal -> SecurityRejected")

    // 3. reconstruct + fallback checks
    val tempRoot = Files.createTempDirectory("bundle-diff-reconstruct").toFile()
    try {
        val staging = File(tempRoot, "staging").apply { mkdirs() }
        val activeOta = File(tempRoot, "activeOta").apply { mkdirs() }
        val olderOta = File(tempRoot, "olderOta").apply { mkdirs() }
        val builtin = File(tempRoot, "builtin").apply { mkdirs() }

        val changedBytes = "new-main".toByteArray()
        val changedHash = sha256Hex(changedBytes)
        File(staging, "main.jsbundle.hbc").writeBytes(changedBytes)

        val segBytes = "reused-segment".toByteArray()
        val segHash = sha256Hex(segBytes)
        File(activeOta, "segments").mkdirs()
        File(olderOta, "segments").mkdirs()
        File(activeOta, "segments/a.seg.hbc").writeText("corrupt")
        File(olderOta, "segments/a.seg.hbc").writeBytes(segBytes)

        val commonBytes = "builtin-common".toByteArray()
        val commonHash = sha256Hex(commonBytes)
        File(builtin, "common.bundle").writeBytes(commonBytes)

        val manifest = BundleDiffManifest(
            schemaVersion = 1,
            appType = "android",
            appVersion = "6.8.0",
            baseAppVersion = "6.8.0",
            baseMetadataSha256 = changedHash,
            targetBundleVersion = "20260102",
            targetMetadataSha256 = changedHash,
            hashAlgorithm = "sha256",
            changedFiles = mapOf("main.jsbundle.hbc" to changedHash),
            reusedFiles = mapOf(
                "segments/a.seg.hbc" to segHash,
                "common.bundle" to commonHash,
            ),
            deletedFiles = emptyList(),
        )

        BundleDiffReconstruction.reconstruct(
            stagingDir = staging,
            manifest = manifest,
            candidateBundleDirs = listOf(activeOta, olderOta),
            sha256OfFile = ::sha256OfFile,
            builtinCopy = { relPath, destFile, expectedHash ->
                val src = File(builtin, relPath)
                if (!src.exists() || sha256OfFile(src.absolutePath) != expectedHash) {
                    false
                } else {
                    src.copyTo(destFile, overwrite = true)
                    true
                }
            },
        )

        expect(
            sha256OfFile(File(staging, "segments/a.seg.hbc").absolutePath) == segHash,
            "reused segment copied from fallback OTA dir",
        )
        expect(
            sha256OfFile(File(staging, "common.bundle").absolutePath) == commonHash,
            "reused common.bundle copied from builtin fallback",
        )

        val missingErr = runCatching {
            BundleDiffReconstruction.reconstruct(
                stagingDir = staging,
                manifest = manifest.copy(reusedFiles = mapOf("missing.bundle" to "f".repeat(64))),
                candidateBundleDirs = listOf(activeOta, olderOta),
                sha256OfFile = ::sha256OfFile,
                builtinCopy = { _, _, _ -> false },
            )
        }.exceptionOrNull()
        expect(missingErr is BundleDiffReconstructionException.FallbackRequired, "missing reused file -> FallbackRequired")
    } finally {
        tempRoot.deleteRecursively()
    }

    println("BundleDiffReconstruction: $assertions assertions passed")
}
