package com.example.foodvillage

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import com.example.foodvillage.databinding.ActivityEcoScoreBinding

class EcoScoreAcitivity : AppCompatActivity() {

    private var mBinding: ActivityEcoScoreBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // 바인딩
        mBinding = ActivityEcoScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼
        binding.btnActivityEcoScoreBack.setOnClickListener{
            this.finish()
        }
    }

}