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
    private class HybridViewHolder(
        val hybridView: HybridScrollGuard,
        var lastState: StateWrapper? = null,
    )

    private val views = hashMapOf<View, HybridViewHolder>()

    override fun getName(): String = "ScrollGuard"

    override fun createViewInstance(reactContext: ThemedReactContext): ScrollGuardFrameLayout {
        val hybridView = HybridScrollGuard(reactContext)
        val view = hybridView.view as ScrollGuardFrameLayout
        views[view] = HybridViewHolder(hybridView)
        return view
    }

    override fun onDropViewInstance(view: ScrollGuardFrameLayout) {
        views[view]?.lastState = null
        super.onDropViewInstance(view)
        views.remove(view)
    }

    override fun prepareToRecycleView(
        reactContext: ThemedReactContext,
        view: ScrollGuardFrameLayout,
    ): ScrollGuardFrameLayout? {
        views[view]?.lastState = null
        return super.prepareToRecycleView(reactContext, view)
    }

    override fun updateState(
        view: ScrollGuardFrameLayout,
        props: ReactStylesDiffMap,
        stateWrapper: StateWrapper
    ): Any? {
        val holder = views[view]
            ?: throw Error("Couldn't find view $view in local views table!")
        val hybridView = holder.hybridView
        val oldState = holder.lastState
        val newState = stateWrapper

        hybridView.beforeUpdate()
        HybridScrollGuardStateUpdater.updateViewProps(hybridView, newState, oldState)
        hybridView.afterUpdate()
        holder.lastState = newState

        return super.updateState(view, props, newState)
    }
}
