package com.margelo.nitro.scrollguard

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.absoluteValue

@DoNotStrip
class HybridScrollGuard(val context: ThemedReactContext) : HybridScrollGuardSpec() {

    override val view: View = ScrollGuardFrameLayout(context)

    override var direction: ScrollGuardDirection? = ScrollGuardDirection.HORIZONTAL
        set(value) {
            field = value
            (view as ScrollGuardFrameLayout).guardDirection = value ?: ScrollGuardDirection.HORIZONTAL
        }
}

/**
 * A native FrameLayout that prevents ancestor ViewPager2 (PagerView) from
 * intercepting touch events that belong to child scrollable views.
 *
 * Mechanism: On ACTION_DOWN, immediately calls requestDisallowInterceptTouchEvent(true)
 * to prevent all ancestors from intercepting. On ACTION_MOVE, checks the gesture
 * direction and only keeps blocking for the guarded direction. Vertical gestures
 * are released so the page can still scroll vertically.
 */
class ScrollGuardFrameLayout(context: Context) : FrameLayout(context) {

    var guardDirection: ScrollGuardDirection = ScrollGuardDirection.HORIZONTAL

    private var initialX = 0f
    private var initialY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var directionDecided = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
                directionDecided = false
                // Immediately prevent all ancestors (including ViewPager2) from intercepting.
                // This is critical: ViewPager2's onInterceptTouchEvent runs in the same
                // touch dispatch pass, so we must disallow BEFORE it gets a chance to decide.
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!directionDecided) {
                    val dx = (ev.x - initialX).absoluteValue
                    val dy = (ev.y - initialY).absoluteValue

                    if (dx > touchSlop || dy > touchSlop) {
                        directionDecided = true
                        val isHorizontalGesture = dx > dy

                        val shouldBlock = when (guardDirection) {
                            ScrollGuardDirection.HORIZONTAL -> isHorizontalGesture
                            ScrollGuardDirection.VERTICAL -> !isHorizontalGesture
                            ScrollGuardDirection.BOTH -> true
                        }

                        if (shouldBlock) {
                            // Guarded direction: keep blocking ancestors
                            parent?.requestDisallowInterceptTouchEvent(true)
                        } else {
                            // Non-guarded direction: release to ancestors
                            // (e.g., allow vertical scrolling of collapsible tabs)
                            parent?.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                directionDecided = false
            }
        }
        // Never intercept: let children (ScrollView/FlatList) handle touches normally
        return false
    }

    /**
     * Re-assert disallow after child dispatch.
     *
     * This handles the case where a deeply-nested NestedScrollableHost (from another
     * PagerView) calls requestDisallowInterceptTouchEvent(false) during its own
     * dispatchTouchEvent, which would propagate up and override our earlier disallow.
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val handled = super.dispatchTouchEvent(ev)

        if (ev.action == MotionEvent.ACTION_MOVE && directionDecided) {
            val dx = (ev.x - initialX).absoluteValue
            val dy = (ev.y - initialY).absoluteValue
            val isHorizontalGesture = dx > dy

            val shouldBlock = when (guardDirection) {
                ScrollGuardDirection.HORIZONTAL -> isHorizontalGesture
                ScrollGuardDirection.VERTICAL -> !isHorizontalGesture
                ScrollGuardDirection.BOTH -> true
            }

            if (shouldBlock) {
                // Re-assert after child dispatch to prevent override
                parent?.requestDisallowInterceptTouchEvent(true)
            }
        }

        return handled
    }
}
