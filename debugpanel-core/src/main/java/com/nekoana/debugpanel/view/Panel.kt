package com.nekoana.debugpanel.view

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.appcompat.view.ContextThemeWrapper
import com.nekoana.debugpanel.DebugPanelScope
import com.nekoana.debugpanel.core.R
import kotlin.properties.Delegates

internal fun interface OnDragCallback {
    fun onDrag(offsetX: Int, offsetY: Int)
}

private object DragHelper {
    fun attach(view: View, callback: OnDragCallback) {
        view.setOnTouchListener(DragHelperTouchListener(callback))
    }

    private class DragHelperTouchListener(callback: OnDragCallback) : View.OnTouchListener,
        OnDragCallback by callback {
        private var lastX = 0
        private var lastY = 0

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val rawX = event.rawX.toInt()
            val rawY = event.rawY.toInt()

            if (event.action == MotionEvent.ACTION_DOWN) {
                lastX = rawX
                lastY = rawY
            }

            if (event.action == MotionEvent.ACTION_MOVE) {
                onDrag(rawX - lastX, rawY - lastY)
            }

            lastX = rawX
            lastY = rawY

            return false
        }
    }
}

internal class Panel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : ViewGroup(ContextThemeWrapper(context, R.style.DebugPanel), attrs, defStyleAttr){
    private var onDragCallback: OnDragCallback? = null

    private val container = Container(context, attrs, defStyleAttr)
    private val ball = Ball(context, attrs, defStyleAttr)

    private var scope: (DebugPanelScope.() -> Unit)? = null

    init {
        addView(ball)

        DragHelper.attach(ball) { offsetX, offsetY ->
            onDragCallback?.onDrag(offsetX, offsetY)
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
                addView(container)
                scope?.invoke(container)
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

    fun setPanelScope(scope: DebugPanelScope.() -> Unit) {
        this@Panel.scope = scope
    }

    fun setOnDragCallback(callback: OnDragCallback) {
        onDragCallback = callback
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

internal fun panel(context: Context,scope: DebugPanelScope.() -> Unit, onDragCallback: OnDragCallback) = Panel(context).apply {
    setOnDragCallback(onDragCallback)
    setPanelScope(scope)
}