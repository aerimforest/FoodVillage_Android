package com.example.foodvillage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityAddressSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class AddressSettingAcitivity : AppCompatActivity() {

    private var mBinding: ActivityAddressSettingBinding? = null
    private val binding get() = mBinding!!
    var addressData: String? = null
    var moreaddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_setting)

        // 바인딩
        mBinding = ActivityAddressSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().uid
        val DbRefUsers = mDatabase.getReference("users/$uid")

        DbRefUsers.get()
            .addOnFailureListener {}
            .addOnSuccessListener {
                val t_hashMap: HashMap<String, Any> = it.value as HashMap<String, Any>
                Log.d("유저", "hash.name: " + t_hashMap.get("name"))

                addressData = t_hashMap.get("address") as String
                moreaddress = t_hashMap.get("moreaddress")!! as String
                binding.tvAddressSettingAddress.text = ("$addressData $moreaddress")
            }

        binding.btnActivityAddressSettingCancel.setOnClickListener {
            this.finish()
        }

        // 현재 위치 설정 액티비티로 전환
        binding.imageView4.setOnClickListener {
            val intenty = Intent(this, CurrentAddressActivity::class.java)
            intenty.putExtra("etActivityDetailAddress", moreaddress)
            startActivity(intenty)
            this.finish()
        }
    }
}