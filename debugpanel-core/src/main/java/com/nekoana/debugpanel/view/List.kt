package com.nekoana.debugpanel.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import com.nekoana.debugpanel.DebugPanelScope
import com.nekoana.debugpanel.core.R


internal class List @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr),DebugPanelScope {
    private var scope: (DebugPanelScope.() -> Unit)? = null

    init {
        orientation = VERTICAL
        dividerDrawable = AppCompatResources.getDrawable(context, R.drawable.list_divider)
        showDividers = SHOW_DIVIDER_MIDDLE
    }

    fun setPanelScope(scope: DebugPanelScope.() -> Unit) {
        this.scope = scope
        scope.invoke(this)
    }
}

fun list(context: Context, scope: DebugPanelScope.() -> Unit): ViewGroup = List(context).apply {
    setPanelScope(scope)
}