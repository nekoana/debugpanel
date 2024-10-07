package com.nekoana.debugpanel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nekoana.debugpanel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DebugPanel(this, this){
            button("Hello"){
                Toast.makeText(this@MainActivity, "Hi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}