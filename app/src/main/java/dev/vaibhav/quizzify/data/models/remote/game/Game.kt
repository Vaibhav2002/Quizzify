package dev.vaibhav.quizzify.data.models.remote.game

import com.google.gson.annotations.SerializedName
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import java.io.Serializable

data class Game(
    val gameId: String = "",
    val players: List<Player> = emptyList(),
    val quiz: QuizDto = QuizDto(),
    val hostId: String = "",
    val gameState: String = GameState.WAITING.name,
    val inviteCode: String = gameId,
    @SerializedName("timestamp")
    val timeStamp: Long = System.currentTimeMillis(),
) : Serializable

enum class GameState {
    WAITING, STARTED, FINISHED, CANCELED
}