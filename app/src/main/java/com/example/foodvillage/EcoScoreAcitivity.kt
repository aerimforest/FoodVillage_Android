package com.example.foodvillage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foodvillage.databinding.ActivityEcoScoreBinding

class EcoScoreAcitivity : AppCompatActivity() {

    private var mBinding: ActivityEcoScoreBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // status bar 색상 변경
        val window = this.window
        window.statusBarColor = ContextCompat.getColor(this, R.color.sky)
        // statue bar 아이콘 색상 변경
        window.decorView.systemUiVisibility = 0

        // 바인딩
        mBinding = ActivityEcoScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼
        binding.btnActivityEcoScoreBack.setOnClickListener {
            this.finish()
        }
    }
}