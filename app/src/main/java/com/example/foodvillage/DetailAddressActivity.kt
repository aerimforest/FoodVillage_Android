package com.example.foodvillage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityDetailAddressBinding

class DetailAddressActivity : AppCompatActivity(){

    private var mBinding: ActivityDetailAddressBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_address)

        // 바인딩
        mBinding = ActivityDetailAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 누르면 다시 현재주소설정 지도 페이지로
        binding.btnActivityCurrentAdressSettingDetailBack.setOnClickListener {
            val intent= Intent(this, CurrentAddreessActivity::class.java)
            startActivity(intent)
        }

        // 나가기 버튼
        binding.btnActivityAddressSettingDetailCancel.setOnClickListener {
            this.finish()
        }

        // 지도에서 확인하기 버튼
        binding.clyActivityCurrentAdressSettingDetailCheckmap.setOnClickListener {
            val intent= Intent(this, CurrentAddreessActivity::class.java)
            startActivity(intent)
        }


//        // 현재 위치로 설정 버튼 누르면
//        binding.clyActivityCurrentAddressDetailDecide.setOnClickListener {
//            // 주소 저장 필요
//        }

    }

}