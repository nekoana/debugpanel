package com.nekoana.debugpanel

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nekoana.debugpanel.core.R
import kotlin.properties.Delegates

class DebugPanel(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    scope: DebugScope.() -> Unit
) : ViewGroup(ContextThemeWrapper(context, R.style.DebugPanel)), DefaultLifecycleObserver {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val panelLayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.START or Gravity.TOP
    }

    private val container = Container(context)
    private val ball = Ball(context)

    private val debug: DebugScope by lazy(LazyThreadSafetyMode.NONE) {
        DebugScope().apply(scope)
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)

        addView(ball)

        attach(ball) { offsetX, offsetY ->
            panelLayoutParams.x += offsetX
            panelLayoutParams.y += offsetY
            updatePanelLayout()
        }

        //透明背景
        setBackgroundColor(Color.TRANSPARENT)
        //不响应触摸事件
        isClickable = false
        //不响应焦点事件
        isFocusable = false
        //不响应触摸事件
        isFocusableInTouchMode = false
        //设置布局动画
        layoutTransition = LayoutTransition()
    }

    /**
     * 是否展开
     */
    private var isExpanded: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            if (newValue) {
                debug.render(container)
                addView(container)
            } else {
                super.removeView(container)
            }

            if (newValue) ball.setXText()
            else ball.setDText()
        }
    }

    init {
        ball.setOnClickListener {
            if (container.pop() && !container.isEmpty()) return@setOnClickListener
            isExpanded = !isExpanded
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        windowManager.addView(this, panelLayoutParams)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        windowManager.removeView(this)
    }

    private fun updatePanelLayout() {
        windowManager.updateViewLayout(this, panelLayoutParams)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        ball.measure(widthMeasureSpec, heightMeasureSpec)
        container.measure(widthMeasureSpec, heightMeasureSpec)

        if (isExpanded) {
            setMeasuredDimension(
                ball.collapsedSize + container.expendedWidth,
                container.expendedHeight
            )
        } else {
            setMeasuredDimension(ball.collapsedSize, ball.collapsedSize)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val hoverBallWidth = ball.measuredWidth
        val hoverBallHeight = ball.measuredHeight

        val containerPanelWidth = container.measuredWidth
        val containerPanelHeight = container.measuredHeight

        if (isExpanded) {
            ball.layout(0, 0, hoverBallWidth, hoverBallHeight)
            container.layout(
                hoverBallWidth,
                0,
                hoverBallWidth + containerPanelWidth,
                containerPanelHeight
            )
        } else {
            ball.layout(0, 0, hoverBallWidth, hoverBallHeight)
        }
    }
}