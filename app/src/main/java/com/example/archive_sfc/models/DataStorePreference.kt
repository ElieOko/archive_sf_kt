package com.example.archive_sfc.models

//import android.content.Context
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import com.example.archive_sfc.constante.DataStorePreference
//import com.example.archive_sfc.models.room.User
//
//class DataStorePreference(context: Context) {
//    val dataStore by preferencesDataStore("user_preferences")
//
//    private suspend fun saveDataStore(key:String,value: User){
//        val storeKey: Preferences.Key<String> = stringPreferencesKey(key)
//        dataStore.edit { settings->
//            settings[storeKey] = value.toString()
//        }
//    }
//}