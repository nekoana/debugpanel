package com.nekoana.debugpanel.core

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
import android.util.Log
import android.view.Gravity.CENTER
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.properties.Delegates

class DebugPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attrs, R.style.DebugPanel) {

    private val containerPanel = ContainerPanel(context, attrs, defStyleAttr)
    private val hoverBall = HoverBall(context, attrs, defStyleAttr)

    init {
        addView(hoverBall)
        //透明背景
        setBackgroundColor(Color.TRANSPARENT)
        layoutTransition = LayoutTransition()
        orientation = HORIZONTAL
    }

    /**
     * 是否展开
     */
    private var isExpanded: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            if (newValue) {
                addView(containerPanel)
            } else {
                removeView(containerPanel)
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

    //todo 通过ViewDragHelper实现拖动功能
    private val dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(
            child: View,
            pointerId: Int
        ): Boolean {
            //是否允许view的拖动功能，返回true是允许拖动，返回false是不允许拖动
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            //控制横向方向的拖动位移，如果不重写此方法默认是不允许横向运动的，按照下面重写方法后可以允许横向方向的拖动
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            //控制垂直方向的拖动位移，如果不重写此方法默认是不允许垂直运动的，重写方法后可以允许垂直方向的拖动
            return top
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            elevation += 20
        }

        override fun onViewDragStateChanged(state: Int) {
            log("onViewDragStateChanged: $state")
            if (state == ViewDragHelper.STATE_IDLE) {
                elevation -= 20
            }
        }
    })

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

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
        super.onLayout(changed, l, t, r, b)
        log("onLayout: $changed, $l, $t, $r, $b")
        //todo 保持 hoverBall 在原位置
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //todo 绘制cutout
    }


    private fun log(message: String) {
        Log.d(TAG, message)
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


        private val inAnimatorSet =  AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(this@HoverBall, "scaleX", 1f, 0.8f),
                    ObjectAnimator.ofFloat(this@HoverBall, "scaleY", 1f, 0.8f),
                )
                duration = 200
            }

        private val outAnimatorSet =  AnimatorSet().apply {
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
            gravity = CENTER
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(collapsedSize, collapsedSize)
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    outAnimatorSet.cancel()
                    inAnimatorSet.start()
                }
                MotionEvent.ACTION_UP -> {
                    inAnimatorSet.cancel()
                    outAnimatorSet.start()
                }
            }
            return super.onTouchEvent(event)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            //绘制“D”
            val text = if (!isExpanded) {
                "D"
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