package io.github.kartoffelsup.nuntius.ui.message

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.core.currentTextStyle
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.px

@Composable
fun TextMessageView(
    text: String,
    bgColor: Color = Color.Blue,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier.None) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.px)
    ) {
        Text(
            modifier = LayoutPadding(5.dp) + modifier,
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
