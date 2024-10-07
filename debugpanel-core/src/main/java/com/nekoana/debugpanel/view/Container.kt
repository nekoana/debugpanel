package com.nekoana.debugpanel.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import com.nekoana.debugpanel.DebugPanelScope
import com.nekoana.debugpanel.core.R
import kotlin.apply


class Container(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(ContextThemeWrapper(context, R.style.DebugPanel_Container), attrs, defStyleAttr),DebugPanelScope {
    /**
     * 展开时的宽度
     */
    val expendedWidth: Int

    /**
     * 展开时的高度
     */
    val expendedHeight: Int

    private val expendedContainerBackground: Drawable

    private val containerStack = mutableListOf<View>()

    //在hoverBar为展开状态时，绘制一个圆形的cutout
    private val cutoutPaint: Paint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    init {
        val typeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.DebugPanel_Container,
            defStyleAttr,
            R.style.DebugPanel_Container
        )
        expendedWidth =
            typeArray.getDimensionPixelSize(R.styleable.DebugPanel_Container_expandedWidth, 0)
        expendedHeight =
            typeArray.getDimensionPixelSize(R.styleable.DebugPanel_Container_expandedHeight, 0)

        val defaultExpendedContainerBackground =
            AppCompatResources.getDrawable(
                context,
                R.drawable.expended_container_background
            )!!

        expendedContainerBackground =
            typeArray.getDrawable(R.styleable.DebugPanel_Container_expandedContainerBackground)
                ?: defaultExpendedContainerBackground

        typeArray.recycle()

        background = expendedContainerBackground
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = MeasureSpec.makeMeasureSpec(expendedWidth, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(expendedHeight, MeasureSpec.EXACTLY)

        super.onMeasure(widthSpec, heightSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制cutout
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        child?.let { containerStack.add(it) }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        child?.let { containerStack.remove(it) }
    }

    fun pop(): Boolean {
        val last = containerStack.lastOrNull() ?: return false
        removeView(last)
        return true
    }

    fun isEmpty() = containerStack.isEmpty()

    override fun view(view: () -> View) {
        TODO("Not yet implemented")
    }
}