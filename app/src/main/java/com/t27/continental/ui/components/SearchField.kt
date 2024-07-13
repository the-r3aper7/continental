package com.t27.continental.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchField(
    value: String,
    label: String,
    supportingText: String? = null,
    onValueChange: (q: String) -> Unit,
    leadingIcon: @Composable() (() -> Unit)? = null
) {
    OutlinedTextField(
        leadingIcon = leadingIcon,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth(),
        supportingText = {
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    color = Color.Gray
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        singleLine = true,
    )
}