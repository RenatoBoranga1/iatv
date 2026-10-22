package com.iatv.app.feature.live

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.iatv.app.core.designsystem.*
import com.iatv.app.core.model.EpgProgram
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable fun LiveGuide(programs: List<EpgProgram>, compact: Boolean = false) {
    var now by remember { mutableStateOf(Instant.now()) }
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) { now = Instant.now(); delay(30_000) }
        }
    }
    val guide = remember(programs, now) { presentEpg(programs, now) }
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault()) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (!compact) Text("Programação", style = MaterialTheme.typography.titleLarge)
        guide.current?.let { current ->
            Text(current.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatter.format(Instant.parse(current.startAt)), style = MaterialTheme.typography.labelMedium, color = IaTvColors.Muted)
                Text(formatter.format(Instant.parse(current.endAt)), style = MaterialTheme.typography.labelMedium, color = IaTvColors.Muted)
            }
            LinearProgressIndicator(progress = { guide.progress }, modifier = Modifier.fillMaxWidth(), color = IaTvColors.Cyan, trackColor = IaTvColors.Raised)
        } ?: Text("Programação atual indisponível", style = MaterialTheme.typography.bodyMedium, color = IaTvColors.Muted)
        Text("A seguir: ${guide.next?.title ?: "programação indisponível"}", style = MaterialTheme.typography.labelMedium, color = IaTvColors.Muted, maxLines = 2)
    }
}
