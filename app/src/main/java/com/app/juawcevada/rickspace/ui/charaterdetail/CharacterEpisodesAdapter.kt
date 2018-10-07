package com.app.juawcevada.rickspace.ui.charaterdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.databinding.EpisodeItemBinding
import com.app.juawcevada.rickspace.ui.ListRecyclerAdapter

class CharacterEpisodesAdapter: ListRecyclerAdapter<String,EpisodeItemBinding>() {

    override val differ: AsyncListDiffer<String> =
            AsyncListDiffer(this, EpisodeDiffCallback())

    override fun bind(binding: EpisodeItemBinding, item: String) {
        binding.episodeUrl = item
    }

    override fun createBinding(parent: ViewGroup): EpisodeItemBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(
                layoutInflater,
                R.layout.episode_item,
                parent,
                false
        )
    }

    class EpisodeDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = newItem == oldItem


        override fun areContentsTheSame(oldItem: String, newItem: String) = newItem == oldItem

    }
}