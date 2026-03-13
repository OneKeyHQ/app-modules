package com.margelo.nitro.scrollguard

import android.view.View
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.margelo.nitro.scrollguard.views.HybridScrollGuardStateUpdater

/**
 * Custom ViewGroupManager for ScrollGuard that supports child views.
 *
 * The nitrogen-generated HybridScrollGuardManager extends SimpleViewManager,
 * which does not implement IViewGroupManager. Since ScrollGuardFrameLayout
 * is a FrameLayout that wraps child scrollable views, we need a ViewGroupManager.
 */
class ScrollGuardViewGroupManager : ViewGroupManager<ScrollGuardFrameLayout>() {
    private val views = hashMapOf<View, HybridScrollGuard>()

    override fun getName(): String = "ScrollGuard"

    override fun createViewInstance(reactContext: ThemedReactContext): ScrollGuardFrameLayout {
        val hybridView = HybridScrollGuard(reactContext)
        val view = hybridView.view as ScrollGuardFrameLayout
        views[view] = hybridView
        return view
    }

    override fun onDropViewInstance(view: ScrollGuardFrameLayout) {
        super.onDropViewInstance(view)
        views.remove(view)
    }

    override fun updateState(
        view: ScrollGuardFrameLayout,
        props: ReactStylesDiffMap,
        stateWrapper: StateWrapper
    ): Any? {
        val hybridView = views[view]
            ?: throw Error("Couldn't find view $view in local views table!")

        hybridView.beforeUpdate()
        HybridScrollGuardStateUpdater.updateViewProps(hybridView, stateWrapper)
        hybridView.afterUpdate()

        return super.updateState(view, props, stateWrapper)
    }
}
