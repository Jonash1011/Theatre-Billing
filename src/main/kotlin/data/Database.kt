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
                "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, isAdmin BOOLEAN NOT NULL DEFAULT 0)"
            )
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS category (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)"
            )
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS product (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price REAL NOT NULL, stock INTEGER NOT NULL, categoryId INTEGER NOT NULL, FOREIGN KEY(categoryId) REFERENCES category(id))"
            )
            // Create purchase table with all required columns
            conn.createStatement().executeUpdate("""
                CREATE TABLE IF NOT EXISTS purchase (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    productId INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    paymentMode TEXT NOT NULL,
                    dateTime TEXT NOT NULL,
                    FOREIGN KEY(productId) REFERENCES product(id)
                )
            """)
            
            // Create default admin user if no users exist
            try {
                val userCheck = conn.createStatement().executeQuery("SELECT COUNT(*) as count FROM user")
                val userCount = if (userCheck.next()) userCheck.getInt("count") else 0
                userCheck.close()
                
                if (userCount == 0) {
                    conn.createStatement().executeUpdate(
                        "INSERT INTO user (username, password, isAdmin) VALUES ('admin', 'password123', 1)"
                    )
                    println("Default admin user created")
                }
            } catch (e: Exception) {
                println("Error creating default admin user: ${e.message}")
            }
        } catch (e: Exception) {
            println("Table creation error: ${e.message}")
            // Do not throw, allow app to open
        }
    }
}
