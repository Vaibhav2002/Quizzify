package dev.vaibhav.quizzify.data.models.remote

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "quiz_table")
data class QuizDto(
    val name: String = "",
    val description: String = "",
    val createdBy: String = "",
    val createdByUserId: String = "",
    val votes: Int = 0,
    val category: CategoryDto = CategoryDto(),
    val questions: List<QuestionDto> = emptyList(),
    val questionCount: Int = questions.size,
    @SerializedName("timestamp")
    val timeStamp: Long = System.currentTimeMillis(),
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
) : Serializable {

    val isInstantQuiz: Boolean
        get() = createdByUserId.isEmpty()

    companion object {
        val diff = object : DiffUtil.ItemCallback<QuizDto>() {
            override fun areItemsTheSame(oldItem: QuizDto, newItem: QuizDto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: QuizDto, newItem: QuizDto) = oldItem == newItem
        }
    }
}
