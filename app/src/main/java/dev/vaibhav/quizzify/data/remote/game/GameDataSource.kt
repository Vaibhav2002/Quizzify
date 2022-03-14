package dev.vaibhav.quizzify.data.remote.game

import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.Flow

interface GameDataSource {

    suspend fun upsertGame(game: Game): Resource<Unit>

    suspend fun connectToGame(gameId: String, player: Player): Resource<Unit>

    suspend fun observeGame(gameId: String): Flow<Resource<Game>>

    suspend fun updateGameState(gameId: String, gameState: String): Resource<Unit>

    suspend fun getGame(gameId: String): Resource<Game>

    suspend fun getAllGames(): Resource<List<Game>>
}