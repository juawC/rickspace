package com.app.juawcevada.rickspace.ui

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

/**
 * Glide module with glide configurations.
 *
 */
const val CACHE_MB = 100

@GlideModule
class GlideConfigModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(
                InternalCacheDiskCacheFactory(context, (1024 * 1024 * CACHE_MB).toLong()))
    }
}