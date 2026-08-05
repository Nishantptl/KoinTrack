package com.nishant.kointrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nishant.kointrack.ui.navigation.KoinTrackNavHost
import com.nishant.kointrack.ui.theme.KoinTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinTrackTheme {
                KoinTrackNavHost()
            }
        }
    }
}