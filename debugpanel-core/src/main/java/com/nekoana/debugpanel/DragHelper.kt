package com.nekoana.debugpanel

import android.view.MotionEvent
import android.view.View

fun interface OnDragCallback {
    fun onDrag(offsetX: Int, offsetY: Int)
}

fun attach(view: View, callback: OnDragCallback) {
    view.setOnTouchListener(DragHelperTouchListener(callback))
}

private class DragHelperTouchListener(callback: OnDragCallback) :
    View.OnTouchListener, OnDragCallback by callback {

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