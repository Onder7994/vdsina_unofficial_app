package com.andtree.vdsina.utils

import androidx.compose.ui.graphics.Color
import com.andtree.vdsina.ui.theme.Green
import com.andtree.vdsina.ui.theme.Red


object VerticalProgressHelper {
    fun getProgress(serverStatus: String): Float {
        return if (serverStatus == "active") 1.0f
        else 0.0f
    }

    fun getProgressColor(progress: Float): Color {
        if (progress == 1.0f){
            return Green
        } else {
            return Red
        }
    }
}