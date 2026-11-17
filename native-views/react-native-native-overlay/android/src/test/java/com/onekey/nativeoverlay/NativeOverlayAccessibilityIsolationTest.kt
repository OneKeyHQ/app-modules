package com.onekey.nativeoverlay

import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlayAccessibilityIsolationTest {
  private val context get() = RuntimeEnvironment.getApplication()

  private class Tree(context: android.content.Context) {
    val root = FrameLayout(context)
    val reactRoot = FrameLayout(context)
    val card = FrameLayout(context)
    val content = View(context).apply { importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES }
    val chrome = View(context)
    val pageHost = FrameLayout(context)
    val lower = View(context)
    val blocker = View(context)
    val above = View(context)
    val globalHost = FrameLayout(context)

    init {
      root.addView(reactRoot)
      root.addView(globalHost)
      reactRoot.addView(card)
      reactRoot.addView(chrome)
      card.addView(content)
      card.addView(pageHost)
      pageHost.addView(lower)
      pageHost.addView(blocker)
      pageHost.addView(above)
    }
  }

  @Test
  fun pageBlockerHidesPageAndNavigationWithoutHidingTheHost() {
    val tree = Tree(context)
    val isolation = NativeOverlayAccessibilityIsolation()
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, tree.content.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, tree.chrome.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.pageHost.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.blocker.importantForAccessibility)
  }

  @Test
  fun entriesAbovePageBlockerAndGlobalHostStayAccessible() {
    val tree = Tree(context)
    NativeOverlayAccessibilityIsolation().update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, tree.lower.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.above.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.globalHost.importantForAccessibility)
  }

  @Test
  fun dismissalOrSuspensionRestoresOriginalFlags() {
    val tree = Tree(context)
    tree.lower.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    val isolation = NativeOverlayAccessibilityIsolation()
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    isolation.update(tree.root, null, emptyList(), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, tree.content.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, tree.lower.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.chrome.importantForAccessibility)
  }

  @Test
  fun globalBlockerHidesReactRootThenPageIsolationReturns() {
    val tree = Tree(context)
    val globalLower = View(context)
    tree.globalHost.addView(globalLower)
    val isolation = NativeOverlayAccessibilityIsolation()
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    isolation.update(tree.root, tree.globalHost, listOf(globalLower), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, tree.reactRoot.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, globalLower.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, tree.content.importantForAccessibility)
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.reactRoot.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, globalLower.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, tree.content.importantForAccessibility)
  }

  @Test
  fun detachedHostRestoresIsolationInsteadOfHidingAnotherTree() {
    val tree = Tree(context)
    val isolation = NativeOverlayAccessibilityIsolation()
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    tree.card.removeView(tree.pageHost)
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, tree.content.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.lower.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.chrome.importantForAccessibility)
  }

  @Test
  fun changingTheBlockingEntryRestoresEntriesNoLongerBelowIt() {
    val tree = Tree(context)
    val isolation = NativeOverlayAccessibilityIsolation()
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower, tree.blocker), tree.globalHost)
    isolation.update(tree.root, tree.pageHost, listOf(tree.lower), tree.globalHost)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, tree.blocker.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, tree.lower.importantForAccessibility)
  }

  @Test
  fun lockBlockerIsolatesMessageToastLayerBothBeforeAndAfterLockOpens() {
    val localAccessor = Class.forName(
      "com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsLocalAccessor"
    ).getDeclaredConstructor().newInstance()
    ReactNativeFeatureFlags::class.java.getDeclaredField("accessor").apply {
      isAccessible = true
      set(null, localAccessor)
    }
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    reactContext.onHostResume(activity)
    val themedContext = ThemedReactContext(reactContext, activity, "ToastIsolationTest", 1)
    val host = NativeOverlayHost.of(activity)
    val toastLayer = FrameLayout(activity)
    host.attachToastLayer(toastLayer)
    val modal = NativeOverlayEntryRootView(themedContext).apply {
      blocking = true
      isShownForInput = true
    }
    host.attach(modal, NativeOverlayLevel.MODAL)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, toastLayer.importantForAccessibility)

    val lock = NativeOverlayEntryRootView(themedContext).apply {
      blocking = true
      isShownForInput = true
    }
    host.attach(lock, NativeOverlayLevel.LOCK)
    assertEquals(
      View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS,
      toastLayer.importantForAccessibility,
    )

    val secondToastLayer = FrameLayout(activity)
    host.detachToastLayer(toastLayer)
    host.attachToastLayer(secondToastLayer)
    assertEquals(
      View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS,
      secondToastLayer.importantForAccessibility,
    )

    host.detach(lock)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, secondToastLayer.importantForAccessibility)
  }

  @Test
  @Config(sdk = [24, 26, 28])
  fun toastFontAndPresenterSupportApi24Through28() {
    assertNotNull(ToastFont.make(context, null, emptyMap(), "400"))
    assertNotNull(ToastFont.make(context, null, emptyMap(), "700"))
    val hostView = View(context)
    val presenter = NativeOverlayToastPresenter(hostView)
    presenter.sync(
      ToastItem.parseList("""[{"spec":{"id":"a","layout":"message","duration":1000},"visible":true,"revision":0,"generation":0}]"""),
      ToasterConfig.parse("{}"),
    )
    presenter.invalidate()
  }

  @Test
  fun namedIconColorsNormalizeToStrictRgbaAcrossCssFormats() {
    val strictPattern = Regex("""^rgba\(\d{1,3},\d{1,3},\d{1,3},(?:0(?:\.\d+)?|1(?:\.0+)?)\)$""")
    val inputs = listOf(
      "#ff0000" to "rgba(255,0,0,1)",
      "#00ff0080" to "rgba(0,255,0,0.502)",
      "rgb(12, 34, 56)" to "rgba(12,34,56,1)",
      "rgba(255, 0, 128, 0.5)" to "rgba(255,0,128,0.502)",
      "hsl(120, 100%, 50%)" to "rgba(0,255,0,1)",
      "transparent" to "rgba(0,0,0,0)",
    )
    for ((raw, expected) in inputs) {
      val icon = ToastIcon.parse(
        JSONObject().put("kind", "icon").put("name", "DotVerSolid").put("color", raw).put("size", 20),
      ) as ToastIcon.Named
      assertEquals(expected, icon.colorString)
      assertTrue(strictPattern.matches(icon.colorString))
    }
  }

  @Test
  fun rebuildingOrInvalidatingToastCardCancelsPendingImageLoads() {
    val card = NativeOverlayToastCardView(context)
    val spec = ToastSpec.parse(
      JSONObject()
        .put("id", "img-toast")
        .put("layout", "notification")
        .put("duration", 5000)
        .put("icon", JSONObject().put("kind", "image").put("uri", "http://127.0.0.1:1/slow.png").put("size", 24))
        .put("image", JSONObject().put("uri", "http://127.0.0.1:1/thumb.png").put("size", 40)),
    )
    repeat(10) {
      card.apply(spec, 0xffffffff.toInt())
    }
    card.cancelLoads()
  }
}
