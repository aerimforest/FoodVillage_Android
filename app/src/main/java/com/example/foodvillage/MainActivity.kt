package com.example.foodvillage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodvillage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainactivityTomymap.setOnClickListener{
            val intent= Intent(this@MainActivity, MyMapActivity::class.java)
            startActivity(intent)
        }
        binding.btnMainactivityTomarketmap.setOnClickListener{
            val intent= Intent(this@MainActivity, MarketMapActivity::class.java)
            startActivity(intent)
        }
    }
}