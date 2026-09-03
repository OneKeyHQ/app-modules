package com.margelo.nitro.onekeyimage

import com.bumptech.glide.load.DataSource
import org.junit.Assert.assertEquals
import org.junit.Test

class OneKeyImageCacheTypeTest {
  @Test
  fun exposesOnlyThePublicCacheTypeSemantics() {
    assertEquals(OneKeyImageCacheType.MEMORY, DataSource.MEMORY_CACHE.toOneKeyImageCacheType())
    assertEquals(OneKeyImageCacheType.DISK, DataSource.DATA_DISK_CACHE.toOneKeyImageCacheType())
    assertEquals(OneKeyImageCacheType.DISK, DataSource.RESOURCE_DISK_CACHE.toOneKeyImageCacheType())
    assertEquals(OneKeyImageCacheType.NONE, DataSource.LOCAL.toOneKeyImageCacheType())
    assertEquals(OneKeyImageCacheType.NONE, DataSource.REMOTE.toOneKeyImageCacheType())
  }
}
