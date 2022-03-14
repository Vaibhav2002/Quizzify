package dev.vaibhav.quizzify.util

import com.vaibhav.healthify.data.models.OnBoarding
import dev.vaibhav.quizzify.R

object Constants {

    const val GAME_KEY_LENGTH = 8

    const val INVITE_CODE_DIALOG = "InviteCode"
    const val QUESTION_COUNT_DIALOG = "QuestionCountDialog"
    const val CREATE_QUESTION_DIALOG = "CreateQuestionDialog"

    const val PORTFOLIO_LINK = "https://vaibhavjaiswal.vercel.app/#/"

    const val TITLE_TOP_MARGIN = 56

    const val GAME_TIME = 30 * 1000L

    const val THREE_ITEM_PERCENTAGE = 0.3f

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    val questionCount = listOf(
        5, 10, 15, 20, 25, 30
    )

    val onBoardingList = listOf(
        OnBoarding(
            id = 1,
            anim = R.raw.playing_games,
            title = "Play free Quizzes",
            subtitle = "Play and create unlimited quizzes for free."
        ),
        OnBoarding(
            id = 2,
            anim = R.raw.friends_anim,
            title = "Play with friends",
            subtitle = "Challenge your friends in quiz by inviting them to play."
        ),
        OnBoarding(
            id = 3,
            anim = R.raw.leaderboard,
            title = "Earn XP as you go",
            subtitle = "Earn XP when you play quizzes. Make your way up to the leaders"
        )
    )

    val avatars = listOf(
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar.png?alt=media&token=af2e2647-5a61-42b7-9b62-adc43dad6310",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-1.png?alt=media&token=c4412535-9a4a-46cf-9c60-b212a5ede20d",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-2.png?alt=media&token=b27caeea-2f06-4079-85ed-3b8254a7269f",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-3.png?alt=media&token=b1adcd2b-cba9-4356-8dd6-9149de7cc2d6",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-4.png?alt=media&token=e791c1ed-3381-4ace-8748-3bb1bf98a7ae",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-5.png?alt=media&token=f252d0c4-8945-471f-a7b7-9e40e21af1dc",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-6.png?alt=media&token=9e08a460-a8a3-4f31-a2cd-18cc21982966",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-7.png?alt=media&token=3ac74d18-ebd2-4c6d-89a4-4349e18b6ff8",
        "https://firebasestorage.googleapis.com/v0/b/quizzify-c1e0b.appspot.com/o/Avatar-8.png?alt=media&token=dda6e942-14bd-495d-8020-4c73fd1ec7ab"
    )
}