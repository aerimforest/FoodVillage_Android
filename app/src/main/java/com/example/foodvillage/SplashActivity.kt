package com.example.foodvillage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.login.ui.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

class SplashActivity : AppCompatActivity() {

    val SPLASH_VIEW_TIME: Long = 3000 // 2초

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)
        this.supportActionBar?.hide()
        CoroutineScope(Dispatchers.IO).launch {
            delay(SPLASH_VIEW_TIME)
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}