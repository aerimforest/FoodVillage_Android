package com.example.foodvillage.schema

data class Store(
        var storeName: String? = null,
        var currentLatitude: Double? = null,
        var currentLongitude: Double? = null,
        var address: String?=null
)