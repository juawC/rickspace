package com.app.juawcevada.rickspace.ui.characterlist

import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.app.juawcevada.rickspace.model.Character

@BindingAdapter("setCharactersList")
fun setCharactersList(recyclerView: RecyclerView, charactersList: PagedList<Character>?) {
    (recyclerView.adapter as CharacterListAdapter).submitList(charactersList)
}