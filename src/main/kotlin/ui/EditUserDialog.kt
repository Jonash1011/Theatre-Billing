package org.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.example.data.User
import org.example.data.UserDao
import org.example.ui.theme.AppTheme

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onUserUpdated: () -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(user.isAdmin) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = AppTheme.cardElevation.dp,
            backgroundColor = AppTheme.surfaceColor,
            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Text("←", fontSize = 20.sp, color = AppTheme.primaryColor, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Edit User Details",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.primaryColor
                    )
                }
                
                // User ID (read-only)
                OutlinedTextField(
                    value = "ID: ${user.id}",
                    onValueChange = { },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = AppTheme.textSecondary,
                        disabledBorderColor = AppTheme.textSecondary.copy(alpha = 0.5f)
                    )
                )
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { 
                        username = it
                        error = ""
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor
                    )
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        error = ""
                    },
                    label = { Text("New Password (leave empty to keep current)") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = AppTheme.primaryColor
                            )
                        }
                    }
                )
                
                if (password.isNotEmpty()) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            error = ""
                        },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = AppTheme.primaryColor,
                            cursorColor = AppTheme.primaryColor
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = AppTheme.primaryColor
                                )
                            }
                        }
                    )
                }
                
                // Admin role toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAdmin,
                        onCheckedChange = { isAdmin = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AppTheme.primaryColor
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Admin User",
                        fontSize = 16.sp,
                        color = AppTheme.textPrimary
                    )
                }
                
                if (error.isNotEmpty()) {
                    Text(
                        error, 
                        color = AppTheme.errorColor,
                        fontSize = 14.sp
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppTheme.primaryColor
                        ),
                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                    ) {
                        Text("← Back")
                    }
                    
                    Button(
                        onClick = {
                            if (username.isBlank()) {
                                error = "Username cannot be empty"
                                return@Button
                            }
                            
                            if (password.isNotEmpty()) {
                                if (password != confirmPassword) {
                                    error = "Passwords do not match"
                                    return@Button
                                }
                                if (password.length < 6) {
                                    error = "Password must be at least 6 characters"
                                    return@Button
                                }
                            }
                            
                            isLoading = true
                            
                            // Check if username already exists (excluding current user)
                            val existingUser = UserDao.getByUsername(username)
                            if (existingUser != null && existingUser.id != user.id) {
                                error = "Username already exists"
                                isLoading = false
                                return@Button
                            }
                            
                            // Update user
                            val updatedUser = user.copy(
                                username = username,
                                password = if (password.isNotEmpty()) password else user.password,
                                isAdmin = isAdmin
                            )
                            
                            val success = UserDao.update(updatedUser)
                            if (success) {
                                onUserUpdated()
                                onDismiss()
                            } else {
                                error = "Failed to update user"
                                isLoading = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.primaryColor,
                            contentColor = AppTheme.textOnPrimary
                        ),
                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AppTheme.textOnPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Update User", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Button(
                        onClick = {
                            val success = UserDao.delete(user.id)
                            if (success) {
                                onUserUpdated()
                                onDismiss()
                            } else {
                                error = "Failed to delete user"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.errorColor,
                            contentColor = AppTheme.textOnPrimary
                        ),
                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
                        enabled = !isLoading
                    ) {
                        Text("Delete User", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
