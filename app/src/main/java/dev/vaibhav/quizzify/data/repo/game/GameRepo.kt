package dev.vaibhav.quizzify.data.repo.game

import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.Flow

interface GameRepo {

    suspend fun addGame(game: Game): Flow<Resource<Unit>>

    suspend fun connectToGame(gameId: String, player: Player): Flow<Resource<Unit>>

    suspend fun observeGame(gameId: String): Flow<Resource<Game>>

    suspend fun startGame(gameId: String): Flow<Resource<Unit>>

    suspend fun doesGameExist(gameId: String): Flow<Resource<Boolean>>

    suspend fun leaveGame(player: Player, game: Game): Flow<Resource<Unit>>

    suspend fun cancelGame(gameId: String): Flow<Resource<Unit>>

    suspend fun completeGame(game: Game, player: Player): Flow<Resource<Unit>>

    suspend fun submitAnswer(game: Game, player: Player): Flow<Resource<Unit>>

    suspend fun getAllGamesOfUser(userId: String): Flow<Resource<List<Game>>>
}