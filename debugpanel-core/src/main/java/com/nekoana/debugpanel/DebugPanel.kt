package com.nekoana.debugpanel

import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import com.nekoana.debugpanel.core.R
import kotlin.apply
import kotlin.properties.Delegates

class DebugPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, R.style.DebugPanel) {

    private val containerPanel = ContainerPanel(context, attrs, defStyleAttr)
    private val hoverBall = HoverBall(context, attrs, defStyleAttr)

    init {
        super.addView(hoverBall)
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
                super.addView(containerPanel)
            } else {
                super.removeView(containerPanel)
            }

            postInvalidate()
            hoverBall.postInvalidate()
        }
    }

    init {
        hoverBall.setOnClickListener {
            isExpanded = !isExpanded
        }
    }

    override fun addView(view: View) {
        containerPanel.addView(view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        hoverBall.measure(widthMeasureSpec, heightMeasureSpec)
        containerPanel.measure(widthMeasureSpec, heightMeasureSpec)

        if (isExpanded) {
            setMeasuredDimension(
                hoverBall.collapsedSize + containerPanel.expendedWidth,
                containerPanel.expendedHeight
            )
        } else {
            setMeasuredDimension(hoverBall.collapsedSize, hoverBall.collapsedSize)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val hoverBallWidth = hoverBall.measuredWidth
        val hoverBallHeight = hoverBall.measuredHeight

        val containerPanelWidth = containerPanel.measuredWidth
        val containerPanelHeight = containerPanel.measuredHeight

        if (isExpanded) {
            hoverBall.layout(0, 0, hoverBallWidth, hoverBallHeight)
            containerPanel.layout(
                hoverBallWidth,
                0,
                hoverBallWidth + containerPanelWidth,
                containerPanelHeight
            )
        } else {
            hoverBall.layout(0, 0, hoverBallWidth, hoverBallHeight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //todo 绘制cutout
    }

    private inner class HoverBall(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        /**
         * 折叠时的尺寸
         */
        val collapsedSize: Int

        /**
         * 折叠时的背景
         */
        private val collapsedBackground: Drawable

        private val textBounds = Rect()

        private val inAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(this@HoverBall, "scaleX", 1f, 0.8f),
                ObjectAnimator.ofFloat(this@HoverBall, "scaleY", 1f, 0.8f),
            )
            duration = 200
        }

        private val outAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(this@HoverBall, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(this@HoverBall, "scaleY", 0.8f, 1f),
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
                R.styleable.DebugPanel,
                defStyleAttr,
                R.style.DebugPanel
            )
            collapsedSize = typeArray.getDimensionPixelSize(R.styleable.DebugPanel_collapsedSize, 0)

            val defaultCollapsedBackground =
                AppCompatResources.getDrawable(context, R.drawable.collapsed_background)!!
            collapsedBackground = typeArray.getDrawable(R.styleable.DebugPanel_collapsedBackground)
                ?: defaultCollapsedBackground

            textPaint.textSize =
                typeArray.getDimension(R.styleable.DebugPanel_collapsedTextSize, 0f)

            typeArray.recycle()

            background = collapsedBackground
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(collapsedSize, collapsedSize)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            //绘制“D”
            val text = if (!isExpanded) {
                "D"
            } else if(containerPanel.getChildCount() > 0){
                "<"
            } else {
                "X"
            }

            textPaint.getTextBounds(text, 0, text.length, textBounds)

            val x = width / 2 - textBounds.centerX()
            val y = height / 2 - textBounds.centerY()

            canvas.drawText(text, x.toFloat(), y.toFloat(), textPaint)
        }
    }

    private inner class ContainerPanel(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
    ) : FrameLayout(context, attrs, defStyleAttr) {
        /**
         * 展开时的宽度
         */
        val expendedWidth: Int

        /**
         * 展开时的高度
         */
        val expendedHeight: Int

        //在hoverBar为展开状态时，绘制一个圆形的cutout
        private val cutoutPaint: Paint = Paint().apply {
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        private val expendedContainerBackground: Drawable

        init {
            val typeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.DebugPanel,
                defStyleAttr,
                R.style.DebugPanel
            )
            expendedWidth = typeArray.getDimensionPixelSize(R.styleable.DebugPanel_expandedWidth, 0)
            expendedHeight =
                typeArray.getDimensionPixelSize(R.styleable.DebugPanel_expandedHeight, 0)

            val defaultExpendedContainerBackground =
                AppCompatResources.getDrawable(context, R.drawable.expended_container_background)!!

            expendedContainerBackground =
                typeArray.getDrawable(R.styleable.DebugPanel_expandedContainerBackground)
                    ?: defaultExpendedContainerBackground

            typeArray.recycle()

            background = expendedContainerBackground
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(expendedWidth, expendedHeight)
        }
    }

    companion object {
        private const val TAG = "DebugPanel"
    }

}