package com.margelo.nitro.qrscanner

/**
 * Decides which detected texts the live view reports to JS. Same rules as
 * ios/QrFrameTextGate.swift:
 * - nothing is reported while capture is stopped;
 * - every start and stop begins a new generation, carried by each report;
 * - the same text is reported at most once per [repeatIntervalMs] within a
 *   generation, while new text (each part of an animated code) passes at once.
 */
class QrFrameTextGate(private val repeatIntervalMs: Long = 1_000) {
  data class Report(val text: String, val generation: Int)

  private val lock = Any()
  private var generation = 0
  private var capturing = false
  private var lastText: String? = null
  private var lastTextAtMs = 0L

  /** Starts a new generation; capture stays stopped until [setCapturing]. */
  fun nextGeneration(): Int = synchronized(lock) {
    generation += 1
    lastText = null
    generation
  }

  val currentGeneration: Int
    get() = synchronized(lock) { generation }

  fun setCapturing(value: Boolean) = synchronized(lock) { capturing = value }

  fun filter(texts: List<String>, nowMs: Long): List<Report> = synchronized(lock) {
    if (!capturing) return emptyList()
    val reports = ArrayList<Report>()
    for (text in texts) {
      if (text.isEmpty()) continue
      if (text == lastText && nowMs - lastTextAtMs < repeatIntervalMs) continue
      lastText = text
      lastTextAtMs = nowMs
      reports.add(Report(text, generation))
    }
    reports
  }
}
