package com.nekoana.debugpanel.view

import android.content.Context
import android.widget.CompoundButton
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SwitchCompat
import com.nekoana.debugpanel.core.R

fun switch(
    context: Context,
    isChecked: Boolean,
    textOn: CharSequence,
    textOff: CharSequence,
    onCheckedChange: (Boolean) -> Unit
) = SwitchCompat(ContextThemeWrapper(context,R.style.DebugPanel_Switch)).apply {
        this.textOn = textOn
        this.textOff = textOff
        this.isChecked = isChecked

        showText = true

        setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(
                buttonView: CompoundButton?,
                isChecked: Boolean
            ) {
                onCheckedChange(isChecked)
            }
        })
    }