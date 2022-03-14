package dev.vaibhav.quizzify.ui.screens.auth.gettingStarted

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentGettingStartedBinding
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class GettingStartedFragment : Fragment(R.layout.fragment_getting_started) {

    private val binding by viewBinding(FragmentGettingStartedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.gettingStartedImage.setLargeImage(R.drawable.getting_started_illustration)
        binding.signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_gettingStartedFragment_to_loginFragment)
        }
        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_gettingStartedFragment_to_registerFragment)
        }
    }
}
