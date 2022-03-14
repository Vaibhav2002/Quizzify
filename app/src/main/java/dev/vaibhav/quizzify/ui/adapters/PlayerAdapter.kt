package dev.vaibhav.quizzify.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.vaibhav.quizzify.data.models.local.PlayerItem
import dev.vaibhav.quizzify.databinding.PlayerListItemBinding

class PlayerAdapter :
    ListAdapter<PlayerItem, PlayerAdapter.PlayerViewHolder>(PlayerItem.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            PlayerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class PlayerViewHolder(private val binding: PlayerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: PlayerItem) {
            binding.player = player
            binding.hostTv.isVisible = player.isHost
        }
    }
}