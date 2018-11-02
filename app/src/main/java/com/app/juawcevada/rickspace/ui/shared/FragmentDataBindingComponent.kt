package com.app.juawcevada.rickspace.ui.shared

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import com.app.juawcevada.rickspace.testing.OpenClassOnDebug
import javax.inject.Inject

/**
 * Component to set fragment binding adapters.
 */

@OpenClassOnDebug class FragmentDataBindingComponent @Inject
constructor(private val fragment: Fragment) : DataBindingComponent {


    override fun getFragmentBindingAdapters(): FragmentBindingAdapters =
            FragmentBindingAdapters(fragment)

}