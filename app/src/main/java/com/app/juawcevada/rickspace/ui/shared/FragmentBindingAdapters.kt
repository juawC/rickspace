package com.app.juawcevada.rickspace.ui.shared

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug

/**
 * Fragment view binders.
 */

@OpenClassOnDebug
class FragmentBindingAdapters(private val fragment: Fragment) {

    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        GlideApp.with(fragment).load(url).centerCrop().into(imageView)
    }
}
