package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.currentTextStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
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
