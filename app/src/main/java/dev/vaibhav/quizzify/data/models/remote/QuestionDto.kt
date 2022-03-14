package dev.vaibhav.quizzify.data.models.remote

import androidx.recyclerview.widget.DiffUtil
import java.io.Serializable

data class QuestionDto(
    val question: String = "",
    val options: List<OptionDto> = emptyList()
) : Serializable {

    companion object {
        val diff = object : DiffUtil.ItemCallback<QuestionDto>() {
            override fun areItemsTheSame(oldItem: QuestionDto, newItem: QuestionDto) =
                oldItem.question == newItem.question

            override fun areContentsTheSame(oldItem: QuestionDto, newItem: QuestionDto) =
                oldItem == newItem
        }
    }
}
