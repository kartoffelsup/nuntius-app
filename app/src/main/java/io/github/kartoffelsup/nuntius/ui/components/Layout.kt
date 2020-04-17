package io.github.kartoffelsup.nuntius.ui.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.*

@Composable
fun CenteredRow(
    modifier: Modifier = Modifier,
    children: @Composable() RowScope.() -> Unit
) {
    Row(modifier + Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, children = children)
}
