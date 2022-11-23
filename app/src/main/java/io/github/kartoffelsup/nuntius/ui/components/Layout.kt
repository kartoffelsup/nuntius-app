package io.github.kartoffelsup.nuntius.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun CenteredRow(
    modifier: Modifier = Modifier,
    children: @Composable() RowScope.() -> Unit
) {
    Row(
        modifier.then(Modifier.fillMaxWidth()),
        horizontalArrangement = Arrangement.Center
    ) { children() }
}
