package io.github.kartoffelsup.nuntius.ui.message

import androidx.compose.foundation.Text
import androidx.compose.foundation.currentTextStyle
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview


@Composable
fun TextMessageView(
    text: String,
    bgColor: Color = Color.Blue,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(5.dp).then(modifier),
            text = text,
            style = currentTextStyle().copy(color = textColor)
        )
    }
}

@Preview
@Composable
fun TextMessageViewPreview() {
    TextMessageView("Test")
}
