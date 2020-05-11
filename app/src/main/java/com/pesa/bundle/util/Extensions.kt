package com.pesa.bundle.util

import android.widget.Button
import androidx.core.content.ContextCompat
import com.pesa.bundle.R


//Change State Button
fun Button.changeState(state: String) {
    when (state) {
        Constants.STATE_ENABLED -> {
            this.setEnabled(true)
//                ContextCompat sorts for >M
            this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
        Constants.STATE_DISABLED -> {
            this.setEnabled(false)
            this.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.darker_gray
                )
            )
        }

    }
}

