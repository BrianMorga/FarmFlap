package com.example.farmflap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorTheme = lightColorScheme(

)

@Composable
fun FarmFlapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorTheme,
        content = content
    )
}