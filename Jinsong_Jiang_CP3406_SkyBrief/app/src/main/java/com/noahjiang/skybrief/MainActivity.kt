package com.noahjiang.skybrief

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.noahjiang.skybrief.ui.UtilityApp
import com.noahjiang.skybrief.ui.theme.SkyBriefTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyBriefTheme {
                UtilityApp()
            }
        }
    }
}
