package com.andryan.storyapp.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[ID] = user.id
            preferences[NAME] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[ID] ?: "",
                preferences[NAME] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        val ID = stringPreferencesKey("id")
        val NAME = stringPreferencesKey("name")
        val TOKEN_KEY = stringPreferencesKey("token")
        val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
    }
}