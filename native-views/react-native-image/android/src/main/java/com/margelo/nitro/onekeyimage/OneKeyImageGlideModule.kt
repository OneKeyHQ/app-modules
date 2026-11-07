package com.margelo.nitro.onekeyimage

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.GlideModule

@Suppress("DEPRECATION")
class OneKeyImageGlideModule : GlideModule {
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    builder.setDiskCache(InternalCacheDiskCacheFactory(context, 320L * 1024 * 1024))
  }

  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {}
}
