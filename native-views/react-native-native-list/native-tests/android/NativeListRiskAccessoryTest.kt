package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.nativelogger.OneKeyLog
import com.facebook.soloader.SoLoader
import org.json.JSONArray
import org.json.JSONObject

@Suppress("DEPRECATION")
class NativeListRiskAccessoryTest : InstrumentationTestCase() {
  private lateinit var activity: Activity
  private lateinit var list: NativeListView
  private lateinit var recycler: RecyclerView
  private val events = mutableListOf<JSONObject>()
  private var generation = 0

  override fun setUp() {
    super.setUp()
    activity = instrumentation.startActivitySync(
      Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      OneKeyLog.init(activity)
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "RiskTest", 1)
      list = NativeListView(context)
      list.onRowAction = { events.add(JSONObject(it)) }
      activity.setContentView(list)
      recycler = field(list, "recyclerView")
      bindRow(row("risk", "show", "button"))
    }
    awaitRow("risk")
  }

  override fun tearDown() {
    onMain {
      activity.setContentView(View(activity))
      list.dispose()
      activity.finish()
    }
    super.tearDown()
  }

  fun testActivityHideLeadingRebindsLegacyAndRichWithoutReservedSlot() {
    onMain {
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "ActivityTest", 1)
      val rowView = NativeListActivityRowView(context, NativeListFonts())
      activity.setContentView(rowView)
      fun bind(rich: Boolean, hidden: Boolean) {
        val data = JSONObject().put("key", "tx").put("type", "activity")
          .put("title", "Send").put("description", "To Alice")
          .put("leading", JSONObject().put("kind", "icon").put("name", "ArrowTopOutline"))
          .put("hideLeading", hidden)
          .put("footerActions", JSONArray().put(JSONObject()
            .put("key", "speed-up").put("label", "Speed up")))
        if (rich) data.put("amounts", JSONArray().put(JSONObject()
          .put("key", "send").put("text", "••••")))
        else data.put("primaryAmount", "••••")
        rowView.bind(
          NativeListItem("tx", "activity", null, 0, data), null, "linear", "vertical", 0,
          false, { _, _, fallback -> fallback }, false,
        )
      }
      for (rich in listOf(false, true)) {
        val owner = if (rich) field<View>(rowView, "details") else rowView
        val visual = field<View>(owner, "visual")
        val actions = field<LinearLayout>(owner, "actions")
        for (hidden in listOf(false, true, false)) {
          bind(rich, hidden)
          assertEquals(if (hidden) View.GONE else View.VISIBLE, visual.visibility)
          assertEquals(if (hidden) 0 else NativeListScale.dp(activity.resources, 12),
            (visual.layoutParams as LinearLayout.LayoutParams).marginEnd)
          assertEquals(if (hidden) 0 else NativeListScale.dp(activity.resources, 52),
            (actions.layoutParams as LinearLayout.LayoutParams).marginStart)
          assertEquals("Speed up", (actions.getChildAt(0) as TextView).text.toString())
          if (rich) {
            val amount = field<LinearLayout>(owner, "amounts").getChildAt(0)
            assertEquals("••••", field<TextView>(amount, "primary").text.toString())
          } else {
            assertEquals("••••", field<List<TextView>>(rowView, "values")[0].text.toString())
          }
        }
      }
    }
  }

  fun testButtonTouchIsSingleActionAndDisabledCannotPressRow() {
    val button = onMainValue { button() }
    onMain {
      val info = button.createAccessibilityNodeInfo()
      assertEquals(android.widget.Button::class.java.name, info.className.toString())
      assertTrue(info.isEnabled)
      assertTrue(info.isClickable)
    }
    tap(button)
    onMain {
      assertEquals(1, events.size)
      assertEquals("risk", events.single().getString("rowKey"))
      assertEquals("show", events.single().getString("actionKey"))
      val anchor = events.single().getJSONObject("anchor")
      assertEquals("trailingAccessory", anchor.getString("source"))
      assertEquals(0, anchor.getInt("slot"))
      bindRow(row("risk", "show", "button", accessoryDisabled = true))
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optBoolean("disabled") == true }
    onMain {
      val info = button().createAccessibilityNodeInfo()
      assertEquals(android.widget.Button::class.java.name, info.className.toString())
      assertFalse(info.isEnabled)
      assertFalse(info.isClickable)
    }
    tap(onMainValue { button() })
    onMain {
      assertEquals(1, events.size)
      bindRow(row("risk", "show", "button", rowDisabled = true))
    }
    awaitRow("risk") { it.optBoolean("disabled") }
    tap(onMainValue { button() })
    onMain { assertEquals(1, events.size) }
  }

  fun testFullRebindClearsOldControlsAndVisualState() {
    lateinit var oldButton: TextView
    lateinit var rowHost: NativeListRowHost
    onMain {
      bindRow(row("risk", "show", "button").apply {
        getJSONArray("trailing").getJSONObject(0)
          .put("accessibilityLabel", "Risk toggle").put("testID", "risk-toggle")
      })
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optString("testID") == "risk-toggle" }
    onMain {
      rowHost = holder().rowView
      oldButton = button()
      assertEquals("Risk toggle", oldButton.contentDescription)
      assertEquals("risk-toggle", oldButton.getTag(com.facebook.react.R.id.react_test_id))
      val identity = rowHost as NativeListIdentityRowView
      assertEquals(View.VISIBLE, field<View>(identity, "titleIcon").visibility)
      assertEquals(NativeListScale.dp(activity.resources, 1), field<NativeListLeadingVisual>(identity, "leadingFrame").bitmapBorderWidth)
      assertTrue(field<PackedTitleLineLayout>(identity, "titleLine").leadingTextMaxWidth < Int.MAX_VALUE)
      bindRow(row("risk", "edit", "icon", titleIcon = false, border = false))
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optString("kind") == "icon" }
    onMain {
      assertSame(rowHost, holder().rowView)
      val identity = rowHost as NativeListIdentityRowView
      assertEquals(View.GONE, field<View>(identity, "titleIcon").visibility)
      assertFalse(oldButton.isClickable)
      assertNull(oldButton.contentDescription)
      assertNull(oldButton.getTag(com.facebook.react.R.id.react_test_id))
      assertEquals(0, field<NativeListLeadingVisual>(identity, "leadingFrame").bitmapBorderWidth)
      assertEquals(Int.MAX_VALUE, field<PackedTitleLineLayout>(identity, "titleLine").leadingTextMaxWidth)
      oldButton.performClick()
      assertTrue(events.isEmpty())
      bindRow(row("risk", "", "none", titleIcon = false, border = false))
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.length() == 0 }
    onMain {
      assertSame(rowHost, holder().rowView)
      oldButton.performClick()
      assertTrue(events.isEmpty())
      bindRow(row("risk", "", "none", titleIcon = false, border = false).apply {
        getJSONArray("trailing").put(JSONObject().put("kind", "value").put("text", "Risk"))
      })
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optString("kind") == "value" }
    onMain {
      assertSame(rowHost, holder().rowView)
      assertSame(oldButton, button())
      assertEquals(TextView::class.java.name, oldButton.createAccessibilityNodeInfo().className.toString())
      assertFalse(oldButton.isClickable)
      assertNull(oldButton.contentDescription)
      assertNull(oldButton.getTag(com.facebook.react.R.id.react_test_id))
      bindRow(row("risk", "show", "button", titleIcon = false, border = false))
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optString("kind") == "button" }
    val currentButton = onMainValue { button() }
    val location = onMainValue {
      IntArray(2).also { currentButton.getLocationOnScreen(it) }
    }
    val downTime = SystemClock.uptimeMillis()
    instrumentation.sendPointerSync(MotionEvent.obtain(
      downTime, downTime, MotionEvent.ACTION_DOWN,
      location[0] + currentButton.width / 2f,
      location[1] + currentButton.height / 2f, 0))
    onMain { bindRow(row("risk", "hide", "button", titleIcon = false, border = false)) }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optString("actionKey") == "hide" }
    instrumentation.sendPointerSync(MotionEvent.obtain(
      downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
      location[0] + currentButton.width / 2f,
      location[1] + currentButton.height / 2f, 0))
    instrumentation.waitForIdleSync()
    onMain {
      assertSame(rowHost, holder().rowView)
      assertTrue(events.isEmpty())
    }
    tap(onMainValue { button() })
    onMain {
      assertEquals(1, events.size)
      assertEquals("risk", events.single().getString("rowKey"))
      assertEquals("hide", events.single().getString("actionKey"))
      bindRow(row("next-risk", "show", "button", titleIcon = false, border = false))
    }
    awaitRow("next-risk")
    tap(onMainValue { button() })
    onMain {
      assertEquals(2, events.size)
      assertEquals("next-risk", events.last().getString("rowKey"))
      assertEquals("show", events.last().getString("actionKey"))
    }
  }

  fun testOfficialRiskGlyphsPaintPixels() {
    onMain {
      val identity = holder().rowView as NativeListIdentityRowView
      val gas = field<OneKeyIconView>(identity, "titleIcon")
      assertTrue(paintedPixelCount(gas) > 0)
      val crypto = OneKeyIconView(activity).apply {
        iconName = "CryptoCoinOutline"
        tintColor = Color.BLACK
        layout(0, 0, 48, 48)
      }
      assertTrue(paintedPixelCount(crypto) > 0)
    }
  }

  fun testProductionIdentityRendererDirectBindResetsSameInstance() {
    onMain {
      val renderer = NativeListIdentityRowView(list.context as ThemedReactContext, NativeListFonts())
      fun bind(json: JSONObject) {
        renderer.bind(
          NativeListItem(json.getString("key"), "identity", null, 0, json),
          null, "linear", "vertical", 0, false,
          { _, _, fallback -> fallback }, false,
        )
        renderer.measure(
          View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        )
        renderer.layout(0, 0, 1080, renderer.measuredHeight)
      }
      bind(row("risk", "show", "button"))
      val trailing = field<NativeListAccessoryStack>(renderer, "trailingColumn")
      val oldButton = trailing.visibleValues.single()
      assertEquals(View.VISIBLE, field<View>(renderer, "titleIcon").visibility)
      bind(row("risk", "edit", "icon", titleIcon = false, border = false))
      assertTrue(trailing.visibleValues.isEmpty())
      assertEquals(View.GONE, field<View>(renderer, "titleIcon").visibility)
      assertEquals(0, field<NativeListLeadingVisual>(renderer, "leadingFrame").bitmapBorderWidth)
      assertFalse(oldButton.isClickable)
      assertNull(oldButton.contentDescription)
      bind(row("risk", "", "none", titleIcon = false, border = false))
      assertNull(trailing.firstVisibleIcon)
      assertTrue(trailing.visibleValues.isEmpty())
      bind(row("risk", "hide", "button", titleIcon = true, border = true))
      assertEquals("Hide", trailing.visibleValues.single().text.toString())
      assertEquals(View.VISIBLE, field<View>(renderer, "titleIcon").visibility)
    }
  }

  fun testButtonAndTertiaryGeometryUsesOriginalRowInsets() {
    onMain {
      val button = button()
      assertEquals(NativeListScale.dp(activity.resources, 30), button.height)
      assertEquals(NativeListScale.dp(activity.resources, 11), button.paddingLeft)
      assertEquals(NativeListScale.dp(activity.resources, 11), button.paddingRight)
      assertEquals(NativeListScale.dp(activity.resources, 20),
        button.lineHeight)
      val identity = holder().rowView as NativeListIdentityRowView
      val inner = identity.width - identity.paddingLeft - identity.paddingRight
      val leading = field<NativeListLeadingVisual>(identity, "leadingFrame")
      val cap = field<PackedTitleLineLayout>(identity, "titleLine").leadingTextMaxWidth
      assertTrue(cap < (inner * 0.6).toInt())
      assertTrue(leading.width > 0)
      bindRow(row("risk", "edit", "icon", tertiary = true))
    }
    awaitRow("risk") { it.optJSONArray("trailing")?.optJSONObject(0)?.optString("buttonStyle") == "tertiary" }
    onMain {
      val identity = holder().rowView as NativeListIdentityRowView
      val stack = field<NativeListAccessoryStack>(identity, "trailingColumn")
      val icon = stack.firstVisibleIcon!!
      assertEquals(NativeListScale.dp(activity.resources, 38), icon.width)
      assertEquals(NativeListScale.dp(activity.resources, 12), identity.paddingRight)
      val rowPosition = IntArray(2).also(identity::getLocationOnScreen)
      val iconPosition = IntArray(2).also(icon::getLocationOnScreen)
      assertEquals(NativeListScale.dp(activity.resources, 12) -
        NativeListScale.dp(activity.resources, 7),
        identity.width - (iconPosition[0] - rowPosition[0] + icon.width))
    }
  }

  fun testFallbackNoImageFailureThenSuccessKeepsLatestSource() {
    val successFile = File(activity.cacheDir, "native-list-risk-test.png")
    val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply {
      eraseColor(Color.RED)
    }
    successFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bitmap.recycle()
    val responseReady = CountDownLatch(1)
    val imageServer = ServerSocket(0)
    val serverThread = Thread {
      try {
        imageServer.accept().use { client ->
          val input = client.getInputStream().bufferedReader()
          while (input.readLine()?.isNotEmpty() == true) Unit
          if (responseReady.await(10, TimeUnit.SECONDS)) {
            val bytes = successFile.readBytes()
            client.getOutputStream().write(
              "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n".toByteArray()
            )
            client.getOutputStream().write(bytes)
            client.getOutputStream().flush()
          }
        }
      } catch (_: Exception) {
        // Closing the server releases a pending accept after the assertion.
      }
    }.apply { start() }
    try {
      fun fallbackRow(uri: String? = null): JSONObject {
        val row = row("risk", "show", "button")
        val leading = row.getJSONObject("leading")
          .put("fallbackIcon", JSONObject().put("name", "CryptoCoinOutline")
            .put("size", 28).put("backgroundColor", "#444444"))
        if (uri != null) leading.put("image", JSONObject().put("uri", uri)
          .put("width", 40).put("height", 40).put("loadingStrategy", "skeleton"))
        return row
      }
      onMain { bindRow(fallbackRow()) }
      awaitRow("risk") { it.optJSONObject("leading")?.optJSONObject("fallbackIcon") != null }
      val loadingUri = "http://127.0.0.1:${imageServer.localPort}/native-list-risk.png"
      onMain {
        val leading = field<NativeListLeadingVisual>(holder().rowView, "leadingFrame")
        assertEquals(View.VISIBLE, field<OneKeyIconView>(leading, "icon").visibility)
        assertEquals(NativeListScale.dp(activity.resources, 28), field<OneKeyIconView>(leading, "icon").width)
        assertEquals(Color.rgb(0x44, 0x44, 0x44),
          (leading.background as GradientDrawable).color?.defaultColor)
        bindRow(fallbackRow(loadingUri))
      }
      awaitRow("risk") { it.optJSONObject("leading")?.optJSONObject("image")
        ?.optString("uri") == loadingUri }
      awaitCondition {
        val leading = field<NativeListLeadingVisual>(holder().rowView, "leadingFrame")
        val slot = field<MutableList<NativeListImageSlot>>(leading, "slots").first()
        val image = field<Any>(slot.view, "image")
        val host = field<Any>(image, "hostView")
        field<Boolean>(host, "skeletonRequested") && slot.view.visibility == View.VISIBLE &&
          field<OneKeyIconView>(leading, "icon").visibility == View.GONE
      }
      responseReady.countDown()
      val failedUri = "file:///missing/native-list-risk-${SystemClock.uptimeMillis()}.png"
      onMain {
        bindRow(fallbackRow(failedUri))
      }
      awaitRow("risk") { it.optJSONObject("leading")?.optJSONObject("image")
        ?.optString("uri") == failedUri }
      val failedKey = nativeListSourceFallbackStateKey(failedUri, emptyMap())!!
      awaitCondition { NativeListSourceFallbackState.has(failedKey) }
      awaitCondition {
        val leading = field<NativeListLeadingVisual>(holder().rowView, "leadingFrame")
        field<OneKeyIconView>(leading, "icon").visibility == View.VISIBLE
      }
      onMain {
        val leading = field<NativeListLeadingVisual>(holder().rowView, "leadingFrame")
        assertEquals(Color.rgb(0x44, 0x44, 0x44),
          (leading.background as GradientDrawable).color?.defaultColor)
        bindRow(fallbackRow(Uri.fromFile(successFile).toString()))
      }
      awaitRow("risk") { it.optJSONObject("leading")?.optJSONObject("image")
        ?.optString("uri") == Uri.fromFile(successFile).toString() }
      awaitCondition {
        val leading = field<NativeListLeadingVisual>(holder().rowView, "leadingFrame")
        field<OneKeyIconView>(leading, "icon").visibility == View.GONE
      }
      onMain {
        val leading = field<NativeListLeadingVisual>(holder().rowView, "leadingFrame")
        assertEquals(Color.rgb(0x55, 0x55, 0x55),
          (leading.background as GradientDrawable).color?.defaultColor)
      }
    } finally {
      responseReady.countDown()
      imageServer.close()
      serverThread.join(1000)
      successFile.delete()
    }
  }

  fun testExistingDashedVisualDoesNotGainSolidBitmapBorder() {
    onMain {
      val dashed = row("risk", "show", "button")
      dashed.getJSONObject("leading").put("borderStyle", "dashed")
      bindRow(dashed)
    }
    awaitRow("risk") { it.optJSONObject("leading")?.optString("borderStyle") == "dashed" }
    onMain {
      val identity = holder().rowView as NativeListIdentityRowView
      assertEquals(0, field<NativeListLeadingVisual>(identity, "leadingFrame").bitmapBorderWidth)
    }
  }

  private fun paintedPixelCount(view: View): Int {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    view.draw(Canvas(bitmap))
    var count = 0
    for (y in 0 until bitmap.height) {
      for (x in 0 until bitmap.width) {
        if (Color.alpha(bitmap.getPixel(x, y)) > 0) count += 1
      }
    }
    bitmap.recycle()
    return count
  }

  private fun row(
    key: String,
    action: String,
    kind: String,
    accessoryDisabled: Boolean = false,
    rowDisabled: Boolean = false,
    titleIcon: Boolean = true,
    border: Boolean = true,
    tertiary: Boolean = false,
  ): JSONObject {
    val row = JSONObject().put("key", key).put("type", "identity")
      .put("title", "A long risk token symbol")
      .put("height", 72)
      .put("disabled", rowDisabled)
      .put("leading", JSONObject().put("kind", "token")
        .put("backgroundColor", "#555555")
        .put("overlays", JSONArray().put(JSONObject()
          .put("position", "bottomRight").put("text", "E")
          .put("fontWeight", "semibold"))))
    if (border) {
      row.getJSONObject("leading").put("borderColor", "#222222")
      row.put("style", JSONObject().put("leadingContentMaxWidthRatio", 0.6))
    }
    if (titleIcon) row.put("titleIcon", JSONObject().put("name", "GasSolid"))
    val trailing = JSONArray()
    if (kind == "button") trailing.put(JSONObject()
      .put("kind", "button").put("label", if (action == "hide") "Hide" else "Show")
      .put("actionKey", action).put("disabled", accessoryDisabled))
    if (kind == "icon") trailing.put(JSONObject()
      .put("kind", "icon").put("name", "EditOutline").put("actionKey", action)
      .also { if (tertiary) it.put("buttonStyle", "tertiary") })
    return row.put("trailing", trailing)
  }

  private fun bindRow(row: JSONObject) {
    generation += 1
    list.applySnapshot(JSONObject()
      .put("schemaVersion", 1).put("generation", generation)
      .put("layout", JSONObject().put("kind", "linear"))
      .put("rows", JSONArray().put(row)).toString())
  }

  private fun holder(): NativeListViewHolder =
    recycler.findViewHolderForAdapterPosition(0) as NativeListViewHolder

  private fun button(): TextView =
    field<NativeListAccessoryStack>(holder().rowView, "trailingColumn").visibleValues.single()

  private fun awaitRow(key: String, matches: (JSONObject) -> Boolean = { true }) {
    repeat(100) {
      var ready = false
      onMain {
        val holder = recycler.findViewHolderForAdapterPosition(0) as? NativeListViewHolder
        val current = holder?.rowView?.tag as? NativeListItem
        ready = !recycler.isLayoutRequested && current?.key == key && matches(current.json)
      }
      if (ready) return
      SystemClock.sleep(50)
    }
    fail("Native risk row did not bind: $key")
  }

  private fun awaitCondition(condition: () -> Boolean) {
    repeat(100) {
      if (onMainValue(condition)) return
      SystemClock.sleep(50)
    }
    fail("Native risk visual did not reach expected state")
  }

  private fun tap(view: View) {
    val center = onMainValue {
      val location = IntArray(2)
      view.getLocationOnScreen(location)
      Pair(location[0] + view.width / 2f, location[1] + view.height / 2f)
    }
    val downTime = SystemClock.uptimeMillis()
    instrumentation.sendPointerSync(MotionEvent.obtain(
      downTime, downTime, MotionEvent.ACTION_DOWN, center.first, center.second, 0))
    instrumentation.sendPointerSync(MotionEvent.obtain(
      downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, center.first, center.second, 0))
    instrumentation.waitForIdleSync()
  }

  private fun onMain(block: () -> Unit) {
    var failure: Throwable? = null
    instrumentation.runOnMainSync {
      try { block() } catch (error: Throwable) { failure = error }
    }
    failure?.let { throw it }
  }

  private fun <T> onMainValue(block: () -> T): T {
    var value: T? = null
    onMain { value = block() }
    @Suppress("UNCHECKED_CAST")
    return value as T
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> field(owner: Any, name: String): T {
    var type: Class<*>? = owner.javaClass
    while (type != null) {
      try {
        return type.getDeclaredField(name).apply { isAccessible = true }.get(owner) as T
      } catch (_: NoSuchFieldException) {
        type = type.superclass
      }
    }
    error("Missing production field $name")
  }
}
