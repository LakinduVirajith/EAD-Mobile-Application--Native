package com.example.ead_mobile_application__native.model

data class Cart(
    val productId: String,
    val name: String,
    val price: Double,
    val discount: Double,
    val imageResId: Int,
    var quantity: Int
)
