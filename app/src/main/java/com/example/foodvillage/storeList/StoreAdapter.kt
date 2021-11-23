package com.example.foodvillage.storeList

import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R
import com.google.android.gms.common.util.ArrayUtils.removeAll

//xml이 없으면 viewbinding에 activity가 안뜨나?

class StoreAdapter(var storeList : ArrayList<StoreInfo> ) : RecyclerView.Adapter<StoreAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_store_list_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener{
                val curPos: Int = adapterPosition
                val store: StoreInfo = storeList.get(curPos)
            }

        }

    }
    fun datasetChanged(storeList: ArrayList<StoreInfo>){
        this.storeList=storeList
        notifyDataSetChanged()
    }

    // view를 실제 어댑터에 연결
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.storeImage.setImageResource(storeList.get(position).storeImage)
        holder.name.text = storeList.get(position).storeName
        holder.dist.text = storeList.get(position).distance
        //holder.review.text = "리뷰 "
        holder.reviewNum.text = storeList.get(position).reviewTotal
        //holder.product.text = "상품 수"
        holder.productNum.text = storeList.get(position).prodNumTotal
        holder.category.text = storeList.get(position).categories.toString()
        //holder.sale.text = "최대 할인율"
        holder.salePercent.text = storeList.get(position).salePercentMax
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage = itemView.findViewById<ImageView>(R.id.iv_storeImage)
        val name = itemView.findViewById<TextView>(R.id.tv_storeName) // 가게이름
        val dist = itemView.findViewById<TextView>(R.id.tv_distance) // 거리
        //val review = itemView.findViewById<TextView>(R.id.tv_review) // 리뷰
        val reviewNum = itemView.findViewById<TextView>(R.id.tv_review_num) // 리뷰 갯수
        //val product = itemView.findViewById<TextView>(R.id.tv_product) // 상품
        val productNum = itemView.findViewById<TextView>(R.id.tv_product_num) // 상품 갯수
        val category = itemView.findViewById<TextView>(R.id.tv_category) // 카테고리
        //val sale = itemView.findViewById<TextView>(R.id.tv_sale) // 최대 할인률
        val salePercent = itemView.findViewById<TextView>(R.id.tv_sale_percetage) //
    }

}