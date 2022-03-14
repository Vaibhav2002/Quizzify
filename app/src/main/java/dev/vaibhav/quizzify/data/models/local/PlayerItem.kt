package dev.vaibhav.quizzify.data.models.local

import androidx.recyclerview.widget.DiffUtil
import dev.vaibhav.quizzify.data.models.remote.game.Player

data class PlayerItem(
    val player: Player,
    val isHost: Boolean = false
) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<PlayerItem>() {
            override fun areItemsTheSame(oldItem: PlayerItem, newItem: PlayerItem) =
                oldItem.player.playerId == newItem.player.playerId

            override fun areContentsTheSame(oldItem: PlayerItem, newItem: PlayerItem) =
                oldItem == newItem
        }
    }
}
