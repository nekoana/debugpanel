package com.nekoana.debugpanel

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.nekoana.debugpanel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DebugPanel(this, this){
            list {
                button("Hello"){
                    Toast.makeText(this@MainActivity, "Hi", Toast.LENGTH_SHORT).show()
                }
                button("World!") {
                    Toast.makeText(this@MainActivity, "World", Toast.LENGTH_SHORT).show()
                }

                checkbox(true,"World!") {
                    Toast.makeText(this@MainActivity, "isChecked $it", Toast.LENGTH_SHORT).show()
                }

                view {
                    TextView(this@MainActivity).apply {
                        text = "Hello World"
                    }
                }

                view {
                    MaterialButton(this@MainActivity).apply {
                        text = "Material Button"
                    }
                }
            }

            button("Hello"){
                Toast.makeText(this@MainActivity, "Hi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}