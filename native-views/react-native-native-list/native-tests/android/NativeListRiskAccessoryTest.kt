package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.MetricAffectingSpan
import android.text.TextPaint
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

  fun testValuePairSegmentsRebindAndSemanticStyleOnProductionStack() {
    onMain {
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "PairTest", 1)
      val stack = NativeListAccessoryStack(context, NativeListFonts())
      activity.setContentView(stack)
      val item = NativeListItem("pair", "identity", null, 0,
        JSONObject().put("key", "pair").put("type", "identity").put("title", "Pair"))
      fun bind(primary: JSONArray?, secondary: JSONArray?, size: Int? = null) {
        val pair = JSONObject().put("kind", "valuePair")
          .put("primary", "0.001").put("secondary", "\$0.01")
        if (primary != null) pair.put("primaryTextSegments", primary)
        if (secondary != null) pair.put("secondaryTextSegments", secondary)
        val style = JSONObject()
        if (size != null) style.put("value", JSONObject().put("fontSize", size))
        stack.bind(item, JSONArray().put(pair), null, style, false,
          { _, _, fallback -> fallback })
      }
      val primary = JSONArray().put(JSONObject().put("text", "0.00"))
        .put(JSONObject().put("text", "1").put("style", "subscript"))
      val secondary = JSONArray().put(JSONObject().put("text", "\$0.0"))
        .put(JSONObject().put("text", "1").put("style", "subscript"))
      bind(primary, secondary)
      val view = stack.visibleValues.single()
      fun effectiveSize(position: Int): Float {
        val rich = view.text as Spanned
        val paint = TextPaint(view.paint).apply { density = activity.resources.displayMetrics.density }
        rich.getSpans(position, position + 1, MetricAffectingSpan::class.java)
          .forEach { it.updateMeasureState(paint) }
        return paint.textSize
      }
      assertEquals("0.001\n\$0.01", view.text.toString())
      val rich = view.text as Spanned
      assertTrue(effectiveSize(4) < effectiveSize(0))
      assertTrue(effectiveSize(10) < effectiveSize(6))
      assertEquals(0.6f, effectiveSize(4) / effectiveSize(0), 0.12f)
      assertEquals(0.6f, effectiveSize(10) / effectiveSize(6), 0.12f)
      bind(primary, secondary, 20)
      val styled = stack.visibleValues.single().text as Spanned
      assertEquals("0.001\n\$0.01", styled.toString())
      assertEquals(0.6f, effectiveSize(4) / effectiveSize(0), 0.12f)
      assertEquals(0.6f, effectiveSize(10) / effectiveSize(6), 0.12f)
      bind(null, null)
      val plain = stack.visibleValues.single().text as Spanned
      assertEquals("0.001\n\$0.01", plain.toString())
      assertTrue(plain.getSpans(0, plain.length, AbsoluteSizeSpan::class.java).size <= 2)
      assertTrue(effectiveSize(4) >= effectiveSize(0) * 0.9f)
      bind(JSONArray(), JSONArray())
      assertEquals("0.001\n\$0.01", stack.visibleValues.single().text.toString())
      assertFalse(stack.visibleValues.single().isClickable)
    }
  }

  fun testOverviewAccessoryPairsUseHorizontalProductionIdentityCells() {
    onMain {
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "OverviewTest", 1)
      val identity = NativeListIdentityRowView(context, NativeListFonts())
      activity.setContentView(identity)
      fun bind(trailing: JSONArray, height: Int, subtitle: String? = "100.0%") {
        val json = JSONObject().put("key", "overview").put("type", "identity")
          .put("height", height).put("title", "Ethereum")
          .put("leading", JSONObject().put("kind", "network")
            .put("fallbackIcon", JSONObject().put("name", "GlobusOutline")))
          .put("trailing", trailing)
          .put("style", JSONObject().put("trailingGap", 12)
            .put("horizontalPadding", 12).put("verticalPadding", 8)
            .put("leadingGap", 12)
            .put("image", JSONObject().put("width", 32).put("height", 32)))
        if (subtitle != null) json.put("subtitle", subtitle)
        identity.bind(NativeListItem("overview", "identity", null, 0, json), null,
          "linear", "vertical", 0, false, { _, _, fallback -> fallback }, false)
        identity.measure(View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(NativeListScale.dp(activity.resources, height),
            View.MeasureSpec.EXACTLY))
        identity.layout(0, 0, 1080, identity.measuredHeight)
      }
      val pair = JSONObject().put("kind", "valuePair").put("primary", "1.0193B")
        .put("secondary", "\$1,018,849,363.70")
      val chevron = JSONObject().put("kind", "chevron")
      bind(JSONArray().put(pair).put(chevron), 60)
      val trailing = field<NativeListAccessoryStack>(identity, "trailingColumn")
      val value = trailing.visibleValues.single()
      val arrow = trailing.firstVisibleIcon!!
      assertEquals(LinearLayout.HORIZONTAL, trailing.orientation)
      assertEquals("1.0193B\n\$1,018,849,363.70", value.text.toString())
      assertTrue(value.top >= 0 && value.bottom <= trailing.height)
      assertTrue(arrow.top >= 0 && arrow.bottom <= trailing.height)
      assertTrue(trailing.top + value.top >= 0 && trailing.top + value.bottom <= identity.height)
      assertTrue(trailing.top + arrow.top >= 0 && trailing.top + arrow.bottom <= identity.height)
      assertTrue(value.lineCount >= 2)
      assertTrue(kotlin.math.abs(identity.width - identity.paddingRight -
        trailing.left - arrow.right) <= NativeListScale.dp(activity.resources, 1))
      assertTrue(kotlin.math.abs(NativeListScale.dp(activity.resources, 12) -
        arrow.left + value.right) <= NativeListScale.dp(activity.resources, 1))
      assertTrue(kotlin.math.abs((value.top + value.bottom) - (arrow.top + arrow.bottom)) <=
        NativeListScale.dp(activity.resources, 4))
      val refresh = JSONObject().put("kind", "icon").put("name", "RefreshCcwOutline")
      bind(JSONArray().put(refresh).put(chevron), 48, subtitle = null)
      val icons = (0 until trailing.childCount).map(trailing::getChildAt)
        .filterIsInstance<OneKeyIconView>().filter { it.visibility == View.VISIBLE }
      assertEquals(LinearLayout.HORIZONTAL, trailing.orientation)
      assertEquals(2, icons.size)
      assertEquals(NativeListScale.dp(activity.resources, 16), icons[0].width)
      assertEquals(NativeListScale.dp(activity.resources, 24), icons[1].width)
      assertFalse(icons[0].isClickable)
      assertTrue(trailing.top + icons[0].top >= 0 &&
        trailing.top + icons[0].bottom <= identity.height)
      assertTrue(trailing.top + icons[1].top >= 0 &&
        trailing.top + icons[1].bottom <= identity.height)
      assertTrue(kotlin.math.abs(identity.width - identity.paddingRight -
        trailing.left - icons[1].right) <= NativeListScale.dp(activity.resources, 1))
      assertTrue(kotlin.math.abs(NativeListScale.dp(activity.resources, 12) -
        icons[1].left + icons[0].right) <= NativeListScale.dp(activity.resources, 1))
      assertTrue(kotlin.math.abs((icons[0].top + icons[0].bottom) -
        (icons[1].top + icons[1].bottom)) <= NativeListScale.dp(activity.resources, 4))
      bind(JSONArray().put(pair).put(chevron), 60)
      assertEquals(LinearLayout.HORIZONTAL, trailing.orientation)
      assertEquals("1.0193B\n\$1,018,849,363.70", trailing.visibleValues.single().text.toString())
    }
  }

  fun testDecorativeRefreshKeepsSixteenDpFrameAndResetsForActionIcon() {
    onMain {
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "RefreshTest", 1)
      val stack = NativeListAccessoryStack(context, NativeListFonts())
      activity.setContentView(stack)
      val item = NativeListItem("refresh", "identity", null, 0,
        JSONObject().put("key", "refresh").put("type", "identity").put("title", "Network"))
      fun bind(actionKey: String? = null) {
        val descriptor = JSONObject().put("kind", "icon").put("name", "RefreshCcwOutline")
        if (actionKey != null) descriptor.put("actionKey", actionKey)
        stack.bind(item, JSONArray().put(descriptor), null, JSONObject(), false,
          { _, _, fallback -> fallback })
      }
      bind()
      val icon = stack.firstVisibleIcon as OneKeyIconView
      assertEquals(NativeListScale.dp(activity.resources, 16), icon.layoutParams.width)
      assertEquals(NativeListScale.dp(activity.resources, 16), icon.layoutParams.height)
      assertEquals(16, icon.glyphSizeDp)
      assertEquals(0, (icon.layoutParams as LinearLayout.LayoutParams).marginStart)
      icon.layout(0, 0, icon.layoutParams.width, icon.layoutParams.height)
      assertTrue(paintedPixelCount(icon) > 0)
      bind("refresh")
      assertEquals(NativeListScale.dp(activity.resources, 36), icon.layoutParams.width)
      assertEquals(24, icon.glyphSizeDp)
      bind()
      assertEquals(NativeListScale.dp(activity.resources, 16), icon.layoutParams.width)
      assertEquals(16, icon.glyphSizeDp)
      assertFalse(icon.isClickable)
    }
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

  fun testPaddedSquareNetworkOverlayRoundsInnerImageAndResetsAcrossSources() {
    val imageFile = File(activity.cacheDir, "native-list-overlay-circle.png")
    val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply {
      eraseColor(Color.RED)
    }
    imageFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bitmap.recycle()
    val leading = onMainValue {
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "OverlayTest", 1)
      NativeListLeadingVisual(context, NativeListFonts())
    }
    val leadingSize = NativeListScale.dp(activity.resources, 40)
    fun bind(overlay: JSONObject) {
      onMain {
        leading.bind(JSONObject().put("kind", "token")
          .put("overlays", JSONArray().put(overlay)), JSONObject(), "overlay", null,
          false, false)
        leading.measure(View.MeasureSpec.makeMeasureSpec(leadingSize, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(leadingSize, View.MeasureSpec.EXACTLY))
        leading.layout(0, 0, leadingSize, leadingSize)
      }
    }
    onMain { activity.setContentView(leading) }
    fun innerHost(): View {
      val slot = field<MutableList<NativeListImageSlot>>(leading, "overlaySlots").first()
      val image = field<Any>(slot.view, "image")
      return field(image, "hostView")
    }
    fun imageOverlay(uri: String, width: Int = 20, height: Int = 20) =
      JSONObject().put("position", "bottomRight").put("width", width).put("height", height)
        .put("padding", 2).put("image", JSONObject().put("uri", uri))
        .put("fallbackIcon", JSONObject().put("name", "GlobusOutline"))
    val successUri = Uri.fromFile(imageFile).toString()
    bind(imageOverlay(successUri))
    awaitCondition {
      val slot = field<MutableList<NativeListImageSlot>>(leading, "overlaySlots").first()
      innerHost().clipToOutline && field<Boolean?>(slot, "result") == true
    }
    onMain {
      val slot = field<MutableList<NativeListImageSlot>>(leading, "overlaySlots").first()
      assertTrue(kotlin.math.abs(NativeListScale.dp(activity.resources, 16) -
        slot.view.width) <= 1)
    }
    bind(imageOverlay(successUri, width = 20, height = 14))
    onMain { assertFalse(innerHost().clipToOutline) }
    bind(JSONObject().put("position", "bottomRight").put("size", 20)
      .put("padding", 2).put("text", "E"))
    onMain { assertFalse(innerHost().clipToOutline) }
    bind(imageOverlay("file:///missing/overlay-${SystemClock.uptimeMillis()}.png"))
    awaitCondition {
      field<MutableList<OneKeyIconView>>(leading, "overlayFallbackIcons")
        .first().visibility == View.VISIBLE
    }
    bind(imageOverlay(successUri))
    awaitCondition {
      val slot = field<MutableList<NativeListImageSlot>>(leading, "overlaySlots").first()
      innerHost().clipToOutline && field<Boolean?>(slot, "result") == true &&
        field<MutableList<OneKeyIconView>>(leading, "overlayFallbackIcons")
          .first().visibility == View.GONE
    }
    onMain { leading.recycle() }
    imageFile.delete()
  }

  fun testIdentityCellDrawsOutsetNetworkOverlayBeyondAvatarAndResetsClip() {
    val imageFile = File(activity.cacheDir, "native-list-overlay-outset.png")
    val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply {
      eraseColor(Color.BLUE)
    }
    imageFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bitmap.recycle()
    val identity = onMainValue {
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "OutsetTest", 1)
      NativeListIdentityRowView(context, NativeListFonts()).also(activity::setContentView)
    }
    val rowWidth = NativeListScale.dp(activity.resources, 360)
    val rowHeight = NativeListScale.dp(activity.resources, 60)
    fun bind(withOverlay: Boolean) {
      onMain {
        val leading = JSONObject().put("kind", "token")
          .put("backgroundColor", "#FF0000")
        if (withOverlay) leading.put("overlays", JSONArray().put(JSONObject()
          .put("position", "bottomRight").put("size", 20).put("padding", 2)
          .put("offset", 4).put("backgroundColor", "#00FF00")
          .put("image", JSONObject().put("uri", Uri.fromFile(imageFile).toString()))))
        val row = JSONObject().put("key", "outset").put("type", "identity")
          .put("title", "Token").put("height", 60).put("leading", leading)
          .put("style", JSONObject().put("image", JSONObject()
            .put("width", 40).put("height", 40)))
        identity.bind(NativeListItem("outset", "identity", null, 0, row), null,
          "linear", "vertical", 0, false, { _, _, fallback -> fallback }, false)
        identity.measure(View.MeasureSpec.makeMeasureSpec(rowWidth, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(rowHeight, View.MeasureSpec.EXACTLY))
        identity.layout(0, 0, rowWidth, rowHeight)
      }
    }
    try {
      bind(true)
      awaitCondition {
        val leading = field<NativeListLeadingVisual>(identity, "leadingFrame")
        val slot = field<MutableList<NativeListImageSlot>>(leading, "overlaySlots").first()
        field<Boolean?>(slot, "result") == true
      }
      onMain {
        val leading = field<NativeListLeadingVisual>(identity, "leadingFrame")
        assertFalse(identity.clipChildren)
        assertFalse(identity.clipToPadding)
        val rowImage = Bitmap.createBitmap(identity.width, identity.height,
          Bitmap.Config.ARGB_8888)
        identity.draw(Canvas(rowImage))
        val center = NativeListScale.dp(activity.resources, 34)
        val rightRing = (1..4).map { distance ->
          rowImage.getPixel(leading.right + NativeListScale.dp(activity.resources, distance),
            leading.top + center)
        }
        val bottomRing = (1..4).map { distance ->
          rowImage.getPixel(leading.left + center,
            leading.bottom + NativeListScale.dp(activity.resources, distance))
        }
        val frame = field<MutableList<Pair<android.widget.FrameLayout, JSONObject>>>(leading,
          "overlays").first().first
        val imageView = frame.getChildAt(0)
        assertTrue("right outer ring: $rightRing; frame=${frame.left},${frame.top}," +
          "${frame.right},${frame.bottom}; child=${imageView.left},${imageView.top}," +
          "${imageView.right},${imageView.bottom}; padding=${frame.paddingLeft}," +
          "${frame.paddingTop}; color=${(frame.background as GradientDrawable).color?.defaultColor}",
          rightRing.any {
          Color.alpha(it) > 128 && Color.green(it) > Color.blue(it)
        })
        assertTrue("bottom outer ring: $bottomRing", bottomRing.any {
          Color.alpha(it) > 128 && Color.green(it) > Color.blue(it)
        })
        rowImage.recycle()
      }
      bind(false)
      onMain {
        assertTrue(identity.clipChildren)
        assertTrue(identity.clipToPadding)
      }
    } finally {
      onMain { activity.setContentView(View(activity)) }
      imageFile.delete()
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
