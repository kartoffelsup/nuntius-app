package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.foundation.Icon
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import io.github.kartoffelsup.nuntius.R

@Composable
fun UserRow(
    username: String,
    color: Color = MaterialTheme.colors.secondary
) {
    Row {
        Icon(
            asset = vectorResource(R.drawable.ic_outline_person_outline_24),
            modifier = Modifier.preferredSize(20.dp, 20.dp)
                .wrapContentSize(Alignment.Center),
            tint = color
        )
        Text(
            text = username,
            style = currentTextStyle()
        )
    }
}

@Preview
@Composable
fun UserCardPreview() {
    UserRow("Testuser")
}
