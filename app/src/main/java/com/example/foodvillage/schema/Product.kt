package com.example.foodvillage.schema

data class Product(
    var storeName: String? = null,
    var productName: String? = null,
    var discountRate: Double? = null,
    var fixedPrice: Int? = null,
    var dibPeople: List<String>? = null,
    var categoryNum: Int? = null,
    var imgUrl: String? = null
)