package com.example.foodvillage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
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

        setViewBinding()

        setInitialFragment()

        setBottomNavigation()

        setUserInfoToDB()
    }

    private fun setViewBinding() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setInitialFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.main_screen_panel, homeFragment)
            .commit()
    }

    private fun setBottomNavigation() {
        binding.bottomNavigation.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                changeFragmentWithSelectedTab(newIndex)
            }
        })
    }

    private fun changeFragmentWithSelectedTab(newIndex: Int) {
        when (newIndex) {
            0 -> replaceFragment(HomeFragment())
            1 -> replaceFragment(DibFragment())
            2 -> replaceFragment(AroundFragment())
            3 -> replaceFragment(MyPageFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_screen_panel, fragment).commit()
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