package com.example.foodvillage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodvillage.databinding.ActivityStoreListBinding

class StoreListActivity : AppCompatActivity() {

    private var mBinding: ActivityStoreListBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        mBinding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)

        val storeList = arrayListOf(
            StoreInfo(R.drawable.subway, "써브웨이 대학로점", "4.5km", "리뷰 3.9", "상품 갯수 5", "건강/다이어트", "최대 ~43%"),
            StoreInfo(R.drawable.subway, "써브웨이 명동점", "1.6km", "리뷰 4.2", "상품 갯수 9", "건강/다이어트", "최대 ~35%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로점", "3.2km", "리뷰 4.5", "상품 갯수 8", "건강/다이어트", "최대 ~43%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로2점", "3.2km", "리뷰 4.5", "상품 갯수 8", "건강/다이어트", "최대 ~43%"),
            StoreInfo(R.drawable.subway, "써브웨이 대학로점", "4.5km", "리뷰 3.9", "상품 갯수 5", "건강/다이어트", "최대 ~43%"),
            StoreInfo(R.drawable.subway, "써브웨이 명동점", "1.6km", "리뷰 4.2", "상품 갯수 9", "건강/다이어트", "최대 ~35%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로점", "3.2km", "리뷰 4.5", "상품 갯수 8", "건강/다이어트", "최대 ~43%"),
            StoreInfo(R.drawable.subway, "써브웨이 충무로2점", "3.2km", "리뷰 4.5", "상품 갯수 8", "건강/다이어트", "최대 ~43%")
        )

        binding.rvStore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvStore.setHasFixedSize(true)
        binding.rvStore.adapter = StoreAdapter(storeList)
    }
}