package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.omarshehe.forminput.compose.ui.model.Dimens
import com.omarshehe.forminput.compose.ui.model.FormInputButtonUiState
import com.omarshehe.forminput.compose.ui.model.FormInputButtonUiState.Idle
import com.omarshehe.forminput.compose.ui.model.FormInputButtonUiState.Loading
import com.omarshehe.forminput.compose.ui.model.WhenLoading
import com.omarshehe.forminput.compose.ui.model.isLoading

@Composable
fun FormInputButton(
    modifier: Modifier = Modifier,
    uiState: FormInputButtonUiState,
    iconResourceId: Int? = null,
    shape: Shape = MaterialTheme.shapes.large,
    isEnable: Boolean = true,
    onClick: () -> Unit
) {
    val enabledState by remember(uiState, isEnable) {
        mutableStateOf(if (uiState.isLoading()) uiState.isLoading().not() else isEnable)
    }
    Button(
        onClick = onClick,
        enabled = enabledState,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = colorScheme.primary.copy(0.5f),
            disabledContentColor = colorScheme.onPrimary.copy(0.5f)
        ),
        shape = shape,
        modifier = modifier.defaultMinSize(minHeight = Dimens.buttonHeight)
    ) {
        iconResourceId?.apply {
            Icon(
                painter = painterResource(this),
                contentDescription = "SignInButton",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(Dimens.normal))
        }

        Text(
            text = uiState.text.asText(),
            softWrap = false,
            style = MaterialTheme.typography.labelLarge
        )

        uiState.WhenLoading {
            Spacer(modifier = Modifier.width(Dimens.normal))
            CircularProgressIndicator(
                color = colorScheme.primary,
                strokeWidth = Dimens.strokeNormal,
                modifier = Modifier.size(Dimens.large)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ButtonPreview() {
    var buttonState by remember {
        mutableStateOf<FormInputButtonUiState>(Idle("Click me"))
    }
    FormInputButton(Modifier, buttonState) {
        buttonState = Loading("Loading...")
    }
}
