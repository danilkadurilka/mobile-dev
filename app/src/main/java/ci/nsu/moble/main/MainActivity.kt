package ci.nsu.moble.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ci.nsu.moble.main.dependences.DependencesInjection
import ci.nsu.moble.main.ui.navigation.AppNavHost

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val dependencesInjection = DependencesInjection(applicationContext)
        dependencesInjection.tokenManager.clearAll()
        setContent{
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(dependencesInjection = dependencesInjection)
                }
            }
        }
    }
}