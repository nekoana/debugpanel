package com.nekoana.debugpanel

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import com.nekoana.debugpanel.core.R


@DslMarker
annotation class DebugElementMarker

interface Element {
    fun render(parent: View)
}

@DebugElementMarker
abstract class Debug() : Element {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initDebug(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    override fun render(parent: View) {
        for (c in children) {
            c.render(parent)
        }
    }
}

class Button() : Debug() {
    var onClick: ((View) -> Unit)? = null
    var text: String = ""

    override fun render(parent: View) {
        if (parent is ViewGroup) {
            val button =
                AppCompatButton(ContextThemeWrapper(parent.context, R.style.DebugPanel_Button))

            button.text = text
            button.setOnClickListener {
                onClick?.invoke(it)
            }
            parent.addView(
                button,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}

class Checkbox() : Debug() {
    var text: String = ""
    var isChecked: Boolean = false
    var onCheckedChange: ((Boolean) -> Unit)? = null

    override fun render(parent: View) {
        if (parent is ViewGroup) {
            val checkbox =
                AppCompatCheckBox(ContextThemeWrapper(parent.context, R.style.DebugPanel_Checkbox))

            checkbox.text = text
            checkbox.isChecked = isChecked
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                this@Checkbox.isChecked = isChecked
                onCheckedChange?.invoke(isChecked)
            }
            parent.addView(
                checkbox,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}

abstract class Group() : Debug() {
    fun button(init: Button.() -> Unit) = initDebug(Button(), init)
    fun checkbox(init: Checkbox.() -> Unit) = initDebug(Checkbox(), init)
}

class List() : Group() {
    override fun render(parent: View) {
        if (parent is ViewGroup) {
            val list = LinearLayoutCompat(parent.context).apply {
                orientation = VERTICAL
                dividerDrawable =
                    AppCompatResources.getDrawable(context, R.drawable.debugpanel_list_divider)
                showDividers = SHOW_DIVIDER_MIDDLE
            }
            super.render(list)
            parent.addView(
                list,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}

class Scroller() : Group() {
    fun list(init: List.() -> Unit) = initDebug(List(), init)

    override fun render(parent: View) {
        if (parent is ViewGroup) {
            val scroller = ScrollView(parent.context)
            super.render(scroller)
            parent.addView(
                scroller,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}

class DebugScope : Debug() {
    fun list(init: List.() -> Unit) = initDebug(List(), init)
    fun scroller(init: Scroller.() -> Unit) = initDebug(Scroller(), init)
}




