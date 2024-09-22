package com.nekoana.debugpanel

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nekoana.debugpanel.DebugPanel
import com.nekoana.debugpanel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val debugPane = DebugPanel(this)
        val layoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, android.graphics.PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.START or Gravity.TOP

            y = 300
        }

        windowManager.addView(debugPane, layoutParams)
    }
}