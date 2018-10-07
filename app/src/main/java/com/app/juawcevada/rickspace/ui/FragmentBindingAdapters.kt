package com.app.juawcevada.rickspace.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment

/**
 * Fragment view binders.
 */

class FragmentBindingAdapters(private val fragment: Fragment) {

    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        GlideApp.with(fragment).load(url).centerCrop().into(imageView)
    }
}
