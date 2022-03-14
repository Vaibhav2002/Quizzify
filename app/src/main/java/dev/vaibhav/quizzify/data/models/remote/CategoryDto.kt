package dev.vaibhav.quizzify.data.models.remote

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category_table")
data class CategoryDto(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val image: String = ""
) : Serializable {
    companion object {
        val diff = object : DiffUtil.ItemCallback<CategoryDto>() {
            override fun areItemsTheSame(oldItem: CategoryDto, newItem: CategoryDto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CategoryDto, newItem: CategoryDto) =
                oldItem == newItem
        }
    }
}