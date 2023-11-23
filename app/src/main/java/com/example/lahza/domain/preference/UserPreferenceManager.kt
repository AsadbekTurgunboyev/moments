package com.example.lahza.domain.preference

import android.content.Context
import android.preference.PreferenceManager
import com.example.lahza.domain.models.LoginModel

class UserPreferenceManager(private val context: Context) {

    companion object {
        val USER_NAME = "user_name"
        val KEY = "key"
        val PHONE = "phone"
        val FCM_TOKEN = "fcm_token"
    }
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)


    fun saveUserData(model: LoginModel){
        with(prefs.edit()){
            putString(USER_NAME,model.username)
            putString(KEY,model.key)
            putString(PHONE,model.phone)

        }.apply()

    }

    fun getUserKey(): String? = prefs.getString(KEY,null)
    fun saveFCMToken(result: String?) {
        prefs.edit().putString(FCM_TOKEN,result).apply()
    }

    fun getFCMToken(): String? = prefs.getString(FCM_TOKEN,null)

}