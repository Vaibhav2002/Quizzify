package dev.vaibhav.quizzify.ui.screens.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentAboutBinding
import dev.vaibhav.quizzify.util.Constants.PORTFOLIO_LINK
import dev.vaibhav.quizzify.util.Constants.TITLE_TOP_MARGIN
import dev.vaibhav.quizzify.util.setMarginTop
import dev.vaibhav.quizzify.util.viewBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

    private val binding by viewBinding(FragmentAboutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = binding.apply {
        header.setMarginTop(TITLE_TOP_MARGIN)
        myName.setOnClickListener { openPortfolio() }
    }

    private fun openPortfolio() {
        Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(PORTFOLIO_LINK)
            startActivity(it)
        }
    }
}