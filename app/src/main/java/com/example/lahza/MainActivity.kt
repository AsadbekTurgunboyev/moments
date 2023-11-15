package com.example.lahza

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lahza.domain.preference.UserPreferenceManager
import com.example.lahza.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    lateinit var preferenceManager: UserPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = UserPreferenceManager(this)
        if(!preferenceManager.getUserKey().isNullOrEmpty()){
            val intent = Intent(this,HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        setContentView(R.layout.activity_main)
    }


}