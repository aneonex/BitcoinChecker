package com.aneonex.bitcoinchecker.tester.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LogBox(logText: String) {
    Column {
        Text(
            text = "Logs",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(4.dp),

            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.titleMedium

        )

        BasicTextField(
            value = logText,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(4.dp),
            readOnly = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onTertiaryContainer),
            singleLine = false,
        )
    }
}

@Preview
@Composable
private fun LogBoxPreview() {
    LogBox(logText = "My text 1\nLine 222")
}