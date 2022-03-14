package dev.vaibhav.quizzify.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.vaibhav.quizzify.data.models.local.AvatarItem
import dev.vaibhav.quizzify.databinding.AvatarItemBinding
import dev.vaibhav.quizzify.util.dp

class AvatarAdapter(
    private val onAvatarPressed: (AvatarItem) -> Unit
) : ListAdapter<AvatarItem, AvatarAdapter.AvatarViewHolder>(AvatarItem.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = AvatarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class AvatarViewHolder(private val binding: AvatarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.avatarImage.setOnClickListener { onAvatarPressed(currentList[absoluteAdapterPosition]) }
        }

        fun bind(avatar: AvatarItem) = binding.apply {
            rootCard.strokeWidth = if (avatar.isSelected) 3.dp else 0.dp
            avatarImage.load(avatar.avatar)
        }
    }
}