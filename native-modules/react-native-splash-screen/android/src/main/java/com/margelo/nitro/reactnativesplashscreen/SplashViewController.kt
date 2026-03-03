package com.margelo.nitro.reactnativesplashscreen

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.margelo.nitro.nativelogger.OneKeyLog
import java.lang.ref.WeakReference

internal class SplashViewController(
    activity: Activity,
    private val splashView: View
) {
    private companion object {
        private const val TAG = "SplashScreen"
        private const val SEARCH_INTERVAL_MS = 20L

        // Look up ReactRootView class at runtime to avoid compile-time dependency
        private val reactRootViewClass: Class<*>? by lazy {
            try {
                Class.forName("com.facebook.react.ReactRootView")
            } catch (e: ClassNotFoundException) {
                null
            }
        }
    }

    private val weakActivity = WeakReference(activity)
    private val contentView: ViewGroup = activity.findViewById(android.R.id.content)
    private val handler = Handler(Looper.getMainLooper())

    private var autoHideEnabled = true
    private var splashShown = false
    private var searchCancelled = false

    fun show() {
        val activity = weakActivity.get() ?: return
        activity.runOnUiThread {
            val parent = splashView.parent as? ViewGroup
            parent?.removeView(splashView)
            contentView.addView(splashView)
            splashShown = true
            OneKeyLog.info(TAG, "splash view added to content")
            searchForRootView()
        }
    }

    fun preventAutoHide() {
        autoHideEnabled = false
        OneKeyLog.info(TAG, "autoHide disabled")
    }

    fun hide(
        onSuccess: ((Boolean) -> Unit)?,
        onFailure: ((String) -> Unit)?
    ) {
        if (!splashShown) {
            OneKeyLog.info(TAG, "hide: splash not shown, skipping")
            onSuccess?.invoke(false)
            return
        }
        val activity = weakActivity.get()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            onFailure?.invoke("Activity is already destroyed")
            return
        }
        searchCancelled = true
        handler.removeCallbacksAndMessages(null)
        Handler(activity.mainLooper).post {
            contentView.removeView(splashView)
            autoHideEnabled = true
            splashShown = false
            OneKeyLog.info(TAG, "splash view removed")
            onSuccess?.invoke(true)
        }
    }

    private fun searchForRootView() {
        if (searchCancelled) return
        val activity = weakActivity.get()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            OneKeyLog.warn(TAG, "searchForRootView: activity destroyed, stopping search")
            return
        }
        if (!splashShown) return
        val found = findRootView(contentView)
        if (found != null) {
            handleRootView(found)
            return
        }
        handler.postDelayed(::searchForRootView, SEARCH_INTERVAL_MS)
    }

    private fun findRootView(view: View): ViewGroup? {
        val cls = reactRootViewClass
        if (cls != null && cls.isInstance(view)) {
            return view as? ViewGroup
        }
        if (view !== splashView && view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findRootView(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }

    private fun handleRootView(rootView: ViewGroup) {
        if (rootView.childCount > 0 && autoHideEnabled) {
            hide(null, null)
        }
        rootView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                if (rootView.childCount == 1 && autoHideEnabled) {
                    hide(null, null)
                }
            }

            override fun onChildViewRemoved(parent: View, child: View) {
                if (rootView.childCount == 0) {
                    show()
                }
            }
        })
    }
}
