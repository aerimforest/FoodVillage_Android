package com.example.foodvillage

data class StoreInfoData(
    val productName: String,
    val storeName: String,
    val distance: Int,
    val discountRate: Int,
    val fixedPrice: Int,
    val discountedPrice: Int
)
