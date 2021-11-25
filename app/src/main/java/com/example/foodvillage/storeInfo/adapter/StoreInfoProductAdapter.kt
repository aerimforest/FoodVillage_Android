package com.example.foodvillage.storeInfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R
import com.example.foodvillage.schema.Product

class StoreInfoProductAdapter(
    private val context: Context,
    private val productList: ArrayList<Product>
) :
    RecyclerView.Adapter<StoreInfoProductAdapter.Holder>() {

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val productImg = itemView?.findViewById<ImageView>(R.id.imv_store_info_product)
        private val productName = itemView?.findViewById<TextView>(R.id.tv_store_info_product_name)
        private val fixedPrice = itemView?.findViewById<TextView>(R.id.tv_store_info_product_fixed)
        private val discountRate =
            itemView?.findViewById<TextView>(R.id.tv_store_info_discount_rate)
        private val discountedPrice =
            itemView?.findViewById<TextView>(R.id.tv_store_info_product_discounted)

        fun bind(list: Product) {
            // 이미지
            val id = context.resources.getIdentifier(
                list.imgUrl.toString(),
                "drawable",
                context.packageName
            )
            productImg?.setImageResource(id)

            productName?.text = list.productName
            fixedPrice?.text = list.fixedPrice.toString()
            discountRate?.text = list.discountRate?.times(100)?.toInt().toString()
            discountedPrice?.text =
                (list.fixedPrice?.times(list.discountRate!!))?.toInt().toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_store_info_product, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(productList[position])
    }
}