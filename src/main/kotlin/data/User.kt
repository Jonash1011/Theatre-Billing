package org.example.data

data class User(
    val id: Int = 0,
    val username: String,
    val password: String,
    val isAdmin: Boolean = false
)
