package com.example.foodvillage

import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
//xml이 없으면 viewbinding에 activity가 안뜨나?

class StoreAdapter(val storeList : ArrayList<StoreInfo> ) : RecyclerView.Adapter<StoreAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreAdapter.CustomViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_store_list_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener{
                val curPos: Int = adapterPosition
                val store: StoreInfo = storeList.get(curPos)
            }

        }

    }

    // view를 실제 어댑터에 연결
    override fun onBindViewHolder(holder: StoreAdapter.CustomViewHolder, position: Int) {
        holder.storeImage.setImageResource(storeList.get(position).storeImage)
        holder.name.text = storeList.get(position).name
        holder.dist.text = storeList.get(position).distance
        holder.review.text = storeList.get(position).review
        holder.prodNum.text = storeList.get(position).prodNum
        holder.category.text = storeList.get(position).category
        holder.sale.text = storeList.get(position).sale
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage = itemView.findViewById<ImageView>(R.id.iv_storeImage)
        val name = itemView.findViewById<TextView>(R.id.tv_storeName) // 가게이름
        val dist = itemView.findViewById<TextView>(R.id.tv_distance) // 거리
        val review = itemView.findViewById<TextView>(R.id.tv_review) // 리뷰
        val prodNum = itemView.findViewById<TextView>(R.id.tv_prodNum) // 상품 갯수
        val category = itemView.findViewById<TextView>(R.id.tv_category) // 카테고리
        val sale = itemView.findViewById<TextView>(R.id.tv_sale) // 최대 할인률
    }

}