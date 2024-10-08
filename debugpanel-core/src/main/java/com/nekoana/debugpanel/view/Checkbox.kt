package com.nekoana.debugpanel.view

import android.content.Context
import android.widget.CompoundButton
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatCheckBox
import com.nekoana.debugpanel.core.R

fun checkbox(
    context: Context,
    isChecked: Boolean,
    text: CharSequence,
    onCheckedChange: (Boolean) -> Unit
) = AppCompatCheckBox(ContextThemeWrapper(context, R.style.DebugPanel_Checkbox)).apply {
    this.isChecked = isChecked
    this.text = text

    setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(
            buttonView: CompoundButton?,
            isChecked: Boolean
        ) {
            onCheckedChange(isChecked)
        }
    })
}