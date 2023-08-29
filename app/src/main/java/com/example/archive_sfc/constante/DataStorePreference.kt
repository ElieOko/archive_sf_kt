package com.example.archive_sfc.constante

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

object DataStorePreference {

    val Context.dataStore by preferencesDataStore("user_preferences")

}