package dev.vaibhav.quizzify.data.models.local

import androidx.annotation.IdRes

data class SpotLightView(
    @IdRes val id: Int,
    val title: String,
    val description: String
)