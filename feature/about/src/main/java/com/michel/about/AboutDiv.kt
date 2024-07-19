package com.michel.about

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
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

class AboutDiv(
    context: Context,
    onEvent: () -> Unit,
) {
    private val view: View

    init {
        val json = context.assets.open("data.json").bufferedReader().readText().trimIndent()

        val contextThemeWrapper = ContextThemeWrapper(context, R.style.Theme_TodoApp)
        val imageLoader = PicassoDivImageLoader(context)
        val configuration = DivConfiguration.Builder(imageLoader)
            .actionHandler(LeaveDivActionHandler(onEvent))
            .build()

        val divData = JSONObject(json).getDivData()
        view = Div2View(Div2Context(contextThemeWrapper, configuration, lifecycleOwner = null))

        view.setData(divData, DivDataTag("your_unique_tag_here"))
    }

    fun getView(): View {
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