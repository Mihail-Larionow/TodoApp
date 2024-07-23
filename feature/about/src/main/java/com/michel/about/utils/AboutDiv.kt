package com.michel.about.utils

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.michel.about.R
import com.michel.about.handlers.LeaveDivActionHandler
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.div2.DivData
import org.json.JSONObject

internal object AboutDiv {

    fun getView(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        darkTheme: Boolean,
        onEvent: () -> Unit,
    ): View {
        val view: View
        val json = context.assets.open("data.json").bufferedReader().readText().trimIndent()

        val contextThemeWrapper = ContextThemeWrapper(context, R.style.Theme_TodoApp)
        val imageLoader = PicassoDivImageLoader(context)
        val configuration = DivConfiguration.Builder(imageLoader)
            .actionHandler(LeaveDivActionHandler(onEvent))
            .build()

        val divData = JSONObject(json).getDivData()

        view = Div2View(
            Div2Context(
                contextThemeWrapper,
                configuration,
                lifecycleOwner = lifecycleOwner
            )
        )

        view.setData(divData, DivDataTag("your_unique_tag_here"))
        view.setVariable("dark_theme", "$darkTheme")

        return view
    }

    private fun JSONObject.getDivData(): DivData {
        val templates = getJSONObject("templates")
        val card = getJSONObject("card")
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
        environment.parseTemplates(templates)
        return DivData(environment, card)
    }

}