package com.example.foodvillage.schema

data class Product(
    var categoryNum: Long? = null,
    var dibPeople: List<String>? = null,
    var discountRate: Double? = null,
    var fixedPrice: Long? = null,
    var imgUrl: String? = null,
    var productName: String? = null,
    var storeName: String? = null
)