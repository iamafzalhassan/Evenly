package org.example.evenly

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import org.example.evenly.security.AndroidBiometricAuthenticator

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.light(darkScrim = Color.TRANSPARENT, scrim = Color.TRANSPARENT), statusBarStyle = SystemBarStyle.light(darkScrim = Color.TRANSPARENT, scrim = Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        val biometricAuthenticator = AndroidBiometricAuthenticator(this)
        val graph = (application as EvenlyApplication).graph
        setContent { App(biometricAuthenticator = biometricAuthenticator, graph = graph) }
    }
}
