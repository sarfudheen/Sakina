package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.DhikrItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object SakinaAudioPlayer {
    private const val TAG = "SakinaAudioPlayer"

    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId: StateFlow<String?> = _currentPlayingId.asStateFlow()

    private val _playbackRate = MutableStateFlow(1.0f)
    val playbackRate: StateFlow<Float> = _playbackRate.asStateFlow()

    fun initialize(context: Context) {
        if (textToSpeech != null) return
        val appContext = context.applicationContext
        textToSpeech = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val arLocale = Locale("ar")
                val result = textToSpeech?.setLanguage(arLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "Arabic TTS language missing or not supported on this device")
                } else {
                    isTtsInitialized = true
                }
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlaying.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isPlaying.value = false
                        _currentPlayingId.value = null
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isPlaying.value = false
                        _currentPlayingId.value = null
                    }
                })
            } else {
                Log.e(TAG, "Failed to initialize TextToSpeech")
            }
        }
    }

    fun setPlaybackRate(rate: Float) {
        _playbackRate.value = rate
        textToSpeech?.setSpeechRate(rate)
    }

    fun playDhikr(context: Context, dhikr: DhikrItem) {
        initialize(context)

        // If already playing this item, toggle stop
        if (_isPlaying.value && _currentPlayingId.value == dhikr.id) {
            stop()
            return
        }

        stop()
        _currentPlayingId.value = dhikr.id
        _isPlaying.value = true

        // If audioUrl is provided, attempt MediaPlayer
        val audioUrl = dhikr.audioUrl
        if (!audioUrl.isNullOrBlank()) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(audioUrl)
                    setOnPreparedListener { mp ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                val params = mp.playbackParams
                                params.speed = _playbackRate.value
                                mp.playbackParams = params
                            } catch (_: Exception) {}
                        }
                        mp.start()
                    }
                    setOnCompletionListener {
                        _isPlaying.value = false
                        _currentPlayingId.value = null
                        releasePlayer()
                    }
                    setOnErrorListener { _, _, _ ->
                        fallbackToTts(dhikr)
                        true
                    }
                    prepareAsync()
                }
                return
            } catch (e: Exception) {
                Log.e(TAG, "MediaPlayer failed, falling back to TTS", e)
                fallbackToTts(dhikr)
            }
        } else {
            fallbackToTts(dhikr)
        }
    }

    private fun fallbackToTts(dhikr: DhikrItem) {
        val tts = textToSpeech
        if (tts != null) {
            tts.setSpeechRate(_playbackRate.value)
            val textToSpeak = if (dhikr.verseArabic.isNotBlank()) dhikr.verseArabic else dhikr.arabic
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, dhikr.id)
            }
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, dhikr.id)
        } else {
            _isPlaying.value = false
            _currentPlayingId.value = null
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            releasePlayer()
        } catch (_: Exception) {}

        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}

        _isPlaying.value = false
        _currentPlayingId.value = null
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
