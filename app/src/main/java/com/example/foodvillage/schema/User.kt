package com.example.foodvillage.schema

data class Users(
    var id: String? = null,
    var name: String? = null,
    var currentLatitude: Double? = null,
    var currentLongitude: Double? = null,
    var address: String? = null,
    var score: Int? = null
)