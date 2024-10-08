package com.nekoana.debugpanel

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nekoana.debugpanel.view.panel
import kotlin.apply

interface DebugPanelScope {
    fun button(text: String, onClick: () -> Unit) {
        if (this is ViewGroup) {
            addView(
                com.nekoana.debugpanel.view.button(context, text, onClick),
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            )
        }
    }

    fun checkbox(
        checked: Boolean,
        text: String,
        onCheckedChange: (Boolean) -> Unit
    ) {
        if (this is ViewGroup) {
            addView(
                com.nekoana.debugpanel.view.checkbox(
                    context,
                    checked,
                    text,
                    onCheckedChange
                ), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            )
        }
    }

    fun list(scope: DebugPanelScope.() -> Unit) {
        if (this is ViewGroup) {
            addView(com.nekoana.debugpanel.view.list(context, scope))
        }
    }

    fun view(view: () -> View)
}

class DebugPanel(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    scope: DebugPanelScope.() -> Unit
) : DefaultLifecycleObserver {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val panelLayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.START or Gravity.TOP
    }

    private val panel = panel(context, scope) { offsetX, offsetY ->
        panelLayoutParams.x += offsetX
        panelLayoutParams.y += offsetY

        updatePanelLayout()
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }


    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        windowManager.addView(panel, panelLayoutParams)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        windowManager.removeView(panel)
    }

    private fun updatePanelLayout() {
        windowManager.updateViewLayout(panel, panelLayoutParams)
    }

}