package com.zionhuang.music.lyrics

import android.content.Context
import android.util.LruCache
import com.zionhuang.music.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import com.zionhuang.music.models.MediaMetadata
import com.zionhuang.music.utils.reportException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LyricsHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val lyricsProviders = listOf(YouTubeSubtitleLyricsProvider, LrcLibLyricsProvider, KuGouLyricsProvider, YouTubeLyricsProvider)
    private val cache = LruCache<String, List<LyricsResult>>(MAX_CACHE_SIZE)

    suspend fun getLyrics(mediaMetadata: MediaMetadata): String {
        val cached = cache.get(mediaMetadata.id)?.firstOrNull()
        if (cached != null) {
            return cached.lyrics
        }

        val cleanTitle = cleanTitle(mediaMetadata.title)
        val cleanArtist = cleanArtist(mediaMetadata.artists.joinToString { it.name })

        // First pass: try all providers to find synchronized lyrics
        lyricsProviders.forEach { provider ->
            if (provider.isEnabled(context)) {
                provider.getLyrics(
                    mediaMetadata.id,
                    cleanTitle,
                    cleanArtist,
                    mediaMetadata.duration
                ).onSuccess { lyrics ->
                    if (lyrics.startsWith("[")) return lyrics
                }.onFailure {
                    reportException(it)
                }
            }
        }

        // Second pass: if no synchronized lyrics found, return the first available unsynchronized lyrics
        lyricsProviders.forEach { provider ->
            if (provider.isEnabled(context)) {
                provider.getLyrics(
                    mediaMetadata.id,
                    cleanTitle,
                    cleanArtist,
                    mediaMetadata.duration
                ).onSuccess { lyrics ->
                    if (lyrics.isNotEmpty() && lyrics != LYRICS_NOT_FOUND) return lyrics
                }.onFailure {
                    reportException(it)
                }
            }
        }
        return LYRICS_NOT_FOUND
    }

    private fun cleanTitle(title: String): String {
        return title
            .replace(Regex("\\(.*?\\)"), "") // Remove content in parentheses
            .replace(Regex("\\[.*?\\]"), "") // Remove content in brackets
            .replace(Regex("(?i)official video"), "")
            .replace(Regex("(?i)official audio"), "")
            .replace(Regex("(?i)official lyric video"), "")
            .replace(Regex("(?i)lyric video"), "")
            .replace(Regex("(?i)lyrics"), "")
            .replace(Regex("(?i)video"), "")
            .replace(Regex("(?i)ft\\..*"), "")
            .replace(Regex("(?i)feat\\..*"), "")
            .trim()
    }

    private fun cleanArtist(artist: String): String {
        return artist
            .replace(Regex("(?i)ft\\..*"), "")
            .replace(Regex("(?i)feat\\..*"), "")
            .replace(Regex("(?i),.*"), "") // Take only the first artist for better matching in some providers
            .trim()
    }

    suspend fun getAllLyrics(
        mediaId: String,
        songTitle: String,
        songArtists: String,
        duration: Int,
        callback: (LyricsResult) -> Unit,
    ) {
        val cacheKey = "$songArtists-$songTitle".replace(" ", "")
        cache.get(cacheKey)?.let { results ->
            results.forEach {
                callback(it)
            }
            return
        }
        val allResult = mutableListOf<LyricsResult>()
        lyricsProviders.forEach { provider ->
            if (provider.isEnabled(context)) {
                provider.getAllLyrics(mediaId, songTitle, songArtists, duration) { lyrics ->
                    val result = LyricsResult(provider.name, lyrics)
                    allResult += result
                    callback(result)
                }
            }
        }
        cache.put(cacheKey, allResult)
    }

    companion object {
        private const val MAX_CACHE_SIZE = 3
    }
}

data class LyricsResult(
    val providerName: String,
    val lyrics: String,
)
