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
  fun signedMetadataRejectsChangedBodyAndUnsignedInput() {
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
  fun validSignatureWithWrongAscBodyIsRejectedAfterVerification() {
    val result = BundleCryptoCore.verifyDetachedAsc(signedMetadata)
    assertFalse(result.valid)
    assertNull(result.sha256)
    assertEquals("SHA256_TOKEN_INVALID", result.reason)
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
