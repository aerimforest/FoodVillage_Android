package com.example.foodvillage.marketmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R

class MarketProductAdapter(val productList: ArrayList<MarketProductInfo>) : RecyclerView.Adapter<MarketProductAdapter.CustomViewHolderProduct>(){

    class CustomViewHolderProduct(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productImage = itemView.findViewById<ImageView>(R.id.iv_dialog_fmi_market_product_image)
        val productName = itemView.findViewById<TextView>(R.id.tv_dialog_fmi_market_product_name)
        val salePercentage = itemView.findViewById<TextView>(R.id.tv_sale_percetage)
        val originalPrice = itemView.findViewById<TextView>(R.id.tv_dialog_fmi_market_product_originalprice)
        val salePrice = itemView.findViewById<TextView>(R.id.tv_dialog_fmi_market_product_saleprice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderProduct {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_fmi_market_product, parent,false)
        return CustomViewHolderProduct(view).apply {
            itemView.setOnClickListener {
                val curPos: Int = adapterPosition
                val product: MarketProductInfo = productList.get(curPos)

            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolderProduct, position: Int) {
        holder.productImage.setImageResource(productList.get(position).productImage)
        holder.productName.text = productList.get(position).productName
        holder.salePercentage.text = productList.get(position).salePercentage.toString()
        holder.originalPrice.text = productList.get(position).originalPrice.toString()
        holder.salePrice.text = productList.get(position).salePrice.toString()
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}