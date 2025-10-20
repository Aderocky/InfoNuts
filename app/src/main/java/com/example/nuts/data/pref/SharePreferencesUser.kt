package com.example.nuts.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "session")

class SharePreferencesUser(private val dataStore: DataStore<Preferences>) {
    companion object{
        private val MY_PREF_KEY = stringPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IS_PREMIUM = booleanPreferencesKey("isPremium")
        private val TOKEN = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val IS_ADMIN = booleanPreferencesKey("isAdmin")
    }

    suspend fun saveSessionData(user: SharePrefModel) {
        dataStore.edit { preferences ->
            preferences[MY_PREF_KEY] = user.id
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[IS_PREMIUM] = user.isPremium
            preferences[TOKEN] = user.token as String
            preferences[IS_LOGIN_KEY] = user.isLogin
            preferences[IS_ADMIN] = user.isAdmin
        }
    }

    suspend fun updatePremium(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PREMIUM] = isPremium
        }
    }
    suspend fun clearSessionData() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    fun getSessionData(): Flow<SharePrefModel> {
        return dataStore.data.map { preferences ->
            SharePrefModel(
                id = preferences[MY_PREF_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                isPremium = preferences[IS_PREMIUM] ?: false,
                token = preferences[TOKEN] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false,
                isAdmin = preferences[IS_ADMIN] ?: false
            )
        }
    }
}