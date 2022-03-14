package dev.vaibhav.quizzify.data.remote.game

import com.google.firebase.firestore.FirebaseFirestore
import dev.vaibhav.quizzify.data.models.remote.game.Game
import dev.vaibhav.quizzify.data.models.remote.game.Player
import dev.vaibhav.quizzify.data.remote.FirestoreKeys.GAME
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.SafeFirebase.safeCall
import dev.vaibhav.quizzify.util.mapToUnit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseGameDataSource @Inject constructor(private val fireStore: FirebaseFirestore) :
    GameDataSource {

    override suspend fun upsertGame(game: Game): Resource<Unit> =
        safeCall(handleNullCheck = false) {
            fireStore.collection(GAME).document(game.gameId).set(game).await()
        }.mapToUnit()

    override suspend fun connectToGame(gameId: String, player: Player): Resource<Unit> =
        safeCall(handleNullCheck = false) {
            val game =
                fireStore.collection(GAME).document(gameId).get().await().toObject(Game::class.java)
            val newPlayerList = game?.players?.toMutableList()
            newPlayerList?.add(player)
            fireStore.collection(GAME).document(gameId).update("players", newPlayerList).await()
        }.mapToUnit()

    override suspend fun observeGame(gameId: String): Flow<Resource<Game>> = callbackFlow {
        val ref = fireStore.collection(GAME).document(gameId)
        val listener = ref.addSnapshotListener { value, error ->
            if (error == null) {
                value?.toObject(Game::class.java)?.let {
                    trySend(Resource.Success(it))
                }
            } else Resource.Error<Game>(message = error.message.toString())
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateGameState(gameId: String, gameState: String): Resource<Unit> =
        safeCall(false) {
            fireStore.collection(GAME).document(gameId).update("gameState", gameState).await()
        }.mapToUnit()

    override suspend fun getGame(gameId: String): Resource<Game> = safeCall {
        fireStore.collection(GAME).document(gameId).get().await().toObject(Game::class.java)
    }

    override suspend fun getAllGames(): Resource<List<Game>> = safeCall {
        fireStore.collection(GAME).get().await().toObjects(Game::class.java)
    }
}