///
/// HybridTabViewManager.kt
/// Custom ViewGroupManager that replaces the nitrogen-generated SimpleViewManager.
/// Adds child view management support for tab content views.
///

package com.margelo.nitro.tabview.views

import android.view.View
import android.view.ViewGroup
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.margelo.nitro.tabview.*
import com.rcttabview.ReactBottomNavigationView

/**
 * Custom ViewGroupManager for the "TabView" Nitro HybridView.
 * Extends ViewGroupManager (instead of SimpleViewManager) to support React child views.
 */
open class HybridTabViewManager: ViewGroupManager<ViewGroup>() {
  private val views = hashMapOf<View, HybridTabView>()

  override fun getName(): String {
    return "TabView"
  }

  override fun createViewInstance(reactContext: ThemedReactContext): ViewGroup {
    val hybridView = HybridTabView(reactContext)
    val view = hybridView.view
    views[view] = hybridView
    return view as ViewGroup
  }

  override fun onDropViewInstance(view: ViewGroup) {
    super.onDropViewInstance(view)
    val hybridView = views.remove(view)
    hybridView?.onDropViewInstance()
  }

  override fun updateState(view: ViewGroup, props: ReactStylesDiffMap, stateWrapper: StateWrapper): Any? {
    val hybridView = views[view] ?: throw Error("Couldn't find view $view in local views table!")

    hybridView.beforeUpdate()
    HybridTabViewStateUpdater.updateViewProps(hybridView, stateWrapper)
    hybridView.afterUpdate()

    return super.updateState(view, props, stateWrapper)
  }

  // Child view management delegates to ReactBottomNavigationView
  // which already handles addView/removeView internally

  override fun getChildCount(parent: ViewGroup): Int {
    val bottomNav = parent as? ReactBottomNavigationView ?: return 0
    return bottomNav.layoutHolder.childCount
  }

  override fun getChildAt(parent: ViewGroup, index: Int): View? {
    val bottomNav = parent as? ReactBottomNavigationView ?: return null
    return bottomNav.layoutHolder.getChildAt(index)
  }

  override fun removeView(parent: ViewGroup, view: View) {
    val bottomNav = parent as? ReactBottomNavigationView ?: return
    bottomNav.layoutHolder.removeView(view)
  }

  override fun removeAllViews(parent: ViewGroup) {
    val bottomNav = parent as? ReactBottomNavigationView ?: return
    bottomNav.layoutHolder.removeAllViews()
  }

  override fun removeViewAt(parent: ViewGroup, index: Int) {
    val bottomNav = parent as? ReactBottomNavigationView ?: return
    bottomNav.layoutHolder.removeViewAt(index)
  }

  override fun needsCustomLayoutForChildren(): Boolean {
    return true
  }
}
