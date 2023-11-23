package com.example.lahza.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lahza.R
import com.example.lahza.databinding.ActivityHomeBinding
import com.example.lahza.domain.preference.UserPreferenceManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {

    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.homeFragmentNavHost) as NavHostFragment).navController
    }

    lateinit var viewBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.bottomNavigation.setupWithNavController(navController)

        getFCMToken()
    }

    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                saveWriteFCMToken(it.result)
            }
        }
    }

    private fun saveWriteFCMToken(result: String?) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val userPreferenceManager = UserPreferenceManager(this)
        userPreferenceManager.saveFCMToken(result)
        val data = HashMap<String, String>()
        result?.let { data.put("fcm_token", it) }
        userPreferenceManager.getUserKey()?.let { databaseReference.child(it).child("fcm_token").setValue(result) }
    }
}