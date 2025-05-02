package com.example.testing4.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



val Context.dataStore by preferencesDataStore(name = "pref")

class DataStoreManager(private val context: Context) {


    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_CONTACT = stringPreferencesKey("user_contact")
        private val USER_PASSWORD = stringPreferencesKey("user_password")
        private val LOGGED_IN = booleanPreferencesKey("logged_in")
    }

    suspend fun saveOnboardingComplete(completed: Boolean) {
        context.dataStore.edit { pref ->
            pref[ONBOARDING_COMPLETED] = completed
        }
    }

    val onBoardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { pref ->
            pref[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { pref ->
            pref[USER_NAME] = name
        }
    }

    val getUserName: Flow<String> = context.dataStore.data
        .map { pref ->
            pref[USER_NAME] ?: ""
        }

    suspend fun hasLoggedIn(loggedIN: Boolean) {
        context.dataStore.edit { pref ->
            pref[LOGGED_IN] = loggedIN
        }
    }

    val getLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { pref ->
            pref[LOGGED_IN] ?: false
        }
}