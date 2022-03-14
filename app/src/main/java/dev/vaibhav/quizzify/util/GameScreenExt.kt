package dev.vaibhav.quizzify.util

import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import dev.vaibhav.quizzify.R

fun Fragment.handleOnGameBackPress(
    isHost: () -> Boolean,
    onHostExit: () -> Unit,
    onLeave: () -> Unit
) {
    activity?.onBackPressedDispatcher?.addCallback(this) {
        if (isHost())
            requireContext().showAlertDialog(
                title = getString(R.string.close_game),
                description = getString(R.string.close_game_confirm),
                positiveButtonText = getString(R.string.end),
                onConfirmClick = onHostExit
            )
        else
            requireContext().showAlertDialog(
                title = getString(R.string.leave_game),
                description = getString(R.string.leave_game_confirm),
                positiveButtonText = getString(R.string.leave),
                onConfirmClick = onLeave
            )
    }
}
