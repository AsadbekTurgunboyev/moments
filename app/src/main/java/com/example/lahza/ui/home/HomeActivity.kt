package com.example.lahza.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lahza.R
import com.example.lahza.databinding.ActivityHomeBinding
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

    fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("tekshirish", "getFCMToken: ${it.result}")
            }
        }
    }
}