package com.example.ui.i18n

import android.content.Context
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val nativeName: String, val englishName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", "English", false),
    ARABIC("ar", "العربية", "Arabic", true);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}

object SakinaLocaleManager {

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private var appContext: Context? = null
    private const val PREFS_NAME = "sakina_locale_prefs"
    private const val KEY_APP_LANGUAGE = "selected_app_language"

    fun init(context: Context) {
        appContext = context.applicationContext
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val code = prefs.getString(KEY_APP_LANGUAGE, "en") ?: "en"
            _currentLanguage.value = AppLanguage.fromCode(code)
        } catch (_: Exception) {}
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_APP_LANGUAGE, language.code).apply()
        } catch (_: Exception) {}
    }

    fun toggleLanguage() {
        val next = if (_currentLanguage.value == AppLanguage.ENGLISH) AppLanguage.ARABIC else AppLanguage.ENGLISH
        setLanguage(next)
    }

    val isArabic: Boolean
        get() = _currentLanguage.value == AppLanguage.ARABIC

    val layoutDirection: LayoutDirection
        get() = if (_currentLanguage.value.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    fun getString(en: String, ar: String): String {
        return if (isArabic) ar else en
    }
}
