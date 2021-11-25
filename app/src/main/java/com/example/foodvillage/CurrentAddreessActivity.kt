package com.example.foodvillage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityCurrentAddressBinding

class CurrentAddreessActivity :AppCompatActivity(){
    private var mBinding: ActivityCurrentAddressBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_address)

        // 바인딩
        mBinding = ActivityCurrentAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상세 주소 설정 페이지로
        binding.btnActivityCurrentAddressSetting.setOnClickListener {
            val intent= Intent(this, DetailAddressActivity::class.java)
            startActivity(intent)
        }


    }
}