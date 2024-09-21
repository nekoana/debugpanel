package com.nekoana.debugpanel

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nekoana.debugpanel.core.DebugPanel
import com.nekoana.debugpanel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val debugPane = DebugPanel(this)

        val layoutParams = WindowManager.LayoutParams(100,100,WindowManager.LayoutParams.TYPE_APPLICATION,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, android.graphics.PixelFormat.TRANSLUCENT).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.START
        }

        windowManager.addView(debugPane, layoutParams)
    }
}