package dev.vaibhav.quizzify.ui.screens.main.community

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.data.models.remote.CategoryDto
import dev.vaibhav.quizzify.databinding.FragmentCommunityBinding
import dev.vaibhav.quizzify.ui.adapters.QuizAdapter
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class CommunityFragment : Fragment(R.layout.fragment_community) {

    private val binding by viewBinding(FragmentCommunityBinding::bind)
    private val viewModel by viewModels<CommunityViewModel>()
    private lateinit var quizAdapter: QuizAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectCategories()
        collectQuizzes()
        collectEvents()
    }

    private fun collectEvents() = viewModel.events.launchAndCollect(viewLifecycleOwner) {
        when (it) {
            CommunityScreenEvents.NavigateToCreateQuizScreen -> {
                findNavController().navigate(R.id.action_communityFragment_to_createQuizFragment)
            }
            is CommunityScreenEvents.NavigateToQuizDetailsScreen -> {
                val action =
                    CommunityFragmentDirections.actionCommunityFragmentToQuizDetailsFragment(it.quiz)
                findNavController().navigate(action)
            }
        }
    }

    private fun collectQuizzes() = viewModel.quizzes.launchAndCollectLatest(viewLifecycleOwner) {
        quizAdapter.submitList(it)
    }

    private fun collectCategories() =
        viewModel.categories.launchAndCollectLatest(viewLifecycleOwner) {
            addChips(it)
        }

    private fun initListeners() = binding.apply {
        queryEt.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchQueryChanged(text.toString())
        }
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val titleOrNull = chipGroup.findViewById<Chip>(checkedId)?.text?.toString()
            viewModel.onCategorySelected(titleOrNull)
        }
        createQuizBtn.setOnClickListener { viewModel.onCreateQuizPressed() }
    }

    private fun initViews() = binding.apply {
        header.setMarginTop(Constants.TITLE_TOP_MARGIN)
        quizAdapter = QuizAdapter(viewModel::onQuizPressed)
        quizRv.apply {
            setHasFixedSize(false)
            adapter = quizAdapter
        }
    }

    private fun addChips(categories: List<CategoryDto>) = binding.apply {
        categories.forEach {
            val chip = Chip(requireContext()).apply {
                text = it.name
                setTextAppearance(R.style.TextAppearance_MdcTypographyStyles_Subtitle1)
                textStartPadding = 16.dp.toFloat()
                textEndPadding = 16.dp.toFloat()
                id = ViewCompat.generateViewId()
                isCheckable = true
            }
            chipGroup.addView(chip)
        }
        restoreSelectedChip()
    }

    private fun restoreSelectedChip() = binding.apply {
        viewModel.selectedCategory.value?.let { category ->
            val toBeSelected = (0 until chipGroup.childCount).map {
                chipGroup.getChildAt(it) as Chip
            }.find { it.text.equals(category.name) }
            toBeSelected?.let { chipGroup.check(it.id) }
        }
    }
}