package com.example.foodvillage.schema

data class Product(
    var storeName: String? = null,
    var productName: String? = null,
    var discountRate: Double? = null,
    var fixedPrice: Long? = null,
    var dibPeople: List<String>? = null,
    var categoryNum: Long? = null,
    var imgUrl: String? = null
)