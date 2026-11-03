package com.margelo.nitro.onekeyimage

import android.content.Context
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.ImageView
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.views.RecyclableView
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

class HybridOneKeyIcon(context: ThemedReactContext) : HybridOneKeyIconSpec(), RecyclableView {
  private val host = OneKeyIconHost(context)
  private val owner = Any()
  private var generation = 0L
  private var identity: String? = null
  private var displayedGeometry: String? = null
  private var displayed: OneKeyNamedSVG.RenderResult? = null
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
    OneKeyNamedSVG.cachedRender(name, color, host.width, host.height, scale)?.let { hit ->
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

  override fun prepareForRecycle() {
    iconName = null
    resolvedColor = null
    refresh()
  }
  override fun onDropView() { prepareForRecycle(); super.onDropView() }
  override fun dispose() { prepareForRecycle(); host.changed = null; super.dispose() }
}
