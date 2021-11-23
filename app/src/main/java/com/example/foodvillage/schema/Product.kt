package com.example.foodvillage.schema

import android.net.Uri

data class Product(
    var storeName: String? = null,
    var productName: String? = null,
    var discountRate: Double? = null,
    var fixedPrice: Int? = null,
    var productImg: Uri? = null,
    var categoryNum: Int? = null,
    var imgUrl: String? = null
)