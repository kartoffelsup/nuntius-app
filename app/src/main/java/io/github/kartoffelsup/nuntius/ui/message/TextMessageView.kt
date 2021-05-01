package io.github.kartoffelsup.nuntius.ui.message

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun TextMessageView(
    text: String,
    modifier: Modifier = Modifier,
    bgColor: Color = Color.Blue,
    textColor: Color = Color.White
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(5.dp).then(modifier),
            text = text,
            style = LocalTextStyle.current.copy(color = textColor)
        )
    }
}

@Preview
@Composable
fun TextMessageViewPreview() {
    TextMessageView("Test")
}
