package com.margelo.nitro.reactnativesplashscreen

import android.app.Activity
import androidx.core.content.ContextCompat
import com.margelo.nitro.nativelogger.OneKeyLog
import java.util.WeakHashMap

/**
 * Static bridge called by the host app's MainActivity to show/manage the splash screen.
 *
 * Usage in MainActivity.onCreate() (for API < 31 only):
 *   SplashScreenBridge.show(this)
 */
object SplashScreenBridge {
    private const val TAG = "SplashScreen"

    private val controllers = WeakHashMap<Activity, SplashViewController>()

    @Volatile
    var isAlreadyHidden = false

    /**
     * Show the splash screen overlay. Call this from MainActivity.onCreate()
     * for API < Build.VERSION_CODES.S (API 31).
     */
    @JvmStatic
    fun show(activity: Activity) {
        if (isAlreadyHidden) return
        if (controllers.containsKey(activity)) {
            OneKeyLog.warn(TAG, "show: already shown for this activity")
            return
        }
        val splashView = createSplashView(activity) ?: run {
            OneKeyLog.warn(TAG, "show: failed to create splash view")
            return
        }
        val controller = SplashViewController(activity, splashView)
        controllers[activity] = controller
        controller.show()
        OneKeyLog.info(TAG, "show: started for activity")
    }

    @JvmStatic
    fun preventAutoHide(activity: Activity) {
        val controller = controllers[activity]
        if (controller == null) {
            OneKeyLog.warn(TAG, "preventAutoHide: no controller for activity")
            return
        }
        controller.preventAutoHide()
    }

    @JvmStatic
    fun hide(
        activity: Activity,
        onSuccess: ((Boolean) -> Unit)?,
        onFailure: ((String) -> Unit)?
    ) {
        isAlreadyHidden = true
        val controller = controllers[activity]
        if (controller == null) {
            OneKeyLog.warn(TAG, "hide: no controller for activity")
            onFailure?.invoke("No splash screen registered for this activity")
            return
        }
        controller.hide(onSuccess, onFailure)
    }

    private fun createSplashView(activity: Activity): android.view.View? {
        return try {
            val context = activity

            // Read resize mode from app string resource (convention: "expo_splash_screen_resize_mode")
            val resizeModeStr = try {
                val id = context.resources.getIdentifier(
                    "expo_splash_screen_resize_mode", "string", context.packageName
                )
                if (id != 0) context.getString(id).lowercase() else "contain"
            } catch (e: Exception) { "contain" }

            val resizeMode = SplashImageResizeMode.fromString(resizeModeStr)

            // For NATIVE mode use "splashscreen", otherwise use "splashscreen_image"
            val drawableName = if (resizeMode == SplashImageResizeMode.NATIVE) "splashscreen" else "splashscreen_image"
            val imageResId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            val bgColorId = context.resources.getIdentifier(
                "splashscreen_background", "color", context.packageName
            )

            OneKeyLog.info(TAG, "createSplashView: resizeMode=$resizeModeStr drawable=$drawableName imageResId=$imageResId")

            val view = SplashScreenView(context)
            if (bgColorId != 0) {
                view.setBackgroundColor(ContextCompat.getColor(context, bgColorId))
            } else {
                view.setBackgroundColor(android.graphics.Color.WHITE)
            }
            if (imageResId != 0) {
                view.imageView.setImageResource(imageResId)
            }
            view.configureResizeMode(resizeMode)
            view
        } catch (e: Exception) {
            OneKeyLog.error(TAG, "createSplashView failed: ${e.message}")
            null
        }
    }
}
