package com.onekey.nativeoverlay

import android.view.View
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
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
}
