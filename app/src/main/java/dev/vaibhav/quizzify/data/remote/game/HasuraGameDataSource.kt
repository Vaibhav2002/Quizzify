package dev.vaibhav.quizzify.data.remote.game

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import dev.vaibhav.quizzify.*
import dev.vaibhav.quizzify.data.local.room.QuizzifyTypeConverters
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.SafeHasura
import dev.vaibhav.quizzify.util.mapToUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HasuraGameDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val safeHasura: SafeHasura
) : GameDataSource {

    private val typeConverters = QuizzifyTypeConverters()

    override suspend fun upsertGame(game: Game): Resource<Unit> {
        val mutation = UpsertGameMutation(
            gameId = Input.optional(game.gameId),
            hostId = Input.optional(game.hostId),
            quiz = Input.optional(typeConverters.fromQuiz(game.quiz)),
            gameState = Input.optional(game.gameState),
            inviteCode = Input.optional(game.inviteCode),
            players = Input.optional(typeConverters.fromPlayerList(game.players))
        )
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(mutation).await() },
            result = { it.insert_game_one?.gameId }
        ).mapToUnit()
    }

    override suspend fun connectToGame(gameId: String, player: Player): Resource<Unit> {
        val game = getGame(gameId)
        if (game is Resource.Error) return game.mapToUnit()
        val newPlayerList = game.data!!.players.toMutableList().apply { add(player) }
        val mutation = UpdatePlayersMutation(
            gameId,
            players = typeConverters.fromPlayerList(newPlayerList)
        )
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(mutation).await() },
            result = { it.update_game_by_pk?.gameId }
        ).mapToUnit()
    }

    override suspend fun observeGame(gameId: String): Flow<Resource<Game>> {
        val subscription = ObserveGameSubscription(gameId)
        return apolloClient.subscribe(subscription).toFlow().map {
            safeHasura.safeHasuraCall(
                call = { it },
                result = { data -> data.game_by_pk?.let { getGameFromData(it) } }
            )
        }.catch {
            emit(Resource.Error())
        }
    }

    override suspend fun updateGameState(gameId: String, gameState: String): Resource<Unit> {
        val mutation = UpdateGameStateMutation(gameId, gameState)
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(mutation).await() },
            result = { it.update_game_by_pk?.gameId }
        ).mapToUnit()
    }

    override suspend fun getGame(gameId: String): Resource<Game> {
        val query = GetGameByIdQuery(gameId)
        return safeHasura.safeHasuraCall(
            call = { apolloClient.query(query).await() },
            result = { data ->
                data.game_by_pk?.let { getGameFromData(it) }
            }
        )
    }

    override suspend fun getAllGames(): Resource<List<Game>> {
        val query = GetAllGamesQuery()
        return safeHasura.safeHasuraCall(
            call = { apolloClient.query(query).await() },
            result = { data ->
                getGamesFromData(data.game)
            }
        )
    }

    private fun getGameFromData(data: GetGameByIdQuery.Game_by_pk) = Game(
        gameId = data.gameId,
        quiz = typeConverters.toQuiz(data.quiz),
        players = typeConverters.toPlayerList(data.players),
        hostId = data.hostId,
        gameState = data.gameState,
        inviteCode = data.inviteCode
    )

    private fun getGameFromData(data: ObserveGameSubscription.Game_by_pk) = Game(
        gameId = data.gameId,
        quiz = typeConverters.toQuiz(data.quiz),
        players = typeConverters.toPlayerList(data.players),
        hostId = data.hostId,
        gameState = data.gameState,
        inviteCode = data.inviteCode
    )

    private fun getGamesFromData(data: List<GetAllGamesQuery.Game>) = data.map {
        Game(
            gameId = it.gameId,
            quiz = typeConverters.toQuiz(it.quiz),
            players = typeConverters.toPlayerList(it.players),
            hostId = it.hostId,
            gameState = it.gameState,
            inviteCode = it.inviteCode
        )
    }
}