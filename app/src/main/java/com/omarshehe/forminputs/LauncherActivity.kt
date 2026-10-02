package com.omarshehe.forminputs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** First screen: choose which form-input library to try. */
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("FormInputs", style = MaterialTheme.typography.headlineLarge)
                        Text("Choose a sample", style = MaterialTheme.typography.bodyLarge)
                        Button(
                            onClick = { startActivity(Intent(this@LauncherActivity, ComposeDemoActivity::class.java)) },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Compose (forminput)") }
                        OutlinedButton(
                            onClick = { startActivity(Intent(this@LauncherActivity, MainActivity::class.java)) },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("View system (forminputkotlin)") }
                    }
                }
            }
        }
    }
}
