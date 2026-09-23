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
      // metricCard swaps the two views: the large number is drawn by the title
      // view and the small label by the subtitle view.
      Case(type = "metricCard", field = "value", expected = "title"),
      Case(type = "metricCard", field = "title", expected = "subtitle"),
      Case(type = "metricCard", field = "subtitle", expected = "metricSubtitle"),
      Case(type = "metricCard", field = "trend", expected = "status"),
      // identity keeps its own names.
      Case(type = "identity", field = "title", expected = "title"),
      Case(type = "identity", field = "valueSecondary", expected = "valueSecondary"),
      // Legacy status views only serve unmigrated templates.
      Case(type = "rail", field = "status", expected = null),
      Case(type = "activity", field = "status", expected = "status"),
      Case(type = "message", field = "time", expected = null),
      // Amounts, indices and values share the two trailing views.
      Case(type = "activity", field = "primaryAmount", expected = "value"),
      Case(type = "activity", field = "secondaryAmount", expected = "valueSecondary"),
      Case(type = "dataRow", field = "index", expected = "value"),
      Case(type = "dataRow", field = "columns", expected = "dataPrimary"),
      Case(type = "dataRow", field = "columnSecondary", expected = "dataSecondary"),
      Case(type = "system", field = "title", variant = "retry", expected = null),
      Case(type = "sectionHeader", field = "value", expected = "value"),
      // Only the warning variant renders a separate title.
      Case(type = "system", field = "message", variant = "warning", expected = "subtitle"),
      Case(type = "system", field = "message", variant = "noMatch", expected = "title"),
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
      Triple("activity", "", listOf("title", "description", "status", "primaryAmount", "secondaryAmount")),
      Triple("dataRow", "", listOf("columns", "columnSecondary", "index")),
      Triple("metricCard", "", listOf("title", "value", "subtitle", "trend")),
      Triple("sectionHeader", "", listOf("title", "subtitle", "value")),
      Triple("action", "", listOf("title", "value")),
      // Only `warning` carries both a title and a message; the other variants
      // have no title field at all, so their message owning the title view is
      // not a collision.
      Triple("system", "warning", listOf("title", "message", "actionText")),
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
