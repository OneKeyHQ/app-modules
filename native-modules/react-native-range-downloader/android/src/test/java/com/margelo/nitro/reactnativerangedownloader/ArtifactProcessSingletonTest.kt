package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class ArtifactProcessSingletonTest {
  private lateinit var root: File

  @Before
  fun setUp() {
    root = createTempDirectory("firmware-process-singleton-").toFile()
    FirmwareArtifactStore.resetProcessInstanceForTests()
  }

  @After
  fun tearDown() {
    FirmwareArtifactStore.resetProcessInstanceForTests()
    root.deleteRecursively()
  }

  @Test
  fun separateHybridObjectsShareNativeStoreAndTaskRegistry() {
    assertSame(
      FirmwareArtifactStore.processInstance(root),
      FirmwareArtifactStore.processInstance(root),
    )
    assertSame(
      FirmwareArtifactRegistry.processInstance,
      FirmwareArtifactRegistry.processInstance,
    )
  }
}
