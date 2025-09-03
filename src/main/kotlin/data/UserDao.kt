package org.example.data

import java.sql.PreparedStatement
import java.sql.ResultSet

object UserDao {
    
    fun add(user: User): Boolean {
        return try {
            val sql = "INSERT INTO user (username, password, isAdmin) VALUES (?, ?, ?)"
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setString(1, user.username)
            statement.setString(2, user.password)
            statement.setBoolean(3, user.isAdmin)
            val result = statement.executeUpdate() > 0
            statement.close()
            result
        } catch (e: Exception) {
            println("Error adding user: ${e.message}")
            false
        }
    }
    
    fun getByUsername(username: String): User? {
        return try {
            val sql = "SELECT * FROM user WHERE username = ?"
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setString(1, username)
            val resultSet: ResultSet = statement.executeQuery()
            val user = if (resultSet.next()) {
                User(
                    id = resultSet.getInt("id"),
                    username = resultSet.getString("username"),
                    password = resultSet.getString("password"),
                    isAdmin = resultSet.getBoolean("isAdmin")
                )
            } else null
            resultSet.close()
            statement.close()
            user
        } catch (e: Exception) {
            println("Error getting user by username: ${e.message}")
            null
        }
    }
    
    fun getAll(): List<User> {
        return try {
            val sql = "SELECT * FROM user"
            val statement = Database.connection.createStatement()
            val resultSet: ResultSet = statement.executeQuery(sql)
            val users = mutableListOf<User>()
            while (resultSet.next()) {
                users.add(
                    User(
                        id = resultSet.getInt("id"),
                        username = resultSet.getString("username"),
                        password = resultSet.getString("password"),
                        isAdmin = resultSet.getBoolean("isAdmin")
                    )
                )
            }
            resultSet.close()
            statement.close()
            users
        } catch (e: Exception) {
            println("Error getting all users: ${e.message}")
            emptyList()
        }
    }
    
    fun delete(id: Int): Boolean {
        return try {
            val sql = "DELETE FROM user WHERE id = ?"
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setInt(1, id)
            val result = statement.executeUpdate() > 0
            statement.close()
            result
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            false
        }
    }
    
    fun update(user: User): Boolean {
        return try {
            val sql = "UPDATE user SET username = ?, password = ?, isAdmin = ? WHERE id = ?"
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setString(1, user.username)
            statement.setString(2, user.password)
            statement.setBoolean(3, user.isAdmin)
            statement.setInt(4, user.id)
            val result = statement.executeUpdate() > 0
            statement.close()
            result
        } catch (e: Exception) {
            println("Error updating user: ${e.message}")
            false
        }
    }
}
