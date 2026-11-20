package com.margelo.nitro.reactnativebundlecrypto

import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BundleCryptoCoreTest {
  @get:Rule val temporaryFolder = TemporaryFolder()

  private val expectedSignedHash = "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba"

  // Existing release-key test vector, also used by BundleUpdate.testVerification.
  private val signedMetadata = """-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA256

{
  "fileName": "metadata.json",
  "sha256": "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba",
  "size": 158590,
  "generatedAt": "2025-09-19T07:49:13.000Z"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmjNJ1IOHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHs6Rw/9FKHl5aNsE7V0IsFf/l+h16BYKFwVsL69alMk
CFLna8oUn0+tyECF6wKBKw5pHo5YR27o2pJfYbAER6dygDF6WTZ1lZdf5QcBMjGA
LCeXC0hzUBzSSOH4bKBTa3fHp//HdSV1F2OnkymbXqYN7WXvuQPLZ0nV6aU88hCk
HgFifcvkXAnWKoosUtj0Bban/YBRyvmQ5C2akxUPEkr4Yck1QXwzJeNRd7wMXHjH
JFK6lJcuABiB8wpJDXJkFzKs29pvHIK2B2vdOjU2rQzKOUwaKHofDi5C4+JitT2b
2pSeYP3PAxXYw6XDOmKTOiC7fPnfLjtcPjNYNFCezVKZT6LKvZW9obnW8Q9LNJ4W
okMPgHObkabv3OqUaTA9QNVfI/X9nvggzlPnaKDUrDWTf7n3vlrdexugkLtV/tJA
uguPlI5hY7Ue5OW7ckWP46hfmq1+UaIdeUY7dEO+rPZDz6KcArpaRwBiLPBhneIr
/X3KuMzS272YbPbavgCZGN9xJR5kZsEQE5HhPCbr6Nf0qDnh+X8mg0tAB/U6F+ZE
o90sJL1ssIaYvST+VWVaGRr4V5nMDcgHzWSF9Q/wm22zxe4alDaBdvOlUseW0iaM
n2DMz6gqk326W6SFynYtvuiXo7wG4Cmn3SuIU8xfv9rJqunpZGYchMd7nZektmEJ
91Js0rQ=
=A/Ii
-----END PGP SIGNATURE-----"""

  @Test
  fun signedMetadataAcceptsReleaseKeyAndRejectsChangedBodyAndUnsignedInput() {
    val verified = BundleCryptoCore.verifyGpgCleartext(signedMetadata)
    assertTrue(verified.valid)
    assertEquals(expectedSignedHash, verified.sha256)
    assertNull(verified.reason)

    val missingBinding = BundleCryptoCore.verifyGpgCleartext(
      signedMetadata, expectedAppVersion = "5.12.0"
    )
    assertFalse(missingBinding.valid)
    assertNull(missingBinding.sha256)
    assertEquals("METADATA_APPVERSION_MISMATCH", missingBinding.reason)
    assertEquals(
      "METADATA_APPTYPE_MISMATCH",
      BundleCryptoCore.verifyGpgCleartext(signedMetadata, expectedAppType = "mobile").reason
    )
    assertEquals(
      "METADATA_BUNDLEVERSION_MISMATCH",
      BundleCryptoCore.verifyGpgCleartext(signedMetadata, expectedBundleVersion = "20250919").reason
    )

    val tampered = signedMetadata.replace("158590", "158591")
    val rejected = BundleCryptoCore.verifyGpgCleartext(tampered)
    assertFalse(rejected.valid)
    assertNull(rejected.sha256)
    assertEquals("SIGNATURE_INVALID", rejected.reason)

    val unsigned = BundleCryptoCore.verifyGpgCleartext("{\"sha256\":\"$expectedSignedHash\"}")
    assertFalse(unsigned.valid)
    assertNull(unsigned.sha256)
    assertEquals("NOT_PGP_SIGNED_MESSAGE", unsigned.reason)
  }

  @Test
  fun expectedFileNameValidationForDiffManifest() {
    val rejectedMismatch = BundleCryptoCore.verifyGpgCleartext(
      signedMetadata,
      expectedFileName = "diff-manifest.json",
    )
    assertFalse(rejectedMismatch.valid)
    assertNull(rejectedMismatch.sha256)
    assertEquals("INVALID_METADATA_FILENAME", rejectedMismatch.reason)

    val testPublicKey = """-----BEGIN PGP PUBLIC KEY BLOCK-----

mQENBGr9TcEBCADqhPZEGynHy8F6Zs0n/ayUQZgTTocdnZupAbatptQDf4ajCYNk
AmE44GUZhaoUaRepBDlrEr4aEjkcfL1Zx9iGoRu1jbiEBSJnZAdhN8wMQZsG7usG
ydg/H9XparEPyHq6aqYu5Eq5OIJ99Vbe5sY0fEYVnBJHQSRpuH09B/RZYj+Up3aj
Vs84InZGAlpRABsjW6cKDGxI4qqxd6lcuPzzqKZBp0NqeEzIE/I24GbGA4BJTd8S
9gaJXB9Totuxy/uv+4+NLcAG4BDrQz0H0KXMrU9UmSUl4ZWp/iA13aHoZdoEsdv2
aA8HQ3yb5qHElwEAvRapspzYL8MFZm2ak3NHABEBAAG0HlRlc3QgU2lnbmVyIDx0
ZXN0QGV4YW1wbGUuY29tPokBUgQTAQgAPBYhBJtb/2ZyaVZpVQloADBHSIn7JD1z
BQJq/U3BAxsvBAULCQgHAgIiAgYVCgkICwIEFgIDAQIeBwIXgAAKCRAwR0iJ+yQ9
c2xACADEewfGtSOEtSkNCx1r23tfx2iBmAPy5sd0cuIszgg4qlbF01pU9OXJM9/d
slQd+DGFbE2fKSnO0Zu1K84pRULcDCKETf0uXEYYH/K+t4k4PSFnyq3bA5DOHX7w
xPq8jV/4CNcKwpQ40mhmv85q46mJO4ll/NQEN4ZFQliJswyZodfpjzVKz695usOp
W0FD+Sgy2x6iauNrxBtm7ILNWWFCRW9J7+Y7Uc/RMNRpj9BuHk/xkh2QQt9twd95
r2i2Fzn8IuFa2budqTzwea87p9VQdYnzmthHGt6Xzety4frFUBThwix85VIpoROj
DCy0Hhtw6dfpxwWXGeFERjK0xCbs
=ITXe
-----END PGP PUBLIC KEY BLOCK-----"""

    val signedDiffManifest = """-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA256

{
  "fileName": "diff-manifest.json",
  "sha256": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
  "appType": "ios",
  "appVersion": "6.8.0",
  "bundleVersion": "20260101"
}
-----BEGIN PGP SIGNATURE-----

iQEzBAEBCAAdFiEEm1v/ZnJpVmlVCWgAMEdIifskPXMFAmr9TcIACgkQMEdIifsk
PXML3Af/VKdIMeq/vP6i5dfv8SpjQcDwuNlYuQD9pFGqXtG2AADSmufv/9ojc3LZ
QvGzd8Q9EVl+l1AfKSLTV/3kgsVUsEQP+iJCQVXN4OCZvrfEQiXX9VxMM2Rkevmu
XaIoQlURsGOBuLyTZS4ECm/iFUaEp8s0ajCKUNAHcKiXBzwgqYThGF+FM3ahbIb0
5u5IvfVdRnxHBPf9Nd+lH6EFnzIvKHuDMyIR7J9PAfuMnt0AZ1dZ8bHpj6A5yDIu
2/vYjpGvHnqMzobhznsEEty5yLxP57SatrF0FXgRe6hM41MNQlLDKY1QiHn9Sqsg
XHxbRRW2slQi8HEQtq1BY3MPmO0G9Q==
=vF1w
-----END PGP SIGNATURE-----"""

    val acceptedDiff = BundleCryptoCore.verifyGpgCleartextWithKey(
      signedDiffManifest,
      expectedAppVersion = "6.8.0",
      expectedBundleVersion = "20260101",
      expectedAppType = "ios",
      expectedFileName = "diff-manifest.json",
      publicKeyArmored = testPublicKey,
    )
    assertTrue(acceptedDiff.valid)
    assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", acceptedDiff.sha256)
    assertNull(acceptedDiff.reason)

    val rejectedDefault = BundleCryptoCore.verifyGpgCleartextWithKey(
      signedDiffManifest,
      expectedAppVersion = "6.8.0",
      expectedBundleVersion = "20260101",
      expectedAppType = "ios",
      publicKeyArmored = testPublicKey,
    )
    assertFalse(rejectedDefault.valid)
    assertNull(rejectedDefault.sha256)
    assertEquals("INVALID_METADATA_FILENAME", rejectedDefault.reason)
  }

  @Test
  fun validSignatureWithWrongAscBodyIsRejectedAfterVerification() {
    val result = BundleCryptoCore.verifyDetachedAsc(signedMetadata, "OneKey.apk")
    assertFalse(result.valid)
    assertNull(result.sha256)
    assertEquals("SHA256_TOKEN_INVALID", result.reason)

    assertEquals(expectedSignedHash, BundleCryptoCore.extractDetachedAscSha256(expectedSignedHash, null))
    assertEquals("", BundleCryptoCore.extractDetachedAscSha256(expectedSignedHash, "OneKey.apk"))
    assertEquals(
      expectedSignedHash,
      BundleCryptoCore.extractDetachedAscSha256("$expectedSignedHash  *OneKey.apk", "OneKey.apk")
    )
    assertEquals(
      "",
      BundleCryptoCore.extractDetachedAscSha256("$expectedSignedHash  *Other.apk", "OneKey.apk")
    )
  }

  @Test
  fun directoryHashesRejectChangedMissingAndUnlistedFiles() {
    val bundle = temporaryFolder.newFolder("bundle")
    val nested = File(bundle, "nested/bundle").apply { mkdirs() }
    val file = File(nested, "index.js").apply { writeText("abc") }
    File(bundle, "metadata.json").writeText("ignored")

    val hashes = BundleCryptoCore.hashDir(bundle.absolutePath)
    assertEquals(1, hashes.size)
    assertEquals("nested/bundle/index.js", hashes.single().relativePath)
    assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hashes.single().sha256)
    assertTrue(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))

    file.writeText("changed")
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))
    file.delete()
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))

    file.writeText("abc")
    File(bundle, "evil-metadata.json").writeText("unlisted")
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))
  }

  @Test
  fun directoryHashesOnlyExemptUnlistedRootMetadataAndFinderFiles() {
    val bundle = temporaryFolder.newFolder("metadata")
    val nested = File(bundle, "assets").apply { mkdirs() }
    val nestedMetadata = File(nested, "metadata.json").apply { writeText("asset manifest") }
    File(bundle, "metadata.json").writeText("bundle manifest")
    val finderFile = File(nested, ".DS_Store").apply { writeText("finder metadata") }

    val hashes = BundleCryptoCore.hashDir(bundle.absolutePath)
    assertEquals("assets/metadata.json", hashes.single().relativePath)
    assertTrue(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, emptyList()))
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(
      bundle.absolutePath,
      hashes + BundleCryptoCore.DirHash("assets", "0".repeat(64)),
    ))
    val finderHash = requireNotNull(BundleCryptoCore.sha256OfFile(finderFile.absolutePath).sha256)
    val manifest = hashes + BundleCryptoCore.DirHash("assets/.DS_Store", finderHash)
    assertTrue(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, manifest))
    finderFile.writeText("tampered")
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, manifest))
    nestedMetadata.writeText("tampered")
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))
    nestedMetadata.delete()
    nestedMetadata.mkdir()
    assertFalse(BundleCryptoCore.verifyDirAgainstHashes(bundle.absolutePath, hashes))
  }

  @Test
  fun directoryHashesRejectExpectedPathsOutsideBundle() {
    val root = temporaryFolder.newFolder("root")
    val bundle = File(root, "bundle").apply { mkdirs() }
    val outside = File(root, "outside.txt").apply { writeText("outside") }
    val wrongHash = "0".repeat(64)

    assertFalse(
      BundleCryptoCore.verifyDirAgainstHashes(
        bundle.absolutePath,
        listOf(BundleCryptoCore.DirHash("../outside.txt", wrongHash))
      )
    )
    assertFalse(
      BundleCryptoCore.verifyDirAgainstHashes(
        bundle.absolutePath,
        listOf(BundleCryptoCore.DirHash(outside.absolutePath, wrongHash))
      )
    )
    Files.createSymbolicLink(File(bundle, "link.txt").toPath(), outside.toPath())
    assertFalse(
      BundleCryptoCore.verifyDirAgainstHashes(
        bundle.absolutePath,
        listOf(BundleCryptoCore.DirHash("link.txt", wrongHash))
      )
    )
    assertFalse(
      BundleCryptoCore.verifyDirAgainstHashes(
        bundle.absolutePath,
        listOf(BundleCryptoCore.DirHash("", wrongHash))
      )
    )
  }

  @Test
  fun extractedPathSafetyRejectsSymlinks() {
    val bundle = temporaryFolder.newFolder("extracted")
    val outside = temporaryFolder.newFile("outside.txt").apply { writeText("outside") }
    File(bundle, "index.js").writeText("inside")
    assertTrue(BundleCryptoCore.validateExtractedPathSafety(bundle.absolutePath))

    Files.createSymbolicLink(File(bundle, "escape").toPath(), outside.toPath())
    assertFalse(BundleCryptoCore.validateExtractedPathSafety(bundle.absolutePath))
  }
}
