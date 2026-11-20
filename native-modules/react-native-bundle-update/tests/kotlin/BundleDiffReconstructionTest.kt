package com.margelo.nitro.reactnativebundleupdate

import com.margelo.nitro.reactnativebundlecrypto.BundleFileSystem
import com.margelo.nitro.reactnativebundlecrypto.JvmBundleFileSystem
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.security.MessageDigest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

        val oldOta = File(tempRoot, "oldOta").apply { mkdir() }
        File(oldOta, "segments").writeText("old release used a file where the target uses a directory")
        BundleDiffReconstruction.reconstruct(
            stagingDir = staging,
            manifest = manifest,
            candidateBundleDirs = listOf(oldOta, activeOta, olderOta),
            sha256OfFile = ::sha256OfFile,
            fileSystem = JvmBundleFileSystem,
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
                fileSystem = JvmBundleFileSystem,
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
                fileSystem = JvmBundleFileSystem,
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
                fileSystem = JvmBundleFileSystem,
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
            fileSystem = JvmBundleFileSystem,
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

        val archiveLimit = BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES
        expect(archiveLimit == 512L * 1024 * 1024, "OTA archive cap is 512 MiB")
        expect(BundleDiffReconstruction.requireArchiveBytes(archiveLimit - 1, 1) == archiveLimit, "single-stream accepts the exact archive limit")
        expect(
            runCatching { BundleDiffReconstruction.requireArchiveBytes(archiveLimit, 1) }.exceptionOrNull() is
                BundleDiffReconstruction.ArchiveSizeLimitExceeded,
            "single-stream rejects a chunk crossing the archive limit",
        )
        expect(
            runCatching { BundleDiffReconstruction.requireArchiveBytes(archiveLimit, Long.MAX_VALUE) }.exceptionOrNull() is
                BundleDiffReconstruction.ArchiveSizeLimitExceeded,
            "size validation cannot wrap a declared resume length",
        )
        expect(BundleDiffReconstruction.singleStreamRangeTotal("bytes 0-63/64", 0) == 64L, "fresh 206 begins at zero")
        expect(BundleDiffReconstruction.singleStreamRangeTotal("bytes 16-63/64", 16) == 64L, "resumed 206 begins at the requested offset")
        expect(BundleDiffReconstruction.singleStreamRangeTotal("bytes 16-63/64", 0) == null, "fresh 206 rejects a nonzero offset")
        expect(BundleDiffReconstruction.singleStreamRangeTotal("bytes 0-63/64", 16) == null, "resumed 206 rejects a zero offset")
        expect(BundleDiffReconstruction.singleStreamRangeTotal(null, 0) == null, "206 must provide a Content-Range")
        expect(BundleDiffReconstruction.singleStreamRangeTotal("bytes 0-63/64 junk", 0) == null, "206 rejects an invalid Content-Range suffix")
        expect(BundleDiffReconstruction.singleStreamRangeTotal("bytes 0-64/64", 0) == null, "206 rejects an end outside the advertised archive")

        val target = File(tempRoot, "diff-verification-target")
        val targetMetadata = File(target, "metadata.json")
        fun metadataSize() = BundleDiffReconstruction.targetMetadataSizeIfPresent(target, targetMetadata, JvmBundleFileSystem)
        expect(metadataSize() == null, "absent target uses archive verification")
        target.writeText("not a directory")
        expect(
            runCatching { metadataSize() }.exceptionOrNull() is BundleDiffReconstructionException.IntegrityFailed,
            "target occupied by a file fails integrity verification",
        )
        check(target.delete() && target.mkdir())
        expect(
            runCatching { metadataSize() }.exceptionOrNull() is BundleDiffReconstructionException.IntegrityFailed,
            "existing target without metadata fails integrity verification",
        )
        check(targetMetadata.mkdir())
        expect(
            runCatching { metadataSize() }.exceptionOrNull() is BundleDiffReconstructionException.IntegrityFailed,
            "metadata directory fails integrity verification",
        )
        check(targetMetadata.delete())
        targetMetadata.writeText("{}")
        expect(metadataSize() == 2L, "existing target with regular metadata uses tree verification")

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
        BundleDiffReconstruction.writeDescriptor(desc, metaFile, JvmBundleFileSystem)
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

        expect(
            BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(fullSha, diffSha, null) == null,
            "an explicit diff digest cannot replace the download record",
        )
        expect(
            BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(fullSha, wrongSha, loadedDesc) == null,
            "an explicit diff digest must match the download record",
        )
        expect(
            BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                suppliedFullSha256 = wrongSha,
                suppliedSignature = "VALID_PGP_SIGNATURE",
                inMemoryVerified = null,
                descriptor = null,
                persistedAscSignature = null,
                verifySignatureAndExtractMetadataSha256 = { metaSha },
            ) == null,
            "a valid version-bound signature cannot establish the full archive digest",
        )
        val liveRecord = BundleDiffReconstruction.VerifiedDiffMetadata(fullSha, diffSha, metaSha)
        expect(liveRecord.matches(fullSha, diffSha), "verified record matches both digests")
        expect(!liveRecord.matches(fullSha, wrongSha), "verified record rejects a different diff digest")

        val signedDesc = desc.copy(signature = "VALID_PGP_SIGNATURE", fallbackRequired = true)
        val failedRename = object : BundleFileSystem by JvmBundleFileSystem {
            override fun rename(source: File, destination: File) {
                expect(source.parentFile == destination.parentFile, "descriptor temp is on the destination filesystem")
                expect(source.readText().contains("\"fallbackRequired\":true"), "renamed file contains the complete fallback descriptor")
                throw IOException("simulated rename failure")
            }
        }
        expect(
            !BundleDiffReconstruction.writeDescriptor(signedDesc, metaFile, failedRename),
            "descriptor rename failure is reported",
        )
        expect(
            metaFile.parentFile.listFiles().orEmpty().none { it.name.startsWith("diff-descriptor-") },
            "descriptor rename failure removes its temporary file",
        )
        expect(
            BundleDiffReconstruction.readDescriptor(metaFile, "6.8.0", "20260103") == loadedDesc,
            "failed fallback descriptor write preserves the previous download record",
        )
        expect(BundleDiffReconstruction.writeDescriptor(signedDesc, metaFile, JvmBundleFileSystem), "atomically replace an existing descriptor")
        val reloadedSigned = BundleDiffReconstruction.readDescriptor(metaFile, "6.8.0", "20260103")
        expect(reloadedSigned?.signature == "VALID_PGP_SIGNATURE" && reloadedSigned.fallbackRequired, "signed descriptor with fallbackRequired persisted")

        val diffDownloads = File(tempRoot, ".diff").apply { mkdir() }
        val staleDesc = desc.copy(appVersion = "6.7.0-beta", bundleVersion = "b".repeat(220))
        val staleTarget = File(diffDownloads, "${staleDesc.appVersion}-${staleDesc.bundleVersion}${BundleDiffReconstruction.DIFF_META_EXTENSION}")
        expect(BundleDiffReconstruction.writeDescriptor(staleDesc, staleTarget, JvmBundleFileSystem), "write a long versioned descriptor")
        val currentTarget = File(diffDownloads, "6.8.0-rc-2026-01-03.diff.zip.meta.json").apply { createNewFile() }
        val abandonedTemp = File(diffDownloads, "diff-descriptor-1234.tmp").apply { createNewFile() }
        val legacyTemp = File(diffDownloads, "${currentTarget.name}.tmp").apply { createNewFile() }
        fun prune(entry: File) = BundleDiffReconstruction.pruneDiffDownload(entry, JvmBundleFileSystem) {
            it.startsWith("6.8.0-rc-")
        }

        val readyToRename = CountDownLatch(1)
        val resumeRename = CountDownLatch(1)
        lateinit var activeTemp: File
        val pausedRename = object : BundleFileSystem by JvmBundleFileSystem {
            override fun rename(source: File, destination: File) {
                activeTemp = source
                readyToRename.countDown()
                check(resumeRename.await(5, TimeUnit.SECONDS)) { "Timed out waiting for prune" }
                JvmBundleFileSystem.rename(source, destination)
                prune(destination)
                check(destination.exists()) { "Prune removed the published descriptor before the writer exited" }
            }
        }
        val worker = Executors.newSingleThreadExecutor()
        try {
            val updated = staleDesc.copy(signature = "VALID_PGP_SIGNATURE", fallbackRequired = true)
            val writing = worker.submit<Boolean> { BundleDiffReconstruction.writeDescriptor(updated, staleTarget, pausedRename) }
            expect(readyToRename.await(5, TimeUnit.SECONDS), "writer reaches rename before prune")
            val original = staleTarget.readText()
            diffDownloads.listFiles().orEmpty().forEach(::prune)
            expect(activeTemp.parentFile == diffDownloads.canonicalFile, "active temp remains on the destination filesystem")
            expect(activeTemp.isFile && activeTemp.name.length < 64, "prune protects the bounded temporary name")
            expect(staleTarget.readText() == original, "prune protects the stale-version destination during replacement")
            expect(currentTarget.isFile, "prune preserves a completed descriptor under the exact current app-version prefix")
            expect(!abandonedTemp.exists(), "prune reclaims an abandoned random temporary descriptor")
            expect(!legacyTemp.exists(), "prune reclaims an abandoned legacy temporary descriptor")
            resumeRename.countDown()
            expect(writing.get(5, TimeUnit.SECONDS), "the descriptor write publishes successfully after concurrent prune")
            val written = BundleDiffReconstruction.readDescriptor(staleTarget, updated.appVersion, updated.bundleVersion)
            expect(written == updated.copy(diffSha256 = diffSha), "published descriptor retains its exact version fields and updated state")
            expect(!activeTemp.exists(), "successful publication leaves no temporary file")
            prune(staleTarget)
            expect(!staleTarget.exists(), "prune can remove the stale descriptor after the writer exits")
        } finally {
            resumeRename.countDown()
            worker.shutdownNow()
        }

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
                descriptor = mergedDesc.copy(fallbackRequired = false),
                persistedAscSignature = null,
                verifySignatureAndExtractMetadataSha256 = { sig -> if (sig == "VALID_PGP_SIGNATURE") metaSha else null },
            ) == metaSha,
            "post-restart verifyBundle re-establishes trust from preserved signature",
        )
        expect(
            BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(fullSha, diffSha, reloadedSigned) == null,
            "a full-fallback record cannot authorize a diff reconstruction",
        )
        expect(
            BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                suppliedFullSha256 = wrongSha,
                suppliedSignature = null,
                inMemoryVerified = liveRecord,
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

        val diffTask42 = BundleDiffReconstruction.rangeTaskId("6.8.0-42", isDiff = true)
        val fullTask42Diff = BundleDiffReconstruction.rangeTaskId("6.8.0-42.diff", isDiff = false)
        expect(diffTask42 == "diff:6.8.0-42", "diff rangeTaskId prefix")
        expect(fullTask42Diff == "full:6.8.0-42.diff", "full rangeTaskId prefix")
        expect(diffTask42 != fullTask42Diff, "diff task 42 and full task 42.diff do not collide")
        expect(
            BundleDiffReconstruction.rangeTaskIdForFilePath("/tmp/onekey-bundle-download/.diff/6.8.0-42.diff.zip") == diffTask42,
            "rangeTaskIdForFilePath resolves diff task id",
        )
        expect(
            BundleDiffReconstruction.rangeTaskIdForFilePath("/tmp/onekey-bundle-download/6.8.0-42.diff.zip") == fullTask42Diff,
            "rangeTaskIdForFilePath resolves full task id",
        )

        expect(
            BundleDiffReconstruction.isRetryableNetworkIOException(java.io.EOFException("unexpected end of stream")),
            "EOFException classified as retryable network error",
        )
        expect(
            !BundleDiffReconstruction.isRetryableNetworkIOException(java.io.FileNotFoundException("missing")),
            "FileNotFoundException is not classified as network error",
        )

        expect(
            !BundleDiffReconstruction.isRetryableNetworkIOException(java.io.IOException("stream was reset: CANCEL")),
            "generic IOException remains unclassified even when its prose resembles a stream reset",
        )

        val stagingRoot = File(tempRoot, BundleDiffReconstruction.STAGING_ROOT_DIR_NAME).apply { mkdir() }
        val abandoned = File(stagingRoot, "6.8.0-42-abandoned").apply { mkdir() }
        File(abandoned, "partial").writeText("data")
        val outsideStaging = File(tempRoot, "outside-staging").apply { mkdir() }
        val outsideFile = File(outsideStaging, "keep").apply { writeText("kept") }
        val abandonedLink = File(abandoned, "external")
        Files.createSymbolicLink(abandonedLink.toPath(), outsideStaging.toPath())
        val live = File(stagingRoot, "6.8.0-43-live").apply { mkdir() }
        val liveFile = File(live, "partial").apply { writeText("data") }
        BundleDiffReconstruction.retainStagingDirectory(live)
        try {
            BundleDiffReconstruction.sweepStagingDirectory(stagingRoot, JvmBundleFileSystem)
            expect(!abandoned.exists(), "sweep reclaims abandoned current-version staging")
            expect(outsideFile.readText() == "kept", "sweep removes an abandoned symlink without following it")
            expect(liveFile.exists(), "sweep preserves a registered live staging tree")
        } finally {
            BundleDiffReconstruction.releaseStagingDirectory(live, JvmBundleFileSystem)
        }
        expect(!live.exists(), "settled staging is removed")

        val zipWithRootEntry = File(tempRoot, "with-root-entry.zip")
        java.util.zip.ZipOutputStream(zipWithRootEntry.outputStream()).use { output ->
            output.putNextEntry(java.util.zip.ZipEntry("./"))
            output.closeEntry()
            output.putNextEntry(java.util.zip.ZipEntry("payload"))
            output.write("content".toByteArray())
            output.closeEntry()
        }
        val rootEntryOutput = File(tempRoot, "with-root-entry")
        BundleDiffReconstruction.unzipFile(zipWithRootEntry.path, rootEntryOutput.path, JvmBundleFileSystem)
        expect(File(rootEntryOutput, "payload").readText() == "content", "extraction accepts a ZIP root directory entry")

        val symlinkOutput = File(tempRoot, "with-symlink").apply { mkdir() }
        val missingTarget = File(symlinkOutput, "missing")
        Files.createSymbolicLink(File(symlinkOutput, "payload").toPath(), missingTarget.toPath())
        val symlinkError = runCatching {
            BundleDiffReconstruction.unzipFile(zipWithRootEntry.path, symlinkOutput.path, JvmBundleFileSystem)
        }.exceptionOrNull()
        expect(symlinkError is SecurityException && !missingTarget.exists(), "extraction rejects dangling symlink entries")

        val zip = File(tempRoot, "extraction-budget.zip")
        java.util.zip.ZipOutputStream(zip.outputStream()).use { output ->
            for (name in listOf("a", "b")) {
                output.putNextEntry(java.util.zip.ZipEntry(name))
                output.write(ByteArray(8))
                output.closeEntry()
            }
        }
        BundleDiffReconstruction.unzipFile(zip.path, File(tempRoot, "at-budget").path, JvmBundleFileSystem, maxUncompressedBytes = 16)
        expect(File(tempRoot, "at-budget/b").length() == 8L, "extraction accepts the exact cumulative byte limit")
        val overBudget = runCatching {
            BundleDiffReconstruction.unzipFile(zip.path, File(tempRoot, "over-budget").path, JvmBundleFileSystem, maxUncompressedBytes = 15)
        }
        expect(overBudget.exceptionOrNull() is SecurityException, "extraction byte limit raises a terminal security error")
        val overEntryLimit = runCatching {
            BundleDiffReconstruction.unzipFile(zip.path, File(tempRoot, "over-entries").path, JvmBundleFileSystem, maxEntries = 1)
        }
        expect(overEntryLimit.exceptionOrNull() is SecurityException, "extraction entry limit raises a terminal security error")
    } finally {
        tempRoot.deleteRecursively()
    }

    println("BundleDiffReconstruction: $assertions assertions passed")
}
