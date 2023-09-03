package com.example.archive_sfc.datastoreprefence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PrefsDataStoreScreenViewModel (
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    /*...*/

    fun readValue(key: String): StateFlow<Any> {
        val keys = stringPreferencesKey(key)
        val state = dataStore.data.map { preferences ->
            preferences[keys] ?: false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )
        return state
    }


    fun saveValue(value: String,key:String) {
        val keys = stringPreferencesKey(key)
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[keys] = value
            }
        }
    }
}