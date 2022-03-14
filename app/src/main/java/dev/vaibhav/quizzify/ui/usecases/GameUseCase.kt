package dev.vaibhav.quizzify.ui.usecases

import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.data.repo.auth.AuthRepository
import dev.vaibhav.quizzify.data.repo.game.GameRepo
import dev.vaibhav.quizzify.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameUseCase @Inject constructor(
    private val gameRepo: GameRepo,
    private val authRepository: AuthRepository
) {

    suspend fun createGame(game: Game) = gameRepo.addGame(game)

    suspend fun connectToGame(gameId: String, user: UserDto): Flow<Resource<Unit>> {
        val player = Player(user.userId, user.username, user.profilePic)
        return gameRepo.connectToGame(gameId, player)
    }

    suspend fun observeGame(gameId: String) = gameRepo.observeGame(gameId)

    suspend fun startGame(gameId: String) = gameRepo.startGame(gameId)

    suspend fun completeGame(game: Game, player: Player) = gameRepo.completeGame(game, player)

    suspend fun submitAnswer(game: Game, player: Player) = gameRepo.submitAnswer(game, player)

    suspend fun cancelGame(gameId: String) = gameRepo.cancelGame(gameId)

    suspend fun leaveGame(player: Player, game: Game) = gameRepo.leaveGame(player, game)
}