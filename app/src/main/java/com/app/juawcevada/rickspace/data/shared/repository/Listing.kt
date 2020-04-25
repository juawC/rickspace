package com.app.juawcevada.rickspace.data.shared.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class Listing<T>(
        // the LiveData of paged lists for the UI to observe
    val pagedList: LiveData<PagedList<T>>,
        // represents the network request status to show to the user
    val networkState: LiveData<Resource<Unit>>,
        // retry last request
    val retryAction: () -> Unit
)