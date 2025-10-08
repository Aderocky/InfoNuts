package com.example.nuts.data.pref

import android.content.Context
import androidx.core.content.edit

class SharePreferencesUser(private val context: Context) {
    companion object{
        private const val MY_PREF_KEY = "MY_PREF"
    }

    fun saveStringData(key: String, data: String?){
        val sharePreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        sharePreferences.edit { putString(key, data) }
    }

    fun getStringData(key: String): String? {
        val sharePreferences = context.getSharedPreferences(MY_PREF_KEY, Context.MODE_PRIVATE)
        return sharePreferences.getString(key,null)

    }
}