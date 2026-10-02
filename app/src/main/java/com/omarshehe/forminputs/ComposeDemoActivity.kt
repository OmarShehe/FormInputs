package com.omarshehe.forminputs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.FormInputDefaults
import com.omarshehe.forminput.compose.ui.FormInputStrings
import com.omarshehe.forminput.compose.ui.FormInputTheme

/** Every forminput input on one screen, for trying on a device. */
class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var classic by remember { mutableStateOf(false) }
            var french by remember { mutableStateOf(false) }
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(ScreenPadding),
                        verticalArrangement = Arrangement.spacedBy(SectionGap),
                    ) {
                        Text("forminput", style = MaterialTheme.typography.headlineSmall)
                        DemoSwitch("Classic style (formInputModifier)", classic) { classic = it }
                        DemoSwitch("Own texts (FormInputStrings, French)", french) { french = it }
                        DemoForm(classic = classic, french = french)
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(SwitchGap)) {
        Switch(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}

/** All the sections under one `FormInputTheme`: style and shape are set here once, so no field repeats them. */
@Composable
private fun DemoForm(classic: Boolean, french: Boolean) {
    val look = demoLook(classic)
    val defaults = FormInputDefaults(style = look.style, shape = look.shape, strings = if (french) frenchStrings else FormInputStrings())
    FormInputTheme(defaults) {
        // The field names are the app's own text, so they follow the French switch through tr().
        CompositionLocalProvider(LocalFrench provides french) {
            Column(verticalArrangement = Arrangement.spacedBy(SectionGap)) {
                TextFieldsSection(look)
                PriceAndPasswordSection(look)
                DropdownsSection(look)
                PickersSection(look)
                ButtonAndStepperSection(look)
                UploadsSection()
                GeneratedFormSection(look)
            }
        }
    }
}
