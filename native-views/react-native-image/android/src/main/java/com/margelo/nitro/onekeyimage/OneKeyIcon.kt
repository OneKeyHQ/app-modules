package com.margelo.nitro.onekeyimage

import android.content.Context
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.ImageView
import com.facebook.react.uimanager.ThemedReactContext
import java.lang.ref.WeakReference

private class OneKeyIconHost(context: Context) : ImageView(context) {
  var changed: (() -> Unit)? = null
  var attached = false
    private set
  init { scaleType = ScaleType.FIT_CENTER }
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    changed?.invoke()
  }
  override fun onAttachedToWindow() { super.onAttachedToWindow(); attached = true; changed?.invoke() }
  override fun onDetachedFromWindow() { attached = false; super.onDetachedFromWindow(); changed?.invoke() }
  override fun onConfigurationChanged(newConfig: Configuration) { super.onConfigurationChanged(newConfig); changed?.invoke() }
}

// Not a RecyclableView: Fabric's recycle pool zeroes the bounds of a dropped view
// while ScreenStack can still draw it in the outgoing screen transition.
class HybridOneKeyIcon(context: ThemedReactContext) : HybridOneKeyIconSpec() {
  private val host = OneKeyIconHost(context)
  private val owner = Any()
  private var generation = 0L
  private var identity: String? = null
  private var displayedGeometry: String? = null
  private var displayed: OneKeyNamedSVG.RenderResult? = null
  private var dropped = false
  override val view: ImageView get() = host
  override var iconName: String? = null
  override var resolvedColor: String? = null
  init { host.changed = { refresh() } }

  override fun afterUpdate() { super.afterUpdate(); refresh() }

  private fun clearImage() {
    displayed = null; displayedGeometry = null
    host.colorFilter = null; host.setImageDrawable(null)
  }

  private fun applyTint(result: OneKeyNamedSVG.RenderResult, color: String) {
    host.colorFilter = result.tintColor(color)?.let { PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN) }
  }

  private fun refresh() {
    if (dropped) {
      // Keep the last frame while a transition still draws the dropped host.
      if (host.attached) return
      dropped = false
      prepareForRecycle()
      return
    }
    val scale = host.resources.displayMetrics.density
    val next = "$iconName|$resolvedColor|${host.width}|${host.height}|$scale|${host.attached}"
    if (identity == next) return
    identity = next; generation++
    val revision = generation
    OneKeyNamedSVG.cancel(owner)
    val name = iconName
    val color = resolvedColor
    if (!host.attached || host.width !in 1..4096 || host.height !in 1..4096 ||
      host.width.toLong() * host.height > 4194304 || name.isNullOrEmpty() || color == null ||
      !OneKeyNamedSVG.isValidColor(color)) { clearImage(); return }
    val geometry = "$name|${host.width}|${host.height}|$scale"
    val ready = displayed
    if (displayedGeometry == geometry && ready != null && host.drawable != null) {
      if (ready.canTint(color)) { applyTint(ready, color); return }
    } else clearImage()
    val appContext = host.context.applicationContext ?: host.context
    OneKeyNamedSVG.setCacheDirIfNeeded(appContext.cacheDir)
    val hit = OneKeyNamedSVG.cachedRender(name, color, host.width, host.height, scale)
      ?: OneKeyNamedSVG.diskRender(appContext.assets, name, color, host.width, host.height, scale)
    if (hit != null) {
      displayed = hit; displayedGeometry = geometry
      applyTint(hit, color)
      host.setImageBitmap(hit.bitmap)
      return
    }
    val weakHost = WeakReference(host)
    val weakSelf = WeakReference(this)
    OneKeyNamedSVG.requestRender(owner, host.context.applicationContext.assets, name, color,
      host.width, host.height, scale) { result ->
      val current = weakSelf.get()
      val target = weakHost.get()
      if (current?.generation != revision || target?.attached != true) return@requestRender
      if (result == null) { current.clearImage(); return@requestRender }
      current.displayed = result; current.displayedGeometry = geometry
      current.applyTint(result, color)
      target.setImageBitmap(result.bitmap)
    }
  }

  /** Clears immediately, for native owners that reuse the view themselves. */
  fun prepareForRecycle() {
    iconName = null
    resolvedColor = null
    refresh()
  }

  private fun drop() {
    if (dropped) return
    if (host.attached && host.drawable != null) {
      dropped = true
      generation++
      OneKeyNamedSVG.cancel(owner)
    } else {
      prepareForRecycle()
    }
  }

  override fun onDropView() { drop(); super.onDropView() }
  override fun dispose() { drop(); super.dispose() }
}
