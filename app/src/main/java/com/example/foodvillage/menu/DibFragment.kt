package com.example.foodvillage.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvillage.ItemDecoration
import com.example.foodvillage.R
import com.example.foodvillage.databinding.FragmentDibBinding
import com.example.foodvillage.schema.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_dib.*
import kotlinx.android.synthetic.main.item_dib_product_list.view.imv_product
import kotlinx.android.synthetic.main.item_dib_product_list.view.tv_discount_rate
import kotlinx.android.synthetic.main.item_dib_product_list.view.tv_discounted_price
import kotlinx.android.synthetic.main.item_dib_product_list.view.tv_distance
import kotlinx.android.synthetic.main.item_dib_product_list.view.tv_fixed_price
import kotlinx.android.synthetic.main.item_dib_product_list.view.tv_product_name
import kotlinx.android.synthetic.main.item_dib_product_list.view.tv_store_name

class DibFragment : Fragment() {

    private var _binding: FragmentDibBinding? = null
    private val binding get() = _binding!!
    private var dibProductList = arrayListOf<Product>()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        _binding = FragmentDibBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(context, 2)
        rv_dib_list.adapter = DibProductAdapter()
        rv_dib_list.layoutManager = gridLayoutManager
        rv_dib_list.addItemDecoration(ItemDecoration(2, 50, true))
    }

    inner class DibProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        init {
            databaseReference = firebaseDatabase.getReference("products")
            databaseReference.orderByChild("discountRate").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    // ArrayList 비워줌
                    dibProductList.clear()

                    for (postSnapshot in dataSnapshot.children) {
                        val item = postSnapshot.getValue(Product::class.java)
                        var uidList = postSnapshot.child("dibPeople").value.toString()
                        uidList = uidList.substring(1, uidList.length - 1)
                        val arr = uidList.split(", ")

                        for (i in arr) {
                            if (i == auth.uid) {
                                if (item != null) {
                                    dibProductList.add(0, item)
                                }
                            }
                        }
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dib_product_list, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = (holder as ViewHolder).itemView

            viewHolder.tv_product_name.text = dibProductList[position].productName
            viewHolder.tv_store_name.text = dibProductList[position].storeName
            viewHolder.tv_discount_rate.text =
                (dibProductList[position].discountRate?.times(100))?.toInt()
                    .toString()
            viewHolder.tv_fixed_price.text = dibProductList[position].fixedPrice.toString()
            viewHolder.tv_discounted_price.text = (dibProductList[position].fixedPrice?.times(
                dibProductList[position].discountRate!!
            ))?.toInt().toString()

            val databaseDistanceReference: DatabaseReference =
                firebaseDatabase.getReference("stores/${dibProductList[position].storeName}/distance/${auth.uid}")

            databaseDistanceReference.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    viewHolder.tv_distance.text = dataSnapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            // drawable 파일에서 이미지 검색 후 적용
            val id = context!!.resources.getIdentifier(
                dibProductList[position].imgUrl.toString(),
                "drawable",
                context!!.packageName
            )
            viewHolder.imv_product.setImageResource(id)

            // Todo: recyclerview item click listener
        }

        override fun getItemCount(): Int {
            return dibProductList.size
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}