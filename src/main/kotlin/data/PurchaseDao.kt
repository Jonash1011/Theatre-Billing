package org.example.data

import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

// Purchase record for billing
data class Purchase(
    val id: Int = 0,
    val productId: Int,
    val quantity: Int,
    val paymentMode: String,
    val dateTime: String
)

object PurchaseDao {
    private val conn: Connection = Database.connection
    init {
        // Table is already created in Database.kt
    }
    fun add(purchase: Purchase) {
        val stmt = conn.prepareStatement("INSERT INTO purchase(productId, quantity, paymentMode, dateTime) VALUES (?, ?, ?, ?)")
        stmt.setInt(1, purchase.productId)
        stmt.setInt(2, purchase.quantity)
        stmt.setString(3, purchase.paymentMode)
        stmt.setString(4, purchase.dateTime)
        stmt.executeUpdate()
        stmt.close()
    }
    fun getAll(): List<Purchase> {
        val stmt = conn.prepareStatement("SELECT * FROM purchase")
        val rs = stmt.executeQuery()
        val purchases = mutableListOf<Purchase>()
        while (rs.next()) {
            purchases.add(
                Purchase(
                    id = rs.getInt("id"),
                    productId = rs.getInt("productId"),
                    quantity = rs.getInt("quantity"),
                    paymentMode = rs.getString("paymentMode"),
                    dateTime = rs.getString("dateTime")
                )
            )
        }
        rs.close()
        stmt.close()
        return purchases
    }
}
