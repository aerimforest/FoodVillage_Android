package com.example.foodvillage.schema

import android.net.Uri

// 가게 이름 : Review data
data class Review(
    var userId: String? = null,
    var userName: String? = null,
    var reportDate: String? = null,
    var reviewTitle: String? = null,
    var reviewContent: String? = null,
    var reviewImg: Uri? = null
)