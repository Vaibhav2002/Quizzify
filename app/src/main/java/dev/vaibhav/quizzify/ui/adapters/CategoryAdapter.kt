package dev.vaibhav.quizzify.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.databinding.CategoryItemBinding

class CategoryAdapter(
    private val onCategoryPressed: (CategoryDto) -> Unit
) : ListAdapter<CategoryDto, CategoryAdapter.CategoryViewHolder>(CategoryDto.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onCategoryPressed(currentList[absoluteAdapterPosition]) }
        }

        fun bind(categoryDto: CategoryDto) {
            binding.category = categoryDto
        }
    }
}
