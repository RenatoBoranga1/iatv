package com.iatv.app.core.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.iatv.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Original bundled art for demo IDs only. Unknown provider content keeps the neutral fallback. */
private fun artworkResource(id: String): Int? = when(id) {
    "movie-orbita" -> R.drawable.iatv_horizon
    "movie-mar", "series-horizonte", "channel-mundo" -> R.drawable.iatv_coast
    "movie-domingo", "series-bairro", "channel-aurora" -> R.drawable.iatv_neighborhood
    else -> null
}

private val artworkCache = object : LruCache<String, Bitmap>(8 * 1024) {
    override fun sizeOf(key: String, value: Bitmap) = (value.allocationByteCount / 1024).coerceAtLeast(1)
}

@Composable fun IaTvBackdrop(modifier: Modifier = Modifier, id: String = "movie-orbita", thumbnail: Boolean = false) {
    val resource = artworkResource(id)
    val resources = LocalContext.current.resources
    val sample = if(thumbnail) 4 else 2
    val cacheKey = "$resource/$sample"
    val bitmap by produceState(artworkCache.get(cacheKey), resource, sample) {
        value = artworkCache.get(cacheKey)
        if(value == null && resource != null) value = withContext(Dispatchers.IO) {
            BitmapFactory.decodeResource(resources, resource, BitmapFactory.Options().apply { inSampleSize = sample })
                ?.also { artworkCache.put(cacheKey, it) }
        }
    }
    val image = bitmap
    if(image == null) IaTvArtwork(id, modifier)
    else Image(image.asImageBitmap(), null, modifier, contentScale = ContentScale.Crop)
}
