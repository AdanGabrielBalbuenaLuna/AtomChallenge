package com.example.atomchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.atomchallenge.feature.countries.presentation.navigation.AppNavigation
import com.example.atomchallenge.core.ui.AtomChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtomChallengeTheme {
                AppNavigation()
            }
        }
    }
}