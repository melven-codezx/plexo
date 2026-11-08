package com.melven.plexo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.melven.plexo.ui.PlexoRoot
import com.melven.plexo.ui.theme.PlexoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlexoTheme {
                PlexoRoot()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // O usuario pode ter instalado/desinstalado apps entretanto.
        PlexoApp.instance.catalog.invalidate()
    }
}
