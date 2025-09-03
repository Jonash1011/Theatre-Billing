package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.example.data.User
import org.example.data.UserDao
import org.example.ui.theme.AppTheme

@Composable
fun UserListDialog(
    onDismiss: () -> Unit,
    onEditUser: (User) -> Unit
) {
    var users by remember { mutableStateOf(UserDao.getAll()) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .padding(16.dp),
            elevation = AppTheme.cardElevation.dp,
            backgroundColor = AppTheme.surfaceColor,
            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Text("←", fontSize = 20.sp, color = AppTheme.primaryColor, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "Existing User Details",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.primaryColor
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 20.sp, color = AppTheme.textSecondary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (users.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No users found",
                            fontSize = 16.sp,
                            color = AppTheme.textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { user ->
                            UserItem(
                                user = user,
                                isSelected = selectedUser?.id == user.id,
                                onClick = { selectedUser = user },
                                onEdit = { onEditUser(user) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            users = UserDao.getAll() // Refresh the list
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.accentColor,
                            contentColor = AppTheme.textOnPrimary
                        ),
                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                    ) {
                        Text("Refresh")
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = if (isSelected) 8.dp else 2.dp,
        backgroundColor = if (isSelected) AppTheme.primaryLightColor else AppTheme.cardColor,
        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        user.username,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (user.isAdmin) {
                        Card(
                            backgroundColor = AppTheme.accentColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.textOnPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Card(
                            backgroundColor = AppTheme.primaryColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "USER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.textOnPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "ID: ${user.id}",
                    fontSize = 14.sp,
                    color = AppTheme.textSecondary
                )
            }
            
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.primaryColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) {
                Text("Edit", fontSize = 12.sp)
            }
        }
    }
}
