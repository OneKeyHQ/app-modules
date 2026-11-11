package com.margelo.nitro.reactnativebundleupdate

import java.io.File
import java.nio.file.Files

// Dependency-free JVM tests compile the same path helpers shipped on Android.
fun main() {
    var assertions = 0
    fun expect(actual: Boolean, expected: Boolean, scenario: String) {
        check(actual == expected) { "$scenario: expected $expected, got $actual" }
        assertions += 1
    }

    val tempRoot = Files.createTempDirectory("bundle-verification-path").toFile()
    try {
        val folderName = "6.7.0-27053504"
        val bundleDir = File(tempRoot, folderName)
        val segmentDir = File(bundleDir, "segments-background")
        segmentDir.mkdirs()
        val segment = File(segmentDir, "core.chains.all.CoreChainImported.seg.hbc")
        segment.writeText("segment")
        val entryBundle = File(bundleDir, "main.jsbundle.hbc")
        entryBundle.writeText("main")

        // Metadata keys are bundle-relative and use "/" separators.
        expect(
            bundleRelativePath(bundleDir, segment) ==
                "segments-background/core.chains.all.CoreChainImported.seg.hbc",
            true,
            "nested file key",
        )
        expect(bundleRelativePath(bundleDir, entryBundle) == "main.jsbundle.hbc", true, "top-level file key")

        // Callers may name the root with a trailing separator.
        expect(
            bundleRelativePath(File(bundleDir.path + File.separator), segment) ==
                "segments-background/core.chains.all.CoreChainImported.seg.hbc",
            true,
            "trailing separator on root",
        )

        // A sibling whose name shares the root's name as a prefix is not inside it.
        val siblingDir = File(tempRoot, "$folderName-old")
        siblingDir.mkdirs()
        val siblingFile = File(siblingDir, "main.jsbundle.hbc")
        siblingFile.writeText("sibling")
        expect(bundleRelativePath(bundleDir, siblingFile) == null, true, "prefix-sharing sibling rejected")

        // Files outside the walk root, and the root itself, are rejected instead
        // of being looked up by an absolute path.
        val outside = File(tempRoot, "outside.hbc")
        outside.writeText("outside")
        expect(bundleRelativePath(bundleDir, outside) == null, true, "outside file rejected")
        expect(bundleRelativePath(bundleDir, bundleDir) == null, true, "root directory rejected")

        // Same-directory identity is form agnostic: a different path form for the
        // same tree compares equal, a different directory does not.
        expect(
            isSameDirectory(File(bundleDir.path + File.separator), bundleDir),
            true,
            "same dir with trailing separator",
        )
        expect(isSameDirectory(bundleDir, siblingDir) == false, true, "different dirs are not the same")
        expect(isSameDirectory(bundleDir, File(tempRoot, "missing")) == false, true, "missing dir is not the same")

        val link = File(tempRoot, "bundle-link")
        val linked = runCatching { Files.createSymbolicLink(link.toPath(), bundleDir.toPath()) }.isSuccess
        if (linked) {
            expect(isSameDirectory(link, bundleDir), true, "symlinked root resolves to the same dir")
            expect(
                bundleRelativePath(
                    link,
                    File(link, "segments-background/core.chains.all.CoreChainImported.seg.hbc"),
                ) == "segments-background/core.chains.all.CoreChainImported.seg.hbc",
                true,
                "symlinked root keeps bundle-relative keys",
            )
        } else {
            println("BundleVerificationPath: symlink scenarios skipped (symlinks unavailable)")
        }
    } finally {
        tempRoot.deleteRecursively()
    }

    println("BundleVerificationPath: $assertions assertions passed")
}
