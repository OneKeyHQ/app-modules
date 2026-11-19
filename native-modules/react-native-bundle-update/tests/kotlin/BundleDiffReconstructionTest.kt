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

        // 4. validateManifestAgainstTargetMetadata + full 8-category Android reconstruction
        val targetFileEntries = mapOf(
            "main.jsbundle.hbc" to changedHash,
            "segments/a.seg.hbc" to segHash,
            "common.bundle" to commonHash,
        )
        BundleDiffReconstruction.validateManifestAgainstTargetMetadata(manifest, targetFileEntries)
        assertions += 1

        val omittedErr = runCatching {
            BundleDiffReconstruction.validateManifestAgainstTargetMetadata(
                manifest,
                mapOf("main.jsbundle.hbc" to changedHash),
            )
        }.exceptionOrNull()
        expect(omittedErr is BundleDiffReconstructionException.FallbackRequired, "partition count mismatch rejected")

        val hashMismatchErr = runCatching {
            BundleDiffReconstruction.validateManifestAgainstTargetMetadata(
                manifest,
                targetFileEntries + ("common.bundle" to "e".repeat(64)),
            )
        }.exceptionOrNull()
        expect(hashMismatchErr is BundleDiffReconstructionException.FallbackRequired, "partition hash mismatch rejected")

        // 5. Verify special Android built-in path mappings (main.jsbundle.hbc, assets/drawable-*, assets/raw/*, web-embed/*)
        expect(
            BundleDiffReconstruction.builtinCandidateAssetPaths("main.jsbundle.hbc") ==
                listOf("main.jsbundle.hbc", "index.android.bundle"),
            "Android main.jsbundle.hbc maps to index.android.bundle",
        )
        expect(
            BundleDiffReconstruction.builtinCandidateAssetPaths("assets/drawable-xhdpi/icon.png") ==
                listOf("assets/drawable-xhdpi/icon.png", "drawable-xhdpi/icon.png", "assets/assets/drawable-xhdpi/icon.png"),
            "Android assets/drawable-xhdpi candidate asset paths",
        )
        expect(
            BundleDiffReconstruction.builtinCandidateApkResZipEntries("assets/drawable-xhdpi/icon.png") ==
                listOf("res/drawable-xhdpi/icon.png", "res/drawable-xhdpi-v4/icon.png"),
            "Android APK res zip entries for drawable",
        )
        expect(
            BundleDiffReconstruction.builtinCandidateApkResZipEntries("assets/raw/sound.mp3") ==
                listOf("res/raw/sound.mp3", "res/raw-v4/sound.mp3"),
            "Android APK res zip entries for raw",
        )
        expect(
            BundleDiffReconstruction.builtinCandidateAssetPaths("web-embed/index.html") ==
                listOf("web-embed/index.html"),
            "Android web-embed stays at web-embed/index.html",
        )

        // 6. Version sanitization rejects leading/trailing dots (isolating .diff and .diff-staging)
        // while preserving compatibility with legacy installed version names such as "42-diff"
        expect(BundleDiffReconstruction.isSafeVersionComponent("6.8.0"), "valid appVersion accepted")
        expect(BundleDiffReconstruction.isSafeVersionComponent("42-diff"), "legacy 42-diff version accepted")
        expect(BundleDiffReconstruction.isSafeVersionComponent("123.diff-staging"), "legacy 123.diff-staging accepted")
        expect(!BundleDiffReconstruction.isSafeVersionComponent(".diff-staging"), "leading dot rejected")
        expect(!BundleDiffReconstruction.isSafeVersionComponent(".diff"), "leading dot .diff rejected")
        expect(!BundleDiffReconstruction.isSafeVersionComponent("123."), "trailing dot rejected")

        // 7. Uppercase sha256 normalization and retryable diff error classification
        val upperHex = "A1B2C3D4".repeat(8)
        expect(BundleDiffReconstruction.normalizeSha256(upperHex) == upperHex.lowercase(), "uppercase sha256 normalized to lowercase")
        expect(BundleDiffReconstruction.normalizeSha256("invalid") == null, "invalid sha256 returns null")
        expect(BundleDiffReconstruction.isRetryableDiffDownloadError("NETWORK_ERROR"), "NETWORK_ERROR is retryable")
        expect(BundleDiffReconstruction.isRetryableDiffDownloadError("TIMEOUT"), "TIMEOUT is retryable")
        expect(!BundleDiffReconstruction.isRetryableDiffDownloadError("HTTP_ERROR"), "HTTP_ERROR is not retryable")

        // 8. Cumulative entry count and byte limits across changedFiles + reusedFiles
        val entryLimitErr = runCatching {
            BundleDiffReconstruction.reconstruct(
                stagingDir = staging,
                manifest = manifest,
                candidateBundleDirs = listOf(activeOta, olderOta),
                sha256OfFile = ::sha256OfFile,
                builtinCopy = { _, _, _ -> false },
                maxEntries = 2,
                maxTotalBytes = 1024L * 1024L,
            )
        }.exceptionOrNull()
        expect(
            entryLimitErr is BundleDiffReconstructionException.SecurityRejected,
            "entry count limit exceeded -> SecurityRejected",
        )

        val byteLimitErr = runCatching {
            BundleDiffReconstruction.reconstruct(
                stagingDir = staging,
                manifest = manifest,
                candidateBundleDirs = listOf(activeOta, olderOta),
                sha256OfFile = ::sha256OfFile,
                builtinCopy = { _, _, _ -> false },
                maxEntries = 10,
                maxTotalBytes = 15L,
            )
        }.exceptionOrNull()
        expect(
            byteLimitErr is BundleDiffReconstructionException.SecurityRejected,
            "cumulative byte limit exceeded -> SecurityRejected",
        )

        // Unmatched oversized reuse candidate in first dir does not block smaller matching candidate in second dir
        File(activeOta, "segments/a.seg.hbc").writeBytes(ByteArray(128) { 0x41 })
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
            maxEntries = 10,
            maxTotalBytes = 50L,
        )
        expect(
            sha256OfFile(File(staging, "segments/a.seg.hbc").absolutePath) == segHash,
            "unmatched oversized reuse candidate skipped in favor of smaller matching candidate",
        )

        // Split APK resource resolution (base APK + splitSourceDirs)
        val baseApk = File(tempRoot, "base.apk")
        val splitXhdpiApk = File(tempRoot, "split_config.xhdpi.apk")
        val iconBytes = "split-xhdpi-icon-bytes".toByteArray()
        val iconHash = sha256Hex(iconBytes)
        java.util.zip.ZipOutputStream(baseApk.outputStream()).use { zos ->
            zos.putNextEntry(java.util.zip.ZipEntry("AndroidManifest.xml"))
            zos.write("manifest".toByteArray())
            zos.closeEntry()
        }
        java.util.zip.ZipOutputStream(splitXhdpiApk.outputStream()).use { zos ->
            zos.putNextEntry(java.util.zip.ZipEntry("res/drawable-xhdpi-v4/icon.png"))
            zos.write(iconBytes)
            zos.closeEntry()
        }
        val apkPaths = BundleDiffReconstruction.collectInstalledApkPaths(
            sourceDir = baseApk.absolutePath,
            splitSourceDirs = arrayOf(splitXhdpiApk.absolutePath),
        )
        expect(apkPaths.size == 2, "collectInstalledApkPaths includes base and split APKs")
        val copiedIconDest = File(tempRoot, "copied-icon.png")
        val copiedFromSplit = BundleDiffReconstruction.copyMatchingZipEntryFromApks(
            apkPaths = apkPaths,
            candidateEntryNames = BundleDiffReconstruction.builtinCandidateApkResZipEntries("assets/drawable-xhdpi/icon.png"),
            destinationFile = copiedIconDest,
            expectedHash = iconHash,
            sha256OfFile = ::sha256OfFile,
        )
        expect(copiedFromSplit && sha256OfFile(copiedIconDest.absolutePath) == iconHash, "copied drawable resource from split configuration APK")

        // 9. Descriptor persistence, signature preservation on cache hit, pre-ASC verifyBundle, post-restart fail-closed verifyBundle, and fallback resolution
        val fullSha = "1".repeat(64)
        val diffSha = "2".repeat(64)
        val metaSha = "3".repeat(64)
        val wrongSha = "9".repeat(64)
        val metaFile = File(tempRoot, "6.8.0-20260103.diff.zip.meta.json")
        val desc = BundleDiffReconstruction.DiffArchiveDescriptor(
            appVersion = "6.8.0",
            bundleVersion = "20260103",
            downloadUrl = "https://cdn.onekey.so/full.zip",
            fileSize = 4096.0,
            sha256 = fullSha,
            diffSha256 = diffSha.uppercase(),
            signature = null,
        )
        BundleDiffReconstruction.writeDescriptor(desc, metaFile)
        val loadedDesc = BundleDiffReconstruction.readDescriptor(metaFile, "6.8.0", "20260103")
        expect(loadedDesc != null && loadedDesc.diffSha256 == diffSha && !loadedDesc.fallbackRequired, "descriptor persisted and normalized")

        expect(
            BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(
                suppliedFullSha256 = fullSha.uppercase(),
                suppliedDiffSha256 = null,
                descriptor = loadedDesc,
            ) == diffSha,
            "pre-ASC verifyBundle resolves diffSha256 from descriptor",
        )
        expect(
            BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(
                suppliedFullSha256 = wrongSha,
                suppliedDiffSha256 = null,
                descriptor = loadedDesc,
            ) == null,
            "pre-ASC verifyBundle rejects mismatched fullSha256",
        )
        expect(
            BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                suppliedFullSha256 = fullSha,
                suppliedSignature = null,
                inMemoryVerified = null,
                descriptor = loadedDesc,
                persistedAscSignature = null,
                verifySignatureAndExtractMetadataSha256 = { metaSha },
            ) == null,
            "post-restart verifyBundle fails closed without verified signature",
        )

        val signedDesc = desc.copy(signature = "VALID_PGP_SIGNATURE", fallbackRequired = true)
        BundleDiffReconstruction.writeDescriptor(signedDesc, metaFile)
        val reloadedSigned = BundleDiffReconstruction.readDescriptor(metaFile, "6.8.0", "20260103")
        expect(reloadedSigned?.signature == "VALID_PGP_SIGNATURE" && reloadedSigned.fallbackRequired, "signed descriptor with fallbackRequired persisted")

        val mergedDesc = BundleDiffReconstruction.descriptorPreservingVerifiedSignature(
            newDescriptor = desc,
            existingDescriptor = reloadedSigned,
        )
        expect(mergedDesc.signature == "VALID_PGP_SIGNATURE" && mergedDesc.fallbackRequired, "cached download preserves verified signature and fallbackRequired")

        expect(
            BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                suppliedFullSha256 = fullSha,
                suppliedSignature = null,
                inMemoryVerified = null,
                descriptor = mergedDesc,
                persistedAscSignature = null,
                verifySignatureAndExtractMetadataSha256 = { sig -> if (sig == "VALID_PGP_SIGNATURE") metaSha else null },
            ) == metaSha,
            "post-restart verifyBundle re-establishes trust from preserved signature",
        )
        expect(
            BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                suppliedFullSha256 = wrongSha,
                suppliedSignature = null,
                inMemoryVerified = fullSha to metaSha,
                descriptor = signedDesc,
                persistedAscSignature = null,
                verifySignatureAndExtractMetadataSha256 = { metaSha },
            ) == null,
            "verifyBundle rejects mismatched suppliedFullSha256",
        )

        val fallback = BundleDiffReconstruction.resolveFallbackDownloadParams(
            expectedFullSha256 = fullSha,
            expectedDiffSha256 = null,
            explicitDownloadUrl = null,
            explicitFileSize = null,
            inMemoryFullParams = null,
            descriptor = reloadedSigned,
        )
        expect(
            fallback == ("https://cdn.onekey.so/full.zip" to 4096.0),
            "fallback download params recovered from persisted descriptor on verifyBundleASC retry",
        )
    } finally {
        tempRoot.deleteRecursively()
    }

    println("BundleDiffReconstruction: $assertions assertions passed")
}
