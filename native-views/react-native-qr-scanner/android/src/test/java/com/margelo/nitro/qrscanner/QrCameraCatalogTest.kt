package com.margelo.nitro.qrscanner

import com.margelo.nitro.qrscanner.QrCameraCatalog.Candidate
import org.junit.Assert.assertEquals
import org.junit.Test

class QrCameraCatalogTest {
  @Test
  fun offersOneBackOneFrontThenExternalCameras() {
    val picked = QrCameraCatalog.pick(
      listOf(
        Candidate("2", QrCameraPosition.BACK),
        Candidate("1", QrCameraPosition.FRONT),
        Candidate("0", QrCameraPosition.BACK),
        Candidate("10", QrCameraPosition.EXTERNAL),
        Candidate("11", QrCameraPosition.EXTERNAL),
      ),
    )
    assertEquals(listOf("2", "1", "10", "11"), picked.map { it.id })
  }

  @Test
  fun dropsRepeatedIdsAndHandlesMissingPositions() {
    val picked = QrCameraCatalog.pick(
      listOf(Candidate("5", QrCameraPosition.EXTERNAL), Candidate("5", QrCameraPosition.EXTERNAL)),
    )
    assertEquals(listOf("5"), picked.map { it.id })
    assertEquals(emptyList<Candidate>(), QrCameraCatalog.pick(emptyList()))
  }

  @Test
  fun labelsExternalCamerasInOrder() {
    assertEquals("Back camera", QrCameraCatalog.label(QrCameraPosition.BACK, 0))
    assertEquals("External camera", QrCameraCatalog.label(QrCameraPosition.EXTERNAL, 0))
    assertEquals("External camera 2", QrCameraCatalog.label(QrCameraPosition.EXTERNAL, 1))
  }
}
