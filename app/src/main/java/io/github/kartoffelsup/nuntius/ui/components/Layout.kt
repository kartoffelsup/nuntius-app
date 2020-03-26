package io.github.kartoffelsup.nuntius.ui.components

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.Arrangement
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope

@Composable
fun CenteredRow(
    modifier: Modifier = Modifier.None,
    children: @Composable() RowScope.() -> Unit
) {
    Row(modifier + LayoutWidth.Fill, arrangement = Arrangement.Center, children = children)
}
