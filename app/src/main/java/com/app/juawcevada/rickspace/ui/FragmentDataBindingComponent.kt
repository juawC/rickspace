package com.app.juawcevada.rickspace.ui

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import javax.inject.Inject

/**
 * Component to set fragment binding adapters.
 */

class FragmentDataBindingComponent @Inject
constructor(private val fragment: Fragment) : DataBindingComponent {


    override fun getFragmentBindingAdapters(): FragmentBindingAdapters =
            FragmentBindingAdapters(fragment)

}