package org.example.evenly

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.light(darkScrim = Color.TRANSPARENT, scrim = Color.TRANSPARENT), statusBarStyle = SystemBarStyle.light(darkScrim = Color.TRANSPARENT, scrim = Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        val graph = (application as EvenlyApplication).graph
        setContent { App(graph = graph) }
    }
}
