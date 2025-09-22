package org.example.data

import java.sql.PreparedStatement
import java.sql.ResultSet

object UserDao {
    
    fun add(user: User): Boolean {
        println("UserDao.add - Adding user: ${user.username}, isAdmin: ${user.isAdmin}")
        return try {
            val sql = "INSERT INTO user (username, password, isAdmin) VALUES (?, ?, ?)"
            println("UserDao.add - Preparing SQL statement for user insertion")
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setString(1, user.username)
            statement.setString(2, user.password)
            statement.setBoolean(3, user.isAdmin)
            
            println("UserDao.add - Executing user insertion")
            val result = statement.executeUpdate() > 0
            statement.close()
            
            if (result) {
                println("UserDao.add - User '${user.username}' added successfully")
                println("UserDao.add - User action: USER_CREATED, Username: ${user.username}, Admin: ${user.isAdmin}")
            } else {
                println("UserDao.add - Failed to add user '${user.username}'")
            }
            
            println("UserDao.add - Returning result: $result")
            result
        } catch (e: Exception) {
            println("UserDao.add - Error adding user '${user.username}': ${e.message}")
            println("UserDao.add - Returning false due to exception")
            false
        }
    }
    
    fun getByUsername(username: String): User? {
        println("UserDao.getByUsername called with username: $username")
        return try {
            val sql = "SELECT * FROM user WHERE username = ?"
            println("UserDao: Preparing SQL statement to get user by username")
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setString(1, username)
            
            println("UserDao: Executing query to find user: $username")
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
            
            if (user != null) {
                println("UserDao: User '$username' found successfully")
            } else {
                println("UserDao: User '$username' not found")
            }
            
            println("UserDao.getByUsername finished: ${if (user != null) "User found" else "User not found"}")
            user
        } catch (e: Exception) {
            println("UserDao: Error getting user by username '$username': ${e.message}")
            println("UserDao: Exception occurred while getting user '$username': ${e.message}")
            println("UserDao.getByUsername finished: null due to exception")
            null
        }
    }
    
    fun getAll(): List<User> {
        println("UserDao.getAll called")
        return try {
            val sql = "SELECT * FROM user"
            println("UserDao: Executing query to get all users")
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
            
            println("UserDao: Retrieved ${users.size} users successfully")
            println("UserDao.getAll finished: ${users.size} users")
            users
        } catch (e: Exception) {
            println("UserDao: Error getting all users: ${e.message}")
            println("UserDao: Exception occurred while getting all users: ${e.message}")
            println("UserDao.getAll finished: empty list due to exception")
            emptyList()
        }
    }
    
    fun delete(id: Int): Boolean {
        println("UserDao.delete called with id: $id")
        return try {
            val sql = "DELETE FROM user WHERE id = ?"
            println("UserDao: Preparing SQL statement to delete user with ID: $id")
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setInt(1, id)
            
            println("UserDao: Executing user deletion")
            val result = statement.executeUpdate() > 0
            statement.close()
            
            if (result) {
                println("UserDao: User with ID $id deleted successfully")
                println("UserDao: USER_DELETED - User ID: $id")
            } else {
                println("UserDao: Failed to delete user with ID $id (user may not exist)")
            }
            
            println("UserDao.delete finished: $result")
            result
        } catch (e: Exception) {
            println("UserDao: Error deleting user with ID $id: ${e.message}")
            println("UserDao: Exception occurred while deleting user ID $id: ${e.message}")
            println("UserDao.delete finished: false")
            false
        }
    }
    
    fun update(user: User): Boolean {
        println("UserDao.update called with id: ${user.id}, username: ${user.username}, isAdmin: ${user.isAdmin}")
        return try {
            val sql = "UPDATE user SET username = ?, password = ?, isAdmin = ? WHERE id = ?"
            println("UserDao: Preparing SQL statement to update user with ID: ${user.id}")
            val statement: PreparedStatement = Database.connection.prepareStatement(sql)
            statement.setString(1, user.username)
            statement.setString(2, user.password)
            statement.setBoolean(3, user.isAdmin)
            statement.setInt(4, user.id)
            
            println("UserDao: Executing user update")
            val result = statement.executeUpdate() > 0
            statement.close()
            
            if (result) {
                println("UserDao: User '${user.username}' (ID: ${user.id}) updated successfully")
                println("UserDao: USER_UPDATED - Username: ${user.username}, ID: ${user.id}, Admin: ${user.isAdmin}")
            } else {
                println("UserDao: Failed to update user with ID ${user.id} (user may not exist)")
            }
            
            println("UserDao.update finished: $result")
            result
        } catch (e: Exception) {
            println("UserDao: Error updating user '${user.username}' (ID: ${user.id}): ${e.message}")
            println("UserDao: Exception occurred while updating user ID ${user.id}: ${e.message}")
            println("UserDao.update finished: false")
            false
        }
    }
}
