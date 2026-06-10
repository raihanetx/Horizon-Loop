package com.horizonloop.app.core.ui.viewmodel

import android.content.Context
import com.horizonloop.app.core.domain.model.Audio
import com.horizonloop.app.features.player.data.AudioPlayer

internal fun buildAudioPlayer(
    ctx: Context, audio: Audio,
    onProg: (Double, Double) -> Unit,
    onState: (Boolean) -> Unit,
    onDone: () -> Unit,
    onErr: (String) -> Unit
): AudioPlayer {
    val path = if (audio.contentUri.isNotBlank()) audio.contentUri else audio.filePath
    return AudioPlayer(ctx).apply {
        onProgressUpdate = { ms, tot -> onProg(ms / 1000.0, tot / 1000.0) }
        onPlaybackStateChanged = onState
        onCompletion = onDone
        onError = onErr
        load(path)
    }
}
