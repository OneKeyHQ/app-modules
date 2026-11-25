package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject

@Suppress("DEPRECATION")
class NativeListMarketMetricStructureTest : InstrumentationTestCase() {
  private fun item(metrics: JSONArray? = null): NativeListItem {
    val json = JSONObject()
      .put("type", "market")
      .put("key", "market-eth")
      .put("variant", "token")
      .put("title", "ETH")
      .put("subtitle", "Ethereum")
      .put("leading", JSONObject().put("kind", "icon").put("name", "coin"))
      .put("price", "$42")
      .put("change", JSONObject().put("text", "+2%").put("tone", "positive"))
    if (metrics != null) json.put("metrics", metrics)
    return NativeListItem.parse(json)
  }

  private fun metrics(vararg columns: Triple<String, Int, String>): JSONArray =
    JSONArray().apply {
      columns.forEach { (key, weight, text) ->
        put(JSONObject().put("key", key).put("weight", weight).put("alignment", "end")
          .put("text", text))
      }
    }

  fun testTextOnlyQuoteUpdateKeepsStructureWhileColumnChangesRequireRebind() {
    val compact = item()
    val two = item(metrics(Triple("price", 1, "$42"), Triple("change", 1, "+2%")))
    val updated = item(metrics(Triple("price", 1, "$43"), Triple("change", 1, "+3%")))
    val three = item(metrics(
      Triple("price", 1, "$43"), Triple("change", 1, "+3%"), Triple("volume", 1, "$1B")))
    val weighted = item(metrics(Triple("price", 2, "$43"), Triple("change", 1, "+3%")))
    val reordered = item(metrics(Triple("change", 1, "+3%"), Triple("price", 1, "$43")))

    assertTrue(hasSameMarketMetricStructure(compact, compact))
    assertTrue(hasSameMarketMetricStructure(two, updated))
    assertFalse(hasSameMarketMetricStructure(compact, two))
    assertFalse(hasSameMarketMetricStructure(two, three))
    assertFalse(hasSameMarketMetricStructure(two, weighted))
    assertFalse(hasSameMarketMetricStructure(two, reordered))
  }

  fun testAccessibilityUsesDisplayedMetricLinesAndUpdatesWithQuotes() = withMarketRow { row ->
    val columns = metrics(
      Triple("price", 1, "$100"), Triple("change", 1, "+5%"), Triple("volume", 1, "$1B"))
    columns.getJSONObject(0).put("textSegments", JSONArray()
      .put(JSONObject().put("text", "$0.0"))
      .put(JSONObject().put("text", "3").put("style", "subscript")))
      .put("secondaryText", "price secondary fallback")
      .put("secondaryTextSegments", JSONArray().put(JSONObject().put("text", "per ETH")))
    columns.getJSONObject(1).put("secondaryText", "24 hours")
    bind(row, item(columns))
    assertEquals("ETH, Ethereum, $0.03, per ETH, +5%, 24 hours, $1B", row.contentDescription)

    val updated = metrics(
      Triple("price", 1, "$101"), Triple("change", 1, "+6%"), Triple("volume", 1, "$2B"))
    updated.getJSONObject(0).put("secondaryText", "Updated price")
    updated.getJSONObject(1).put("textSegments", JSONArray()
      .put(JSONObject().put("text", "+0.0"))
      .put(JSONObject().put("text", "7%").put("style", "subscript")))
      .put("secondaryText", "updated change fallback")
      .put("secondaryTextSegments", JSONArray().put(JSONObject().put("text", "7 days")))
    row.bindMarketQuote(item(updated), null)
    assertEquals("ETH, Ethereum, $101, Updated price, +0.07%, 7 days, $2B", row.contentDescription)

    bind(row, item(metrics(Triple("price", 1, "$102"), Triple("change", 1, "+8%"))))
    assertEquals("ETH, Ethereum, $102, +8%", row.contentDescription)
    bind(row, item())
    assertEquals("ETH, Ethereum, $42, +2%", row.contentDescription)
  }

  fun testExplicitAccessibilityLabelWinsOnFullBindAndQuote() = withMarketRow { row ->
    fun labeled(price: String): NativeListItem {
      val json = item(metrics(Triple("price", 1, price), Triple("change", 1, "+5%"))).json
      json.put("accessibilityLabel", "Custom quote")
      return NativeListItem.parse(json)
    }
    bind(row, labeled("$100"))
    assertEquals("Custom quote", row.contentDescription)
    row.bindMarketQuote(labeled("$101"), null)
    assertEquals("Custom quote", row.contentDescription)
    row.bindMarketQuote(item(metrics(Triple("price", 1, "$102"), Triple("change", 1, "+6%"))), null)
    assertEquals("ETH, Ethereum, $102, +6%", row.contentDescription)
  }

  fun testBoundedColumnsResizeAndReleaseActions() = withMarketRow { host ->
    val columns = JSONArray()
    repeat(7) { columns.put(JSONObject().put("key", "c$it").put("text", it.toString())) }
    columns.getJSONObject(0).put("width", 44).put("actionKey", "favorite").put("accessibilityLabel", "Favorite")
    columns.getJSONObject(1).put("secondaryText", "Long secondary")
    val json = item().json.put("tableColumns", columns)
    val model = NativeListItem.parse(json)
    assertEquals(NativeListRendererKey.BOUNDED_TABLE, model.rendererKey)
    val row = NativeListRendererRegistry.create(host.context as ThemedReactContext,
      model.rendererKey.ordinal, NativeListFonts()) as NativeListBoundedTableView
    val actions = mutableListOf<String>()
    row.onAction = { _, key, _, _ -> actions.add(key) }
    row.bind(model, null, "linear", "vertical", null, false, { _, _, fallback -> fallback })
    assertEquals(7, row.childCount)
    val density = row.resources.displayMetrics.density
    val width = (1080 * density).toInt()
    val height = (56 * density).toInt()
    row.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
    row.layout(0, 0, width, height)
    val second = row.getChildAt(1) as android.view.ViewGroup
    assertTrue(kotlin.math.abs(second.left - row.getChildAt(0).right - 8 * density) <= 1)
    val labels = (second.getChildAt(0) as android.view.ViewGroup).getChildAt(1) as NativeListTableColumnView
    assertTrue(labels.getChildAt(1).width > labels.getChildAt(0).width)
    val button = row.getChildAt(0)
    assertEquals("Favorite", button.contentDescription)
    assertTrue(button.performClick())
    json.put("tableColumns", JSONArray().put(columns.getJSONObject(1)).put(columns.getJSONObject(2)))
    row.bind(NativeListItem.parse(json), null, "linear", "vertical", null, false, { _, _, fallback -> fallback })
    assertEquals(2, row.childCount)
    assertFalse(button.performClick())
    assertEquals(listOf("favorite"), actions)
    row.dispose()
  }

  fun testBoundedDetailContentAlignmentAccessibilityAndPressedState() = withMarketRow { host ->
    val row = NativeListBoundedTableView(host.context as ThemedReactContext, NativeListFonts())
    val columns = JSONArray().put(JSONObject().put("key", "asset").put("text", "fallback")
      .put("textSegments", JSONArray()
        .put(JSONObject().put("text", "E"))
        .put(JSONObject().put("text", "").put("style", "subscript"))
        .put(JSONObject().put("text", "TH")))
      .put("leading", JSONObject().put("kind", "token").put("fallbackText", "ETH")))
      .put(JSONObject().put("key", "price").put("text", "$0.000001")
        .put("textSegments", JSONArray()
          .put(JSONObject().put("text", "$0.0"))
          .put(JSONObject().put("text", "5").put("style", "subscript"))
          .put(JSONObject().put("text", "1")))
        .put("secondaryText", "0.000002 ETH")
        .put("secondaryTextSegments", JSONArray()
          .put(JSONObject().put("text", "0.0"))
          .put(JSONObject().put("text", "5").put("style", "subscript"))
          .put(JSONObject().put("text", "2 ETH"))))
      .put(JSONObject().put("key", "change").put("text", "24h")
        .put("secondaryTextSegments", JSONArray()
          .put(JSONObject().put("text", "+"))
          .put(JSONObject().put("text", "2%"))
          .put(JSONObject().put("text", "").put("style", "subscript"))))
    val container = JSONObject().put("height", 80)
    val data = JSONObject().put("key", "detail").put("type", "dataRow").put("columns", columns)
      .put("tableColumns", columns).put("style", JSONObject().put("container", container)
        .put("image", JSONObject().put("width", 40).put("height", 40)))
    val density = row.resources.displayMetrics.density
    val width = (320 * density).toInt()
    val height = (80 * density).toInt()
    for (alignment in listOf("top", "bottom")) {
      container.put("contentVerticalAlignment", alignment)
      row.bind(NativeListItem.parse(data), null, "linear", "vertical", null, false, { _, _, fallback -> fallback })
      row.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
      row.layout(0, 0, width, height)
      val cell = row.getChildAt(0) as android.view.ViewGroup
      val group = cell.getChildAt(0) as android.view.ViewGroup
      val visual = group.getChildAt(0)
      val labels = group.getChildAt(1)
      assertTrue(visual.height > labels.height)
      assertTrue(kotlin.math.abs(visual.top + visual.bottom - labels.top - labels.bottom) <= 1)
      if (alignment == "top") assertEquals(0, group.top) else assertEquals(cell.height, group.bottom)
      assertEquals("ETH, $0.000001, 0.000002 ETH, 24h, +2%", row.contentDescription)
    }
    val time = android.os.SystemClock.uptimeMillis()
    fun touch(action: Int, x: Float) {
      val event = android.view.MotionEvent.obtain(time, time, action, x, height / 2f, 0)
      row.dispatchTouchEvent(event)
      event.recycle()
    }
    fun fill() = (row.background as android.graphics.drawable.GradientDrawable).color?.defaultColor
    val normal = fill()
    touch(android.view.MotionEvent.ACTION_DOWN, width / 3f)
    val pressed = fill()
    assertFalse(normal == pressed)
    touch(android.view.MotionEvent.ACTION_MOVE, width / 3f + 1)
    assertEquals(pressed, fill())
    touch(android.view.MotionEvent.ACTION_CANCEL, width / 3f + 1)
    assertEquals(normal, fill())
    row.dispose()
  }

  fun testBoundedCellIconActionsAndReuse() = withMarketActivity { activity, context ->
    lateinit var row: NativeListBoundedTableView
    val received = mutableListOf<String>()
    val column = JSONObject().put("key", "address").put("text", "Address")
    val data = item().json.put("tableColumns", JSONArray().put(column).put(JSONObject().put("key", "value").put("text", "42")))
    val icons = JSONArray()
    for ((key, icon) in listOf("copy" to "Copy3Outline", "open" to "OpenOutline"))
      icons.put(JSONObject().put("name", icon).put("actionKey", key).put("accessibilityLabel", key))
    column.put("actions", icons)
    fun bind() = row.bind(NativeListItem.parse(data), JSONObject().put("iconSubdued", "#123"), "linear", "vertical", null, false, { _, _, fallback -> fallback })
    fun find(key: String) = ArrayList<View>().also {
      row.findViewsWithText(it, key, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
    }
    fun bounds(key: String): Rect {
      val icon = find(key).single()
      return Rect(0, 0, icon.width, icon.height).also { row.offsetDescendantRectToMyCoords(icon, it) }
    }
    fun tap(x: Float, y: Float, afterDown: () -> Unit = {}) {
      val now = SystemClock.uptimeMillis()
      for (action in listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP)) {
        val event = MotionEvent.obtain(now, now, action, x, y, 0)
        try { row.dispatchTouchEvent(event) } finally { event.recycle() }
        if (action == MotionEvent.ACTION_DOWN) afterDown()
      }
    }
    instrumentation.runOnMainSync {
      row = NativeListBoundedTableView(context, NativeListFonts())
      row.onAction = { _, key, _, _ -> received.add(key) }
      activity.setContentView(row)
    }
    for (placement in listOf("inline", "below")) {
      instrumentation.runOnMainSync {
        column.put("actionPlacement", placement)
        bind()
      }
      instrumentation.waitForIdleSync()
      instrumentation.runOnMainSync {
        val copy = bounds("copy")
        val open = bounds("open")
        tap(copy.right + 1f, copy.exactCenterY())
        tap(open.left - 1f, open.exactCenterY())
        tap(copy.exactCenterX(), copy.top - 3 * context.resources.displayMetrics.density)
      }
      instrumentation.waitForIdleSync()
      assertEquals(listOf("copy", "open", "copy"), received)
      received.clear()
    }
    for (expanded in listOf(false, true)) {
      for (replace in listOf(false, true)) {
        instrumentation.runOnMainSync {
          icons.getJSONObject(0).put("actionKey", "copy")
          bind()
        }
        instrumentation.waitForIdleSync()
        instrumentation.runOnMainSync {
          assertEquals(0x72000000, (find("copy").single() as OneKeyIconView).tintColor)
          val copy = bounds("copy")
          tap(if (expanded) copy.right + 1f else copy.exactCenterX(), copy.exactCenterY()) {
            if (replace) icons.getJSONObject(0).put("actionKey", "explorer")
            data.put("price", if (expanded) "$43" else "$44")
            bind()
          }
        }
        instrumentation.waitForIdleSync()
        assertEquals(if (replace) emptyList<String>() else listOf("copy"), received)
        received.clear()
      }
    }
    icons.getJSONObject(0).put("actionKey", "copy")
    val reversed = JSONArray().put(icons.getJSONObject(1)).put(icons.getJSONObject(0))
    for (expanded in listOf(false, true)) {
      instrumentation.runOnMainSync { column.put("actions", icons); bind() }
      instrumentation.waitForIdleSync()
      val down = SystemClock.uptimeMillis()
      fun touchCopy(action: Int) {
        val copy = bounds("copy")
        val y = if (expanded) copy.top - 1f else copy.exactCenterY()
        val event = MotionEvent.obtain(down, SystemClock.uptimeMillis(), action, copy.exactCenterX(), y, 0)
        try { row.dispatchTouchEvent(event) } finally { event.recycle() }
      }
      instrumentation.runOnMainSync {
        touchCopy(MotionEvent.ACTION_DOWN)
        column.put("actions", reversed)
        bind()
      }
      instrumentation.waitForIdleSync()
      instrumentation.runOnMainSync {
        assertTrue(bounds("copy").left > bounds("open").left)
        touchCopy(MotionEvent.ACTION_UP)
      }
      instrumentation.waitForIdleSync()
      assertEquals("reordered button, expanded=$expanded", listOf("copy"), received)
      received.clear()
    }
    instrumentation.runOnMainSync {
      column.remove("actions")
      bind()
      assertTrue(find("copy").isEmpty())
      row.dispose()
    }
  }

  fun testHandledTapFindsTableIconInOffsetWindow() = withMarketActivity { activity, context ->
    lateinit var dialog: android.app.Dialog
    lateinit var list: NativeListView
    lateinit var editor: android.widget.EditText
    val actions = mutableListOf<String>()
    val density = context.resources.displayMetrics.density
    instrumentation.runOnMainSync {
      val action = JSONObject().put("name", "Copy3Outline").put("actionKey", "copy").put("accessibilityLabel", "Copy")
      val columns = JSONArray().put(JSONObject().put("key", "address").put("text", "Address")
        .put("actions", JSONArray().put(action))).put(JSONObject().put("key", "price").put("text", "42"))
      list = NativeListView(context)
      list.keyboardShouldPersistTaps = NativeListKeyboardShouldPersistTaps.HANDLED
      list.onRowAction = { actions.add(JSONObject(it).getString("actionKey")) }
      editor = android.widget.EditText(activity)
      dialog = android.app.Dialog(activity)
      dialog.setContentView(android.widget.LinearLayout(activity).apply {
        orientation = android.widget.LinearLayout.VERTICAL
        addView(editor)
        addView(list, -1, (72 * density).toInt())
      })
      dialog.show()
      dialog.window!!.setLayout((280 * density).toInt(), -2)
      list.applySnapshot(JSONObject().put("schemaVersion", 1).put("generation", 1)
        .put("layout", JSONObject().put("kind", "linear")).put("rows", JSONArray().put(
          item().json.put("pressDisabled", true).put("tableColumns", columns))).toString())
    }
    fun waitForKeyboard(visible: Boolean) {
      val deadline = SystemClock.uptimeMillis() + 5000
      do {
        var matched = false
        instrumentation.runOnMainSync {
          matched = androidx.core.view.ViewCompat.getRootWindowInsets(editor)
            ?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime()) == visible
        }
        if (matched) return
        SystemClock.sleep(50)
      } while (SystemClock.uptimeMillis() < deadline)
      fail("Expected keyboard visibility: $visible")
    }
    fun tap(x: Int, y: Int) {
      val down = SystemClock.uptimeMillis()
      for (action in listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP)) {
        val event = MotionEvent.obtain(down, SystemClock.uptimeMillis(), action, x.toFloat(), y.toFloat(), 0)
        try { instrumentation.sendPointerSync(event) } finally { event.recycle() }
      }
      instrumentation.waitForIdleSync()
    }
    try {
      instrumentation.waitForIdleSync()
      val origin = IntArray(2)
      instrumentation.runOnMainSync {
        editor.getLocationOnScreen(origin)
        origin[0] += editor.width / 2
        origin[1] += editor.height / 2
      }
      tap(origin[0], origin[1])
      waitForKeyboard(true)
      var copyX = 0
      var blankX = 0
      var y = 0
      instrumentation.runOnMainSync {
        val icon = ArrayList<View>().also { list.findViewsWithText(it, "Copy", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION) }.single()
        list.rootView.getLocationOnScreen(origin)
        assertTrue("Dialog must exercise a window offset", origin[1] > 0)
        icon.getLocationOnScreen(origin)
        copyX = origin[0] + icon.width + 1
        y = origin[1] + icon.height / 2
        list.getLocationOnScreen(origin)
        blankX = origin[0] + list.width * 3 / 4
      }
      tap(copyX, y)
      assertEquals(listOf("copy"), actions)
      waitForKeyboard(true)
      tap(blankX, y)
      waitForKeyboard(false)
      assertEquals(listOf("copy"), actions)
    } finally {
      instrumentation.runOnMainSync { dialog.dismiss(); list.dispose() }
    }
  }

  fun testBoundedStyledTextFitsFixedAndFlexibleCells() = withMarketRow { host ->
    val row = NativeListBoundedTableView(host.context as ThemedReactContext, NativeListFonts())
    val width = (240 * row.resources.displayMetrics.density).toInt()
    val height = (64 * row.resources.displayMetrics.density).toInt()
    for (type in listOf("market", "dataRow")) {
      val columns = JSONArray().put(JSONObject().put("key", "fixed").put("width", 96)
        .put("text", "ETH").put("secondaryText", "Token"))
        .put(JSONObject().put("key", "flex").put("text", "$42").put("secondaryText", "USD"))
      val style = JSONObject().put(if (type == "market") "price" else "columns", JSONObject().put("alignment", "end"))
        .put(if (type == "market") "subtitle" else "columnSecondary", JSONObject().put("alignment", "center"))
      val data = JSONObject().put("type", type).put("key", type).put("columns", columns)
        .put("tableColumns", columns).put("style", style)
      row.bind(NativeListItem.parse(data), null, "linear", "vertical", null, false, { _, _, fallback -> fallback })
      row.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
      row.layout(0, 0, width, height)
      for (text in listOf("ETH", "Token", "$42", "USD")) {
        val matches = ArrayList<View>()
        row.findViewsWithText(matches, text, View.FIND_VIEWS_WITH_TEXT)
        val label = matches.filterIsInstance<android.widget.TextView>().single { it.text.toString() == text }
        assertTrue("$type $text should fit", label.width >= kotlin.math.floor(label.paint.measureText(text)).toInt())
      }
    }
    row.dispose()
  }

  private fun bind(row: NativeListMarketRowView, item: NativeListItem) {
    row.bind(item, null, "linear", "vertical", null, false, { _, _, fallback -> fallback }, true)
  }

  private fun withMarketRow(action: (NativeListMarketRowView) -> Unit) = withMarketActivity { activity, context ->
    instrumentation.runOnMainSync {
      val row = NativeListMarketRowView(context, NativeListFonts())
      try {
        activity.setContentView(row)
        action(row)
      } finally {
        row.dispose()
      }
    }
  }

  private fun withMarketActivity(action: (Activity, ThemedReactContext) -> Unit) {
    val activity = instrumentation.startActivitySync(
      Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    try {
      lateinit var context: ThemedReactContext
      instrumentation.runOnMainSync {
        SoLoader.init(activity, OpenSourceMergedSoMapping)
        OneKeyLog.init(activity)
        context = ThemedReactContext(BridgeReactContext(activity), activity, "MarketMetricsTest", 1)
      }
      action(activity, context)
    } finally {
      instrumentation.runOnMainSync {
        activity.setContentView(View(activity))
        activity.finish()
      }
    }
  }
}
