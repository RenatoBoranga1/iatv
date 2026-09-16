package com.iatv.app.core.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iatv.app.core.model.ContentCard
@Composable fun TvCard(item: ContentCard, modifier: Modifier = Modifier, onFocus: () -> Unit = {}, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Column(modifier.width(225.dp).height(148.dp).onFocusChanged { focused = it.isFocused; if(it.isFocused) onFocus() }
        .border(if(focused) 3.dp else 1.dp, if(focused) Color(0xFF65E4C5) else Color(0xFF344052), shape)
        .background(Brush.linearGradient(listOf(Color(0xFF234453), Color(0xFF171D30))), shape)
        .clickable(onClick = onClick).padding(16.dp)) {
        Text(item.badge, color = Color(0xFF8DEBD2), fontSize = 11.sp, maxLines = 1)
        Spacer(Modifier.weight(1f))
        Text(item.title, color = Color.White, fontSize = 20.sp, maxLines = 2)
        Text(item.category, color = Color(0xFFB5C1D2), fontSize = 12.sp)
    }
}
