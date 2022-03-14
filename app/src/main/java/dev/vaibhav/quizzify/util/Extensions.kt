package dev.vaibhav.quizzify.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.apollographql.apollo.api.Response
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.GameState.STARTED
import dev.vaibhav.quizzify.data.models.remote.game.GameState.WAITING
import dev.vaibhav.quizzify.ui.screens.errorDialog.ErrorDialogFragment
import dev.vaibhav.quizzify.ui.screens.main.MainActivity
import dev.vaibhav.quizzify.util.Constants.GAME_KEY_LENGTH
import dev.vaibhav.quizzify.util.Constants.getRandomString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty0

infix fun <T, F> Resource<T>.mapTo(change: (T) -> F): Resource<F> = when (this) {
    is Resource.Error -> Resource.Error(errorType, message)
    is Resource.Loading -> Resource.Loading()
    is Resource.Success -> Resource.Success(data?.let { change(it) }, message)
}

fun Resource<*>.mapToUnit() = this mapTo {}

fun <T> Resource<T>.mapMessages(
    successMessage: String? = null,
    errorMessage: String? = null
): Resource<T> = when (this) {
    is Resource.Error -> copy(message = errorMessage ?: message)
    is Resource.Success -> copy(message = successMessage ?: message)
    else -> this
}

inline fun <T> Flow<T>.launchAndCollectLatest(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        this@launchAndCollectLatest.collectLatest {
            action(it)
        }
    }
}

inline fun <T> Flow<T>.launchAndCollect(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        this@launchAndCollect.collect {
            action(it)
        }
    }
}

fun Activity.navigateToMainActivity(finishOff: Boolean = true) {
    Intent(this, MainActivity::class.java).also {
        startActivity(it)
        if (finishOff) finish()
    }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.setMarginEnd(margin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(marginStart, marginTop, margin, marginBottom)
    layoutParams = params
}

fun View.setMarginLeft(margin: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(margin.dp, marginTop, marginRight, marginBottom)
    layoutParams = params
}

fun View.showOrGone(isVisible: Boolean) {
    visibility = if (isVisible) VISIBLE else GONE
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun QuizDto.toGame(isSoloGame: Boolean, hostId: String) = Game(
    gameId = getRandomString(GAME_KEY_LENGTH),
    hostId = hostId,
    gameState = if (isSoloGame) STARTED.name else WAITING.name,
    quiz = this,
)

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.showAlertDialog(
    title: String,
    description: String,
    positiveButtonText: String,
    negativeButtonText: String = "Cancel",
    onCancelClick: (() -> Unit) = { },
    onConfirmClick: () -> Unit
) {
    MaterialAlertDialogBuilder(this).apply {
        setTitle(title)
        setMessage(description)
        setPositiveButton(positiveButtonText) { _, _ -> onConfirmClick() }
        setNegativeButton(negativeButtonText) { _, _ -> onCancelClick() }
    }.show()
}

fun Context.getColorFromId(@ColorRes id: Int) = resources.getColor(id, theme)

fun Int.getOrdinalString(): String {
    val j = this % 10
    val k = this % 100
    val ordinal = when {
        j == 1 && k != 11 -> "st"
        j == 2 && k != 12 -> "nd"
        j == 3 && k != 13 -> "rd"
        else -> "th"
    }
    return "$this$ordinal"
}

fun <T> Response<*>.getErrorResource() =
    Resource.Error<T>(message = errors?.get(0)?.message.toString())

const val ERROR_DIALOG = "ERROR_DIALOG"

fun Fragment.showErrorDialog(
    errorType: ErrorType,
    isVisible: KMutableProperty0<Boolean>
) {
    if (isVisible.get()) return
    isVisible.set(true)
    ErrorDialogFragment(errorType) {
        isVisible.set(false)
    }.also {
        it.show(childFragmentManager, ERROR_DIALOG)
    }
}

fun Context.vibrate() {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    else
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(
        VibrationEffect.createOneShot(
            100L, VibrationEffect.DEFAULT_AMPLITUDE
        )
    )
}
