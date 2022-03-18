package dev.vaibhav.quizzify.data.models.local

data class SpotLightCheck(
    val isHomeSpotLightComplete: Boolean = false,
    val isCommunitySpotLightComplete: Boolean = false,
    val isFavouritesSpotLightComplete: Boolean = false,
    val isProfileSpotLightComplete: Boolean = false
)
