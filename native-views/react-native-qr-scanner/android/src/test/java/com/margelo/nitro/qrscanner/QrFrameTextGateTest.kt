package com.margelo.nitro.qrscanner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrFrameTextGateTest {
  @Test
  fun nothingIsReportedWhileStopped() {
    val gate = QrFrameTextGate()
    gate.nextGeneration()
    assertEquals(emptyList<QrFrameTextGate.Report>(), gate.filter(listOf("a"), 0))
  }

  @Test
  fun repeatsAreThrottledButNewTextPassesImmediately() {
    val gate = QrFrameTextGate(repeatIntervalMs = 1_000)
    val generation = gate.nextGeneration()
    gate.setCapturing(true)
    assertEquals(listOf("a"), gate.filter(listOf("a"), 0).map { it.text })
    assertEquals(emptyList<String>(), gate.filter(listOf("a"), 500).map { it.text })
    assertEquals(listOf("b"), gate.filter(listOf("b"), 600).map { it.text })
    assertEquals(listOf("b"), gate.filter(listOf("b"), 1_700).map { it.text })
    assertEquals(generation, gate.filter(listOf("c"), 1_800).single().generation)
  }

  @Test
  fun stopAndRestartStartNewGenerationAndClearRepeatState() {
    val gate = QrFrameTextGate(repeatIntervalMs = 10_000)
    val first = gate.nextGeneration()
    gate.setCapturing(true)
    assertEquals(1, gate.filter(listOf("a"), 0).size)
    gate.nextGeneration()
    gate.setCapturing(false)
    assertEquals(0, gate.filter(listOf("late"), 1).size)
    val restarted = gate.nextGeneration()
    gate.setCapturing(true)
    assertTrue(restarted > first)
    assertEquals(listOf(QrFrameTextGate.Report("a", restarted)), gate.filter(listOf("a"), 2))
  }

  @Test
  fun animatedUrPartsAtTenFramesPerSecondAllPass() {
    val gate = QrFrameTextGate(repeatIntervalMs = 1_000)
    gate.nextGeneration()
    gate.setCapturing(true)
    val parts = (1..12).map { "ur:crypto-psbt/$it-12/payload$it" }
    val reported = ArrayList<String>()
    var now = 0L
    repeat(2) {
      for (part in parts) {
        repeat(3) {
          reported += gate.filter(listOf(part), now).map { it.text }
          now += 33
        }
      }
    }
    assertEquals(parts, reported.take(12))
    assertEquals(24, reported.size)
  }

  @Test
  fun emptyTextIsIgnored() {
    val gate = QrFrameTextGate()
    gate.nextGeneration()
    gate.setCapturing(true)
    assertEquals(listOf("x"), gate.filter(listOf("", "x"), 0).map { it.text })
  }
}
