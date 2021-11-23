package com.example.foodvillage.schema

data class Store(
<<<<<<< HEAD
        var storeName: String? = null,
        var currentLatitude: Double? = null,
        var currentLongitude: Double? = null,
        var address: String?=null,
        var categoryNames: List<String>? = null,
        var distance: HashMap<String, Double>?=null
=======
    var storeName: String? = null,
    var currentLatitude: Double? = null,
    var currentLongitude: Double? = null,
    var address: String? = null,
    var categoryNames: List<String>? = null,
    var distance: HashMap<String, Double>? = null,
    var storeImg: String? = null,
    var grade: Double? = null
>>>>>>> 39196742d6e4014e7190d7d19bc95737426ad455
)
