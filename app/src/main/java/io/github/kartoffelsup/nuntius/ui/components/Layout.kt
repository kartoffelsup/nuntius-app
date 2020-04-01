package io.github.kartoffelsup.nuntius.ui.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.*

@Composable
fun CenteredRow(
    modifier: Modifier = Modifier.None,
    children: @Composable() RowScope.() -> Unit
) {
    Row(modifier + Modifier.fillMaxWidth(), arrangement = Arrangement.Center, children = children)
}
