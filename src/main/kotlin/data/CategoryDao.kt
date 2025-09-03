package org.example.data

import java.sql.Connection

class CategoryDao(private val conn: Connection = Database.connection) {
    companion object {
        private val instance by lazy { CategoryDao() }

        fun getById(id: Int): Category? {
            return instance.getByIdInternal(id)
        }
    }

    private fun getByIdInternal(id: Int): Category? {
        val stmt = conn.prepareStatement("SELECT * FROM category WHERE id = ?")
        stmt.setInt(1, id)
        val rs = stmt.executeQuery()
        val category = if (rs.next()) {
            Category(rs.getInt("id"), rs.getString("name"))
        } else null
        rs.close()
        stmt.close()
        return category
    }

    fun getAll(): List<Category> {
        val stmt = conn.prepareStatement("SELECT * FROM category")
        val rs = stmt.executeQuery()
        val categories = mutableListOf<Category>()
        while (rs.next()) {
            categories.add(Category(rs.getInt("id"), rs.getString("name")))
        }
        rs.close()
        stmt.close()
        return categories
    }

    fun add(name: String): Boolean {
        return try {
            val stmt = conn.prepareStatement("INSERT INTO category(name) VALUES(?)")
            stmt.setString(1, name)
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun update(id: Int, name: String): Boolean {
        return try {
            val stmt = conn.prepareStatement("UPDATE category SET name = ? WHERE id = ?")
            stmt.setString(1, name)
            stmt.setInt(2, id)
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun delete(id: Int): Boolean {
        return try {
            val stmt = conn.prepareStatement("DELETE FROM category WHERE id = ?")
            stmt.setInt(1, id)
            stmt.executeUpdate()
            stmt.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
