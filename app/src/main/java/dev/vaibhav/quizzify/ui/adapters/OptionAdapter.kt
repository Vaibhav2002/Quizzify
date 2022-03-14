package dev.vaibhav.quizzify.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.data.models.local.OptionItem
import dev.vaibhav.quizzify.databinding.OptionItemBinding
import dev.vaibhav.quizzify.util.getColorFromId

class OptionAdapter(
    private val onOptionPressed: (OptionItem) -> Unit
) : ListAdapter<OptionItem, OptionAdapter.OptionViewHolder>(OptionItem.diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = OptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OptionViewHolder(private val binding: OptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onOptionPressed(currentList[absoluteAdapterPosition]) }
        }

        fun bind(option: OptionItem) = binding.apply {
            optionImg.setImageResource(
                getIconForOption(option)
            )
            val color = getColorForOption(option, root.context)
            optionLayout.setBackgroundColor(color)
            optionText.text = option.option.text
            optionText.setTextColor(getTextColorForOption(option, root.context))
        }
    }

    private fun getIconForOption(option: OptionItem) = when {
        option.showColorAfterSubmit -> if (option.option.correct) R.drawable.correct_option_icon else R.drawable.incorrect_option_icon
        option.isSelected -> R.drawable.ic_selected_option
        else -> R.drawable.ic_unselected_option
    }

    private fun getColorForOption(option: OptionItem, context: Context) = when {
        option.showColorAfterSubmit -> context.getColorFromId(if (option.option.correct) R.color.green else R.color.red)
        option.isSelected -> context.getColorFromId(R.color.optionSelectedBgColor)
        else -> context.getColorFromId(R.color.optionUnselectedBgColor)
    }

    private fun getTextColorForOption(option: OptionItem, context: Context) = when {
        option.showColorAfterSubmit || option.isSelected -> context.getColorFromId(R.color.optionSelectedTextColor)
        else -> context.getColorFromId(R.color.optionTextColor)
    }
}