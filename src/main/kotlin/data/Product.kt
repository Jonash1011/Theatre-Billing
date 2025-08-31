package org.example.data

data class Product(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val stock: Int,
    val categoryId: Int
)
