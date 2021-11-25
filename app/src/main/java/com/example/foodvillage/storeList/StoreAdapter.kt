package com.example.foodvillage.storeList

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.R
import com.example.foodvillage.storeInfo.ui.StoreInfoActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_today_sale.view.*

class StoreAdapter(var storeList: ArrayList<StoreInfo>, private val mContext:Context) :
    RecyclerView.Adapter<StoreAdapter.CustomViewHolder>() {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private var databaseStoreReference: DatabaseReference = firebaseDatabase.reference

    var uid = FirebaseAuth.getInstance().uid
    var DbRefUser = firebaseDatabase.getReference("users/" + uid)


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
        //holder.storeImage.setImageResource(storeList[position].storeImage)
        holder.name.text = storeList[position].storeName
        holder.dist.text = storeList[position].distance
        //holder.review.text = "리뷰 "
        holder.reviewNum.text = storeList[position].reviewTotal
        //holder.product.text = "상품 수"
        holder.productNum.text = storeList[position].prodNumTotal
        holder.category.text = storeList[position].categories.toString()
        //holder.sale.text = "최대 할인율"
        holder.salePercent.text = storeList[position].salePercentMax


        Log.d("리소스", storeList[position].storeImg.toString())
        // drawable 파일에서 이미지 검색 후 적용
        val id = mContext!!.resources.getIdentifier(
            storeList[position].storeImg.toString(),
            "drawable",
            mContext!!.packageName
        )
        holder.storeImage.setImageResource(id)
//
//        val auth: FirebaseAuth = FirebaseAuth.getInstance()
//        val databaseDistanceReference: DatabaseReference =
//            firebaseDatabase.getReference("stores/${storeList[position].storeName}/distance/${auth.uid}")

//        databaseDistanceReference.addValueEventListener(object :
//            ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                holder.tv_distance.text = dataSnapshot.value.toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })

//        holder.tv_fixed_price.apply {
//            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//        }
//        holder.tv_fixed_price_won.apply {
//            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//        }



        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, StoreInfoActivity::class.java)
            intent.putExtra("storeName", storeList[position].storeName)
            mContext?.startActivity(intent)
        })
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