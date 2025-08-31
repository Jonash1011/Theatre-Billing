package org.example.data

import java.sql.Connection
import java.sql.DriverManager
import java.io.File

object Database {
    private val dbPath = System.getProperty("user.home") + File.separator + "theatre_canteen.db"
    private val DB_URL = "jdbc:sqlite:$dbPath"
    
    val connection: Connection by lazy {
        try {
            // Explicitly load the SQLite driver
            Class.forName("org.sqlite.JDBC")
            val conn = DriverManager.getConnection(DB_URL)
            initializeTables(conn)
            conn
        } catch (e: Exception) {
            println("Database connection error: ${e.message}")
            throw e
        }
    }
    
    private fun initializeTables(conn: Connection) {
        try {
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS category (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)"
            )
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS product (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price REAL NOT NULL, stock INTEGER NOT NULL, categoryId INTEGER NOT NULL, FOREIGN KEY(categoryId) REFERENCES category(id))"
            )
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS purchase (id INTEGER PRIMARY KEY AUTOINCREMENT, productId INTEGER, quantity INTEGER, price REAL, paymentMode TEXT, dateTime TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(productId) REFERENCES product(id))"
            )
            // Add missing columns to purchase table if they don't exist
            try { conn.createStatement().executeUpdate("ALTER TABLE purchase ADD COLUMN paymentMode TEXT") } catch (e: Exception) { println("paymentMode column: ${e.message}") }
            try { conn.createStatement().executeUpdate("ALTER TABLE purchase ADD COLUMN dateTime TEXT") } catch (e: Exception) { println("dateTime column: ${e.message}") }
            try { conn.createStatement().executeUpdate("ALTER TABLE purchase ADD COLUMN price REAL") } catch (e: Exception) { println("price column: ${e.message}") }
            try { conn.createStatement().executeUpdate("ALTER TABLE purchase ADD COLUMN timestamp DATETIME DEFAULT CURRENT_TIMESTAMP") } catch (e: Exception) { println("timestamp column: ${e.message}") }
        } catch (e: Exception) {
            println("Table creation error: ${e.message}")
            // Do not throw, allow app to open
        }
    }
}
