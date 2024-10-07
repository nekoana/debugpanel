package com.nekoana.debugpanel

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nekoana.debugpanel.view.Panel
import kotlin.apply


interface DebugPanelScope {
    fun button(text: String, onClick: () -> Unit)
    fun switch(text: String, checked: Boolean, onClick: (Boolean) -> Unit)
    fun list(scope: DebugPanelScope.() -> Unit)
    fun view(view: () -> View)
}

class DebugPanel(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    panelScope: DebugPanelScope.() -> Unit
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

    private val panel = Panel(context).apply {
        setOnDragCallback { offsetX, offsetY ->
            panelLayoutParams.x += offsetX
            panelLayoutParams.y += offsetY

            windowManager.updateViewLayout(this, panelLayoutParams)
        }
        setPanelScope(panelScope)
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

    companion object {
        private const val TAG = "DebugPanel"
    }

}