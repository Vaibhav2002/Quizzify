package dev.vaibhav.quizzify.ui.screens.auth.avatarSelect

import dev.vaibhav.quizzify.data.models.local.AvatarItem

data class AvatarSelectScreenState(
    val avatars: List<AvatarItem> = emptyList(),
    val isLoading: Boolean = false
) {
    val isButtonEnabled: Boolean
        get() = avatars.any { it.isSelected } && !isLoading
}
