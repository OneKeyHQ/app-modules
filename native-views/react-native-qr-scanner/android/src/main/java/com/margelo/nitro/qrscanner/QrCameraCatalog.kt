package com.margelo.nitro.qrscanner

/** Which cameras the scanner offers, kept pure for unit tests. */
object QrCameraCatalog {
  data class Candidate(val id: String, val position: QrCameraPosition)

  /**
   * One back and one front camera (the first of each, which CameraX lists as
   * the default lenses; further lenses sit behind the logical camera), then
   * every external camera, without repeated ids.
   */
  fun pick(candidates: List<Candidate>): List<Candidate> {
    val seen = HashSet<String>()
    val ordered = listOfNotNull(
      candidates.firstOrNull { it.position == QrCameraPosition.BACK },
      candidates.firstOrNull { it.position == QrCameraPosition.FRONT },
    ) + candidates.filter { it.position == QrCameraPosition.EXTERNAL }
    return ordered.filter { seen.add(it.id) }
  }

  fun label(position: QrCameraPosition, externalIndex: Int): String = when (position) {
    QrCameraPosition.BACK -> "Back camera"
    QrCameraPosition.FRONT -> "Front camera"
    QrCameraPosition.EXTERNAL -> if (externalIndex == 0) "External camera" else "External camera ${externalIndex + 1}"
  }
}
