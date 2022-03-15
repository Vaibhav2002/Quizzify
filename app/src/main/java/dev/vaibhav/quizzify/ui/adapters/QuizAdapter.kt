package dev.vaibhav.quizzify.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.databinding.QuizItemBinding

class QuizAdapter(
    private val onQuizPressed: (QuizItemBinding, QuizDto) -> Unit
) : ListAdapter<QuizDto, QuizAdapter.QuizViewHolder>(QuizDto.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding =
            QuizItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class QuizViewHolder(private val binding: QuizItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onQuizPressed(
                    binding,
                    currentList[absoluteAdapterPosition]
                )
            }
        }

        fun bind(quizDto: QuizDto) = binding.apply {
            madeByTv.isVisible = quizDto.createdBy.isNotEmpty()
            quiz = quizDto
            root.transitionName = quizDto.id
        }
    }
}