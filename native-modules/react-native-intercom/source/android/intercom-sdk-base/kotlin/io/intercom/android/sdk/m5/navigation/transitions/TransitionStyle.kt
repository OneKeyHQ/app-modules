package io.intercom.android.sdk.m5.navigation.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

internal enum class EnterTransitionStyle {
    SLIDE_UP {
        override fun transition(): EnterTransition =
            slideInVertically(animationSpec = tween()) { it }
    },
    SLIDE_IN_LEFT {
        override fun transition(): EnterTransition =
            slideInHorizontally(animationSpec = tween()) { it } +
                scaleIn(animationSpec = tween(), initialScale = 0.92f)
    },
    SLIDE_IN_RIGHT {
        override fun transition(): EnterTransition =
            slideInHorizontally(animationSpec = tween()) { -it } +
                scaleIn(animationSpec = tween(), initialScale = 0.92f)
    },
    PEEK_BEHIND {
        override fun transition(): EnterTransition =
            scaleIn(animationSpec = tween(), initialScale = 0.92f)
    },
    NONE {
        override fun transition(): EnterTransition = EnterTransition.None
    },
    NULL {
        override fun transition(): EnterTransition? = null
    };

    abstract fun transition(): EnterTransition?
}

internal enum class ExitTransitionStyle {
    SLIDE_DOWN {
        override fun transition(): ExitTransition =
            slideOutVertically(animationSpec = tween()) { it }
    },
    SLIDE_OUT_RIGHT {
        override fun transition(): ExitTransition =
            slideOutHorizontally(animationSpec = tween()) { it } +
                scaleOut(animationSpec = tween(), targetScale = 0.92f)
    },
    SLIDE_OUT_LEFT {
        override fun transition(): ExitTransition =
            slideOutHorizontally(animationSpec = tween()) { -it } +
                scaleOut(animationSpec = tween(), targetScale = 0.92f)
    },
    NONE {
        override fun transition(): ExitTransition = ExitTransition.None
    },
    NULL {
        override fun transition(): ExitTransition? = null
    };

    abstract fun transition(): ExitTransition?
}
