package com.example.foodvillage.storeList

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R
import com.example.foodvillage.schema.Store
import com.example.foodvillage.schema.StoreCategory
import com.example.foodvillage.storeInfo.ui.StoreInfoActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_category.view.*
import kotlin.properties.Delegates

class StoreAdapter(var storeList: ArrayList<StoreInfo>, private val mContext: Context) :
    RecyclerView.Adapter<StoreAdapter.CustomViewHolder>() {
    internal var collection: List<Store> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var viewHolderList = arrayListOf<View>()
    var uid = FirebaseAuth.getInstance().uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_store_list_item, parent, false)
        viewHolderList.add(view)

        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.name.text = storeList[position].storeName
        holder.dist.text = storeList[position].distance
        holder.reviewNum.text = storeList[position].reviewTotal
        holder.productNum.text = storeList[position].prodNumTotal
        holder.salePercent.text = storeList[position].salePercentMax

        val recyclerRecyclerAdapter = CategoryListAdapter()
        val recyclerviewRecyclerviewItemList =
            arrayListOf<StoreCategory>()

        for (i in storeList[position].categories!!) {
            val recyclerviewRecyclerViewItem = StoreCategory(i)
            recyclerviewRecyclerviewItemList.add(recyclerviewRecyclerViewItem)
        }

        recyclerRecyclerAdapter.collection = recyclerviewRecyclerviewItemList
        holder.category.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true)
        holder.category.scrollToPosition(recyclerRecyclerAdapter.itemCount - 1)
        holder.category.adapter = recyclerRecyclerAdapter

        // drawable 파일에서 이미지 검색 후 적용
        val id = mContext.resources.getIdentifier(
            storeList[position].storeImg.toString(),
            "drawable",
            mContext.packageName
        )

        holder.storeImage.setImageResource(id)

//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, StoreInfoActivity::class.java)
            intent.putExtra("storeName", storeList[position].storeName)
            mContext.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))

        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener

    override fun getItemCount(): Int {
        return storeList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage: ImageView = itemView.findViewById(R.id.iv_storeImage)
        val name: TextView = itemView.findViewById(R.id.tv_storeName) // 가게이름
        val dist: TextView = itemView.findViewById(R.id.tv_distance) // 거리
        val reviewNum: TextView = itemView.findViewById(R.id.tv_review_num) // 리뷰 갯수
        val productNum: TextView = itemView.findViewById(R.id.tv_product_num) // 상품 갯수
        val salePercent: TextView = itemView.findViewById(R.id.tv_sale_percetage)
        val category: RecyclerView = itemView.findViewById(R.id.rv_store_list_item_category)
    }

    fun datasetChanged(storeList: ArrayList<StoreInfo>) {
        this.storeList = storeList
        notifyDataSetChanged()
    }
}

class CategoryListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var collection: List<StoreCategory> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as ViewHolder).itemView
        viewHolder.tv_category_item.text = collection[position].categoryName
    }

    override fun getItemCount(): Int {
        return collection.size
    }
}