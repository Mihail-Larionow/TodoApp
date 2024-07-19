package com.michel.about.handlers

import android.net.Uri
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

const val SCHEME_EXIT = "exit-action"

class LeaveDivActionHandler(
    private val onEvent: () -> Unit,
) : DivActionHandler() {
    override fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        resolver: ExpressionResolver
    ): Boolean {
        val url =
            action.url?.evaluate(resolver) ?: return super.handleAction(action, view, resolver)

        return if (url.scheme == SCHEME_EXIT && handleExitAction(url)) {
            true
        } else {
            super.handleAction(action, view, resolver)
        }
    }

    private fun handleExitAction(action: Uri): Boolean {
        return when (action.host) {
            "settings" -> {
                onEvent()
                true
            }

            else -> false
        }
    }
}