package com.nekoana.debugpanel.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import com.nekoana.debugpanel.core.R
import kotlin.apply

class Ball(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : View(ContextThemeWrapper(context, R.style.DebugPanel_Ball), attrs, defStyleAttr) {
    /**
     * 折叠时的尺寸
     */
    val collapsedSize: Int

    private var text = "D"

    /**
     * 折叠时的背景
     */
    private val collapsedBackground: Drawable

    private val textBounds = Rect()

    private val inAnimatorSet = AnimatorSet().apply {
        playTogether(
            ObjectAnimator.ofFloat(this@Ball, "scaleX", 1f, 0.8f),
            ObjectAnimator.ofFloat(this@Ball, "scaleY", 1f, 0.8f),
        )
        duration = 200
    }

    private val outAnimatorSet = AnimatorSet().apply {
        playTogether(
            ObjectAnimator.ofFloat(this@Ball, "scaleX", 0.8f, 1f),
            ObjectAnimator.ofFloat(this@Ball, "scaleY", 0.8f, 1f),
        )
        duration = 200
    }

    /**
     * 文字`D`画笔 在折叠状态下绘制
     */
    private val textPaint = TextPaint().apply {
        color = Color.BLACK
        isAntiAlias = true
        isFakeBoldText = true
    }

    init {
        val typeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.DebugPanel_Ball,
            defStyleAttr,
            R.style.DebugPanel_Ball
        )
        collapsedSize =
            typeArray.getDimensionPixelSize(R.styleable.DebugPanel_Ball_collapsedSize, 0)

        val defaultCollapsedBackground =
            AppCompatResources.getDrawable(context, R.drawable.collapsed_background)!!
        collapsedBackground =
            typeArray.getDrawable(R.styleable.DebugPanel_Ball_collapsedBackground)
                ?: defaultCollapsedBackground

        textPaint.textSize =
            typeArray.getDimension(R.styleable.DebugPanel_Ball_collapsedTextSize, 0f)

        typeArray.recycle()

        background = collapsedBackground
    }

    fun setDText() {
        text = "D"
        invalidate()
    }

    fun setXText() {
        text = "X"
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(collapsedSize, collapsedSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        val x = width / 2 - textBounds.centerX()
        val y = height / 2 - textBounds.centerY()

        canvas.drawText(text, x.toFloat(), y.toFloat(), textPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                inAnimatorSet.start()
                outAnimatorSet.cancel()
            }

            MotionEvent.ACTION_UP -> {
                outAnimatorSet.start()
                inAnimatorSet.cancel()
            }
        }

        return super.onTouchEvent(event)
    }
}