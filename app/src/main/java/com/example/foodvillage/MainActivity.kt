package com.example.foodvillage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodvillage.databinding.ActivityMainBinding
import com.example.foodvillage.login.data.UserInfoData
import com.example.foodvillage.menu.AroundFragment
import com.example.foodvillage.menu.DibFragment
import com.example.foodvillage.menu.HomeFragment
import com.example.foodvillage.menu.MyPageFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUserInfoToDB()

        // Bottom Navigation
        binding.bottomNavigation.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        val homeFragment = HomeFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, homeFragment).commit()
                    }
                    1 -> {
                        val dibFragment = DibFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, dibFragment).commit()
                    }

                    2 -> {
                        val aroundFragment = AroundFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, aroundFragment).commit()
                    }
                    3 -> {
                        val myPageFragment = MyPageFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_screen_panel, myPageFragment).commit()
                    }
                }
            }
        })

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_screen_panel, homeFragment)
            .commit()
    }

    private fun setUserInfoToDB() {
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference = firebaseDatabase.getReference("users")
        val fbAuth: FirebaseAuth?

        val userInfoData = UserInfoData(
            "test",
            0,
            0,
            0
        )

        fbAuth = FirebaseAuth.getInstance()
        databaseReference.child(fbAuth.uid.toString()).setValue(userInfoData)
    }
}