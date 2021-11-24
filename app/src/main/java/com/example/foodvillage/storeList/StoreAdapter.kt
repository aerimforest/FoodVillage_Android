package com.example.foodvillage.storeList

import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R

class StoreAdapter(var storeList: ArrayList<StoreInfo>) :
    RecyclerView.Adapter<StoreAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_store_list_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos: Int = adapterPosition
                val store: StoreInfo = storeList[curPos]
            }
        }
    }

    // view를 실제 어댑터에 연결
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.storeImage.setImageResource(storeList[position].storeImage)
        holder.name.text = storeList[position].storeName
        holder.dist.text = storeList[position].distance
        //holder.review.text = "리뷰 "
        holder.reviewNum.text = storeList[position].reviewTotal
        //holder.product.text = "상품 수"
        holder.productNum.text = storeList[position].prodNumTotal
        holder.category.text = storeList[position].categories.toString()
        //holder.sale.text = "최대 할인율"
        holder.salePercent.text = storeList[position].salePercentMax
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage: ImageView = itemView.findViewById(R.id.iv_storeImage)
        val name: TextView = itemView.findViewById(R.id.tv_storeName) // 가게이름
        val dist: TextView = itemView.findViewById(R.id.tv_distance) // 거리

        //val review = itemView.findViewById<TextView>(R.id.tv_review) // 리뷰
        val reviewNum: TextView = itemView.findViewById(R.id.tv_review_num) // 리뷰 갯수

        //val product = itemView.findViewById<TextView>(R.id.tv_product) // 상품
        val productNum: TextView = itemView.findViewById(R.id.tv_product_num) // 상품 갯수
        val category: TextView = itemView.findViewById(R.id.tv_category) // 카테고리

        //val sale = itemView.findViewById<TextView>(R.id.tv_sale) // 최대 할인률
        val salePercent: TextView = itemView.findViewById(R.id.tv_sale_percetage)
    }

    fun datasetChanged(storeList: ArrayList<StoreInfo>) {
        this.storeList = storeList
        notifyDataSetChanged()
    }
}