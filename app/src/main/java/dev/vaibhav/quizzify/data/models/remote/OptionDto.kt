package dev.vaibhav.quizzify.data.models.remote

import androidx.recyclerview.widget.DiffUtil
import java.io.Serializable

data class OptionDto(
    val text: String = "",
    val correct: Boolean = false
) : Serializable {

    companion object {
        val diff = object : DiffUtil.ItemCallback<OptionDto>() {
            override fun areItemsTheSame(oldItem: OptionDto, newItem: OptionDto) =
                oldItem.text == newItem.text

            override fun areContentsTheSame(oldItem: OptionDto, newItem: OptionDto) =
                oldItem == newItem
        }
    }
}
