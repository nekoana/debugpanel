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
        DebugPanel(this, this) {
            scroller {
                list {
                    checkbox {
                        isChecked = false
                        text = "A"
                        onCheckedChange = { c ->
                            Toast.makeText(
                                this@MainActivity,
                                isChecked.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }

                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }

                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }

                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }
                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }
                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }
                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }
                    }
                    button {
                        text = "A"
                        onClick = {
                            Toast.makeText(this@MainActivity, "A", Toast.LENGTH_SHORT).show()
                        }
                    }

                    checkbox {
                        text = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
                        isChecked = true
                        onCheckedChange = { isChecked ->
                            Toast.makeText(
                                this@MainActivity,
                                isChecked.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}