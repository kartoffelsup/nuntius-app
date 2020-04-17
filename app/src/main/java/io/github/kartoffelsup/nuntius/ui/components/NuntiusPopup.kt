package io.github.kartoffelsup.nuntius.ui.components

import androidx.compose.Composable
import androidx.ui.core.DropDownAlignment
import androidx.ui.core.DropdownPopup
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.ColumnScope
import androidx.ui.layout.preferredSize
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Composable
fun NuntiusPopup(
    open: Boolean,
    color: Color = Color.DarkGray,
    children: @Composable() ColumnScope.() -> Unit
) {
    if (open) {
        DropdownPopup(dropDownAlignment = DropDownAlignment.Start) {
            Surface(color = color) {
                Column() {
                    children()
                }
            }
        }
    }
}

@Preview
@Composable
fun NuntiusPopupPreview() {
    Column(Modifier.preferredSize(200.dp)) {
        DropdownPopup {
            Surface(color = Color.DarkGray) {
                Column() {
                    Text("Hello")
                    Text("Bye")
                }
            }
        }
    }
}
