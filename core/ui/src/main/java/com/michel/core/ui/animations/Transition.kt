package com.michel.core.ui.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

object Transition {

    val screenExitRight: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
        get() = {
            slideOutHorizontally(targetOffsetX = { 500 })
        }

    val screenEnterRight: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
        get() = {
            slideInHorizontally(initialOffsetX = { 500 })
        }

    val screenExitLeft: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
        get() = {
            slideOutHorizontally(targetOffsetX = { -500 })
        }

    val screenEnterLeft: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
        get() = {
            slideInHorizontally(initialOffsetX = { -500 })
        }

    val screenExitBottom: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
        get() = {
            slideOutVertically(targetOffsetY = { 500 })
        }

    val screenEnterBottom: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
        get() = {
            slideInVertically(initialOffsetY = { 500 })
        }

    val screenExitTop: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
        get() = {
            slideOutVertically(targetOffsetY = { -500 })
        }

    val screenEnterTop: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
        get() = {
            slideInVertically(initialOffsetY = { -500 })
        }
}