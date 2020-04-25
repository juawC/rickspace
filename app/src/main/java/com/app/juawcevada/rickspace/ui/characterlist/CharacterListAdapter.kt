package com.app.juawcevada.rickspace.ui.characterlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.app.juawcevada.rickspace.R
import com.app.juawcevada.rickspace.databinding.CharacterItemBinding
import com.app.juawcevada.rickspace.model.Character
import com.app.juawcevada.rickspace.model.Location
import com.app.juawcevada.rickspace.model.Origin
import com.app.juawcevada.rickspace.ui.shared.PagedRecyclerAdapter


class CharacterListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val onCharacterSelected: (Character) -> Unit
) : PagedRecyclerAdapter<Character, CharacterItemBinding>(CharacterDiff()) {

    private val onClickListener: View.OnClickListener = View.OnClickListener {
        val character: Character = it.tag as Character
        onCharacterSelected(character)
    }

    override fun bind(binding: CharacterItemBinding, item: Character?) {
        binding.character = item ?: createCharacterPlaceHolder()
        binding.root.apply {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun createBinding(parent: ViewGroup): CharacterItemBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(
                layoutInflater,
                R.layout.character_item,
                parent,
                false,
                dataBindingComponent
        )
    }

    class CharacterDiff : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(old: Character, new: Character) = old.id == new.id
        override fun areContentsTheSame(old: Character, new: Character) = old == new
    }

    companion object {
        fun createCharacterPlaceHolder() =
                Character(0,
                        "",
                        "",
                        "",
                        "",
                        "",
                        Origin("", ""),
                        Location("", ""),
                        "",
                        emptyList(),
                        "")
    }
}
