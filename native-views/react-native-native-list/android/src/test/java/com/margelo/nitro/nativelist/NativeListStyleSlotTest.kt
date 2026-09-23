package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListStyleSlotTest {
  private data class Case(
    val type: String,
    val field: String,
    val variant: String = "",
    val expected: String?,
  )

  @Test
  fun styleKeyResolvesToTheViewThatRendersThatModelField() {
    val cases = listOf(
      // identity keeps its own names.
      Case(type = "identity", field = "title", expected = "title"),
      Case(type = "identity", field = "valueSecondary", expected = "valueSecondary"),
      // Legacy status views only serve unmigrated templates.
      Case(type = "rail", field = "status", expected = null),
      Case(type = "message", field = "time", expected = null),
      // Migrated templates no longer resolve slots through the legacy pool.
      Case(type = "system", field = "title", variant = "retry", expected = null),
      Case(type = "sectionHeader", field = "value", expected = "value"),

      Case(type = "system", field = "message", variant = "warning", expected = null),
      Case(type = "system", field = "message", variant = "noMatch", expected = null),
      // A field the template does not render is ignored, never remapped onto
      // whichever view happens to be free.
      Case(type = "identity", field = "price", expected = null),
      Case(type = "rail", field = "subtitle", expected = null),
      Case(type = "market", field = "title", expected = null),
      Case(type = "walletGroup", field = "title", expected = null),
    )

    assertEquals(
      cases.map(Case::expected),
      cases.map { nativeListStyleSlot(it.type, it.variant, it.field) },
    )
  }

  @Test
  fun noTemplateMapsTwoStyleKeysOntoOneView() {
    // A collision would make one of the two keys silently win.
    val templates = listOf(
      Triple("identity", "", listOf("title", "subtitle", "tertiary", "badge", "value", "valueSecondary")),
      Triple("sectionHeader", "", listOf("title", "subtitle", "value")),

    )

    templates.forEach { (type, variant, fields) ->
      val slots = fields.mapNotNull { nativeListStyleSlot(type, variant, it) }
      assertEquals(
        "$type maps two style keys onto one view: $slots",
        slots.size,
        slots.toSet().size,
      )
    }
  }
}
