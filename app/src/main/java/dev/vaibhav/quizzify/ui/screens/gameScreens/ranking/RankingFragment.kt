package dev.vaibhav.quizzify.ui.screens.gameScreens.ranking

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.data.models.local.PlayerItem
import dev.vaibhav.quizzify.databinding.FragmentRankingBinding
import dev.vaibhav.quizzify.ui.adapters.PlayerAdapter

@AndroidEntryPoint
class RankingFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRankingBinding
    private lateinit var playerAdapter: PlayerAdapter
    private val args by navArgs<RankingFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerAdapter = PlayerAdapter()
        initViews()
        playerAdapter.submitList(
            args.game.players.sortedByDescending { it.solved }
                .map { PlayerItem(it) }
        )
    }

    private fun initViews() = binding.apply {
        playerRv.apply {
            setHasFixedSize(false)
            adapter = playerAdapter
        }
    }
}