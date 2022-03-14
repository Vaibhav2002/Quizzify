package dev.vaibhav.quizzify.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import dev.vaibhav.quizzify.databinding.QuestionItemBinding

class QuestionAdapter(
    private val onRemoveClicked: (QuestionDto) -> Unit
) : ListAdapter<QuestionDto, QuestionAdapter.QuestionViewHolder>(QuestionDto.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding =
            QuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class QuestionViewHolder(private val binding: QuestionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val optionAdapter = CreateOptionAdapter()

        init {
            binding.optionRv.apply {
                setHasFixedSize(false)
                adapter = optionAdapter
            }
            binding.closeBtn.setOnClickListener { onRemoveClicked(currentList[absoluteAdapterPosition]) }
        }

        fun bind(question: QuestionDto) = binding.apply {
            questionTv.text = question.question
            optionAdapter.submitList(question.options)
        }
    }
}