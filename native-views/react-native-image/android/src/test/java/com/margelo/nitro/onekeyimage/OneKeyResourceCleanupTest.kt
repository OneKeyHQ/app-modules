package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class OneKeyResourceCleanupTest {
  @Test
  fun ordinaryDecoderExceptionsRecycleAndRethrowTheSameFailure() {
    listOf<Exception>(IOException("corrupt"), IllegalStateException("invalid"))
      .forEach { expected ->
        var recycleCount = 0

        val thrown = assertThrows(expected.javaClass) {
          OneKeyResourceCleanup.recycleOnException(
            recycle = { recycleCount += 1 },
            block = { throw expected },
          )
        }

        assertSame(expected, thrown)
        assertEquals(1, recycleCount)
      }
  }

  @Test
  fun errorsAreNotCaughtOrRecycled() {
    val expected = AssertionError("fatal")
    var recycleCount = 0

    val thrown = assertThrows(AssertionError::class.java) {
      OneKeyResourceCleanup.recycleOnException(
        recycle = { recycleCount += 1 },
        block = { throw expected },
      )
    }

    assertSame(expected, thrown)
    assertEquals(0, recycleCount)
  }
}
