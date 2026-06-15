package com.lumopos.display.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.lumopos.display.discovery.DisplayAdvertiser
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val displayAdvertiser = DisplayAdvertiser(
            context = this,
            appName = runBlocking { getString(R.string.app_name) },
            appAuthor = runBlocking { getString(R.string.app_author) },
            serviceType = "_display._tcp."
        )


        setContent {
            LaunchedEffect(
                Unit
            ) {
                displayAdvertiser.advertise()
            }
        }
    }


}

@Preview
@Composable
fun AppAndroidPreview() {
}