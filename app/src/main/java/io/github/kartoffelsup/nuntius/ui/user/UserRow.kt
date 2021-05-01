package io.github.kartoffelsup.nuntius.ui.user

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kartoffelsup.nuntius.R

@Composable
fun UserRow(
    username: String,
    color: Color = MaterialTheme.colors.secondary
) {
    Row {
        Icon(
            painter = painterResource(R.drawable.ic_outline_person_outline_24),
            modifier = Modifier.size(20.dp, 20.dp)
                .wrapContentSize(Alignment.Center),
            tint = color,
            contentDescription = null
        )
        Text(
            text = username
        )

    }
}

@Preview
@Composable
fun UserCardPreview() {
    UserRow("Testuser")
}
