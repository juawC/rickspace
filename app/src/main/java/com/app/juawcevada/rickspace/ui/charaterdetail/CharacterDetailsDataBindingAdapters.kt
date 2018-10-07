package com.app.juawcevada.rickspace.ui.charaterdetail

import com.app.juawcevada.rickspace.R
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("setEpisodesList")
fun setEpisodesList(recyclerView: RecyclerView, episodes: List<String>) {
    (recyclerView.adapter as CharacterEpisodesAdapter).submitList(episodes)
}

@BindingAdapter("setEpisodeName")
fun setEpisodeName(textView: TextView, episodeUrl: String) {
    val episodeNumber = episodeUrl.takeLastWhile { it != '/' }.toIntOrNull() ?: 0
    textView.text = textView.context.getString(R.string.episode_number, episodeNumber)
}