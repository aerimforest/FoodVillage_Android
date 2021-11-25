package com.example.foodvillage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityAddressSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class AddressSettingAcitivity : AppCompatActivity(){

    private var mBinding: ActivityAddressSettingBinding? = null
    private val binding get() = mBinding!!
    var addressData:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_setting)

        // 바인딩
        mBinding = ActivityAddressSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mDatabase = FirebaseDatabase.getInstance()
        val uid= FirebaseAuth.getInstance().uid
        val DbRefUsers = mDatabase.getReference("users/" + uid)

        DbRefUsers.get()
            .addOnFailureListener {}
            .addOnSuccessListener {
                var t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
                Log.d("유저", "hash.name: " + t_hashMap.get("name"))

                addressData = t_hashMap.get("address") as String
                binding.tvAddressSettingAddress.text = addressData
            }


        binding.imageView4.setOnClickListener {
            intent= Intent(this, MyMapActivity::class.java)
            startActivity(intent)
        }

        binding.btnActivityAddressSettingCancel.setOnClickListener{
            this.finish()
        }


    }
}