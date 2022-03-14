package dev.vaibhav.quizzify.data.remote.user

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import dev.vaibhav.quizzify.GetUserByIdQuery
import dev.vaibhav.quizzify.SaveUserMutation
import dev.vaibhav.quizzify.UpdateUserAvatarMutation
import dev.vaibhav.quizzify.data.models.remote.UserDto
import dev.vaibhav.quizzify.util.Resource
import dev.vaibhav.quizzify.util.SafeHasura
import dev.vaibhav.quizzify.util.mapToUnit
import javax.inject.Inject

class HasuraUserDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val safeHasura: SafeHasura
) :
    UserDataSource {

    override suspend fun getUserData(userId: String): Resource<UserDto> {
        val getUserQuery = GetUserByIdQuery(userId)
        return safeHasura.safeHasuraCall(
            call = { apolloClient.query(getUserQuery).await() },
            result = { data ->
                data.user_by_pk?.let {
                    UserDto(
                        userId = it.userId,
                        username = it.username,
                        email = it.email,
                        profilePic = it.profilePic,
                        exp = it.exp
                    )
                }
            }
        )
    }

    override suspend fun saveUserData(userDto: UserDto): Resource<Unit> {
        val saveUserDataMutation = SaveUserMutation(
            userId = Input.fromNullable(userDto.userId),
            username = Input.fromNullable(userDto.username),
            email = Input.fromNullable(userDto.email),
            profilePic = Input.fromNullable(userDto.profilePic),
            exp = Input.fromNullable(userDto.exp)
        )
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(saveUserDataMutation).await() },
            result = { it.insert_user_one?.userId }
        ).mapToUnit()
    }

    override suspend fun updateAvatar(userId: String, avatar: String): Resource<Unit> {
        val updateAvatarMutation = UpdateUserAvatarMutation(userId, Input.optional(avatar))
        return safeHasura.safeHasuraCall(
            call = { apolloClient.mutate(updateAvatarMutation).await() },
            result = { it.update_user_by_pk?.userId }
        ).mapToUnit()
    }
}