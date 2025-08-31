package org.example.data

import java.sql.Connection
import org.example.data.Database
import org.example.data.Product

class ProductDao(private val conn: Connection = Database.connection) {
	init {
		conn.createStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS product (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price REAL NOT NULL, stock INTEGER NOT NULL, categoryId INTEGER NOT NULL, FOREIGN KEY(categoryId) REFERENCES category(id))"
		)
	}

	fun getAll(): List<Product> {
		val stmt = conn.prepareStatement("SELECT * FROM product")
		val rs = stmt.executeQuery()
		val products = mutableListOf<Product>()
		while (rs.next()) {
			products.add(Product(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getDouble("price"),
				rs.getInt("stock"),
				rs.getInt("categoryId")
			))
		}
		rs.close()
		stmt.close()
		return products
	}

	fun add(product: Product): Boolean {
		return try {
			val stmt = conn.prepareStatement("INSERT INTO product(name, price, stock, categoryId) VALUES(?, ?, ?, ?)")
			stmt.setString(1, product.name)
			stmt.setDouble(2, product.price)
			stmt.setInt(3, product.stock)
			stmt.setInt(4, product.categoryId)
			stmt.executeUpdate()
			stmt.close()
			true
		} catch (e: Exception) {
			false
		}
	}

	fun update(product: Product): Boolean {
		return try {
			val stmt = conn.prepareStatement("UPDATE product SET name = ?, price = ?, stock = ?, categoryId = ? WHERE id = ?")
			stmt.setString(1, product.name)
			stmt.setDouble(2, product.price)
			stmt.setInt(3, product.stock)
			stmt.setInt(4, product.categoryId)
			stmt.setInt(5, product.id)
			stmt.executeUpdate()
			stmt.close()
			true
		} catch (e: Exception) {
			false
		}
	}

	fun delete(id: Int): Boolean {
		return try {
			val stmt = conn.prepareStatement("DELETE FROM product WHERE id = ?")
			stmt.setInt(1, id)
			stmt.executeUpdate()
			stmt.close()
			true
		} catch (e: Exception) {
			false
		}
	}
}
