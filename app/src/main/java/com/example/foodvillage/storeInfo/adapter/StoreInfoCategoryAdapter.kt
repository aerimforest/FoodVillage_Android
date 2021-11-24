package com.example.foodvillage.storeInfo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R
import kotlinx.android.synthetic.main.item_store_info_category.view.*

class StoreInfoCategoryAdapter(
    private val context: Context,
    private val categoryList: ArrayList<StoreCategory>
) :
    RecyclerView.Adapter<StoreInfoCategoryAdapter.Holder>() {

    private var selectedCategory = 0

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val categoryName = itemView?.findViewById<TextView>(R.id.tv_category_item)

        fun bind(list: StoreCategory) {
            categoryName?.text = list.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_store_info_category, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(categoryList[position])
        holder.itemView.setOnClickListener {
            selectedCategory = position
            notifyDataSetChanged()
        }

        if (selectedCategory == position) {
            holder.itemView.tv_category_item.setBackgroundResource(R.drawable.background_btn_selected_green)
            holder.itemView.tv_category_item.setTextColor(Color.WHITE)
        } else {
            holder.itemView.tv_category_item.setBackgroundResource(R.drawable.background_category_non_selected)
            holder.itemView.tv_category_item.setTextColor(Color.parseColor("#999999"))
        }
    }
}