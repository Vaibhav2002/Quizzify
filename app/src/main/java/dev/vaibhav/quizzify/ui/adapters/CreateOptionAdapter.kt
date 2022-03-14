package dev.vaibhav.quizzify.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.vaibhav.quizzify.data.models.remote.OptionDto
import dev.vaibhav.quizzify.databinding.CreateOptionItemBinding

class CreateOptionAdapter :
    ListAdapter<OptionDto, CreateOptionAdapter.CreateOptionViewHolder>(OptionDto.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateOptionViewHolder {
        val binding =
            CreateOptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreateOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateOptionViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class CreateOptionViewHolder(private val binding: CreateOptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(optionDto: OptionDto) = binding.apply {
            tickIcon.isVisible = optionDto.correct
            optionTv.text = "Option ${absoluteAdapterPosition + 1}: ${optionDto.text}"
        }
    }
}