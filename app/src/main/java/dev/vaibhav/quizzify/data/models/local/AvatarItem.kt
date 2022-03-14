package dev.vaibhav.quizzify.data.models.local

import androidx.recyclerview.widget.DiffUtil

data class AvatarItem(
    val avatar: String = "",
    val isSelected: Boolean = false
) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<AvatarItem>() {
            override fun areItemsTheSame(oldItem: AvatarItem, newItem: AvatarItem) =
                oldItem.avatar == newItem.avatar

            override fun areContentsTheSame(oldItem: AvatarItem, newItem: AvatarItem) =
                oldItem.isSelected == newItem.isSelected
        }
    }
}
