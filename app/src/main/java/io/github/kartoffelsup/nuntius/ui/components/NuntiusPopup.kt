package io.github.kartoffelsup.nuntius.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@Composable
fun NuntiusPopup(
    open: Boolean,
    color: Color = Color.DarkGray,
    children: @Composable() ColumnScope.() -> Unit
) {
    if (open) {
            Surface(color = color) {
                Column {
                    children()
                }
            }
    }
}
//
//@Preview
//@Composable
//fun NuntiusPopupPreview() {
//    Column(Modifier.preferredSize(200.dp)) {
//        DropdownPopup {
//            Surface(color = Color.DarkGray) {
//                Column() {
//                    Text("Hello")
//                    Text("Bye")
//                }
//            }
//        }
//    }
//}
