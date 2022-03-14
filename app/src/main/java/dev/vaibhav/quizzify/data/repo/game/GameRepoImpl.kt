package dev.vaibhav.quizzify.data.repo.game

import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.GameState
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.data.remote.game.GameDataSource
import dev.vaibhav.quizzify.util.DATA_NULL
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.mapMessages
import dev.vaibhav.quizzify.util.mapTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepoImpl @Inject constructor(private val gameDataSource: GameDataSource) : GameRepo {

    override suspend fun addGame(game: Game): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        emit(gameDataSource.upsertGame(game))
    }.map { it.mapMessages("Created Game", "Failed to create game") }
        .flowOn(Dispatchers.IO)

    override suspend fun connectToGame(gameId: String, player: Player): Flow<Resource<Unit>> =
        flow {
            emit(Resource.Loading())
            emit(gameDataSource.connectToGame(gameId, player))
        }.map { it.mapMessages("Connected to Game", "Failed to connect to game") }
            .flowOn(Dispatchers.IO)

    override suspend fun observeGame(gameId: String): Flow<Resource<Game>> =
        gameDataSource.observeGame(gameId)

    override suspend fun startGame(gameId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        emit(gameDataSource.updateGameState(gameId, GameState.STARTED.toString()))
    }.map { it.mapMessages("Game Started", "Failed to start game") }
        .flowOn(Dispatchers.IO)

    override suspend fun doesGameExist(gameId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        emit(gameDataSource.getGame(gameId))
    }.map {
        when (it) {
            is Resource.Error -> {
                if (it.message == DATA_NULL) Resource.Success(
                    false,
                    "Game does not exist"
                ) else it.mapTo { false }
            }
            is Resource.Loading -> it.mapTo { false }
            is Resource.Success -> {
                if (it.data!!.gameState == GameState.CANCELED.name)
                    Resource.Success(false, "Game has ended")
                else Resource.Success(true)
            }
        }
    }

    override suspend fun leaveGame(player: Player, game: Game): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val newPlayerList = game.players.toMutableList()
        newPlayerList.removeIf { it.playerId == player.playerId }
        val newGame = game.copy(players = newPlayerList)
        emit(gameDataSource.upsertGame(newGame))
    }.map { it.mapMessages("Successfully left Game", "Failed to leave game") }
        .flowOn(Dispatchers.IO)

    override suspend fun cancelGame(gameId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        emit(gameDataSource.updateGameState(gameId, GameState.CANCELED.name))
    }.map {
        it.mapMessages("Canceled Game", "Failed to cancel game")
    }.flowOn(Dispatchers.IO)

    override suspend fun completeGame(game: Game, player: Player): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val newPlayerList = game.players.toMutableList().map {
            if (it.playerId == player.playerId) it.copy(hasCompleted = true)
            else it
        }
        val hasEveryOneCompleted = newPlayerList.all { it.hasCompleted }
        val newGame = game.copy(
            players = newPlayerList,
            gameState = if (hasEveryOneCompleted) GameState.FINISHED.name else game.gameState
        )
        emit(gameDataSource.upsertGame(newGame))
    }.map { it.mapMessages("Game finished", "Failed to finish Game") }
        .flowOn(Dispatchers.IO)

    override suspend fun submitAnswer(game: Game, player: Player): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val newPlayerList = game.players.map {
            if (it.playerId == player.playerId)
                it.copy(solved = it.solved + 1)
            else it
        }
        val newGame = game.copy(players = newPlayerList)
        emit(gameDataSource.upsertGame(newGame))
    }.map { it.mapMessages("Answer submitted", "Failed to submit answer") }
        .flowOn(Dispatchers.IO)

    override suspend fun getAllGamesOfUser(userId: String): Flow<Resource<List<Game>>> = flow {
        emit(Resource.Loading())
        val resource = gameDataSource.getAllGames()
        emit(resource)
    }.map { resource ->
        resource.mapTo {
            it.filter { game ->
                game.players.find { player -> player.playerId == userId } != null
            }
        }
    }
}