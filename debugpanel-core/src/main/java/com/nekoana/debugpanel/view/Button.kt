package com.nekoana.debugpanel.view

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import com.nekoana.debugpanel.core.R

fun button(
    context: Context,
    text: String,
    onClick: View.OnClickListener
) = AppCompatButton(ContextThemeWrapper(context, R.style.DebugPanel)).apply {
    setOnClickListener(onClick)
    setText(text)
}