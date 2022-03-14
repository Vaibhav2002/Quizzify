package dev.vaibhav.quizzify.data.models.local

import androidx.recyclerview.widget.DiffUtil
import dev.vaibhav.quizzify.data.models.remote.OptionDto

data class OptionItem(
    val option: OptionDto,
    val isSelected: Boolean = false,
    val showColorAfterSubmit: Boolean = false,
) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<OptionItem>() {
            override fun areItemsTheSame(
                oldItem: OptionItem,
                newItem: OptionItem
            ) = oldItem.option.text == newItem.option.text

            override fun areContentsTheSame(
                oldItem: OptionItem,
                newItem: OptionItem
            ) = oldItem == newItem
        }
    }
}