package it.unibo.almamensa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import it.unibo.almamensa.ui.screens.base.BaseScreen
import it.unibo.almamensa.ui.theme.AlmaMensaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlmaMensaTheme {
                BaseScreen();
            }
        }
    }
}
