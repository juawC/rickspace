package com.app.juawcevada.rickspace.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.juawcevada.rickspace.event.Event
import com.app.juawcevada.rickspace.event.EventObserver
import com.app.juawcevada.rickspace.ui.shared.SnackbarMessage
import com.google.android.material.snackbar.Snackbar

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
        provider: ViewModelProvider.Factory
): VM {
    return ViewModelProviders.of(this, provider).get(VM::class.java)
}

fun Fragment.setUpSnackbar(
        snackbarMessage: LiveData<Event<SnackbarMessage>>,
        view: View
) {
    snackbarMessage.observe(this, EventObserver {
        Snackbar.make(view, it.messageId, it.duration).show()
    })

}

