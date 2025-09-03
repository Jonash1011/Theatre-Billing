package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import org.example.ui.theme.AppTheme

@Composable
fun ManageUserDialog(
    onDismiss: () -> Unit,
    onCreateNewUser: () -> Unit,
    onExistingUserDetails: () -> Unit
) {
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
                        "Manage Users",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.primaryColor
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Choose an option to manage users:",
                    fontSize = 16.sp,
                    color = AppTheme.textSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Create New User Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            onCreateNewUser()
                            onDismiss()
                        },
                    elevation = 4.dp,
                    backgroundColor = AppTheme.primaryLightColor,
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    AppTheme.primaryColor,
                                    RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "+",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.textOnPrimary
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                "Create New User",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.textPrimary
                            )
                            Text(
                                "Add a new user to the system",
                                fontSize = 14.sp,
                                color = AppTheme.textSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            "→",
                            fontSize = 20.sp,
                            color = AppTheme.primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Existing User Details Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            onExistingUserDetails()
                            onDismiss()
                        },
                    elevation = 4.dp,
                    backgroundColor = AppTheme.primaryLightColor,
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    AppTheme.accentColor,
                                    RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "👥",
                                fontSize = 20.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                "Existing User Details",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.textPrimary
                            )
                            Text(
                                "View, edit, and manage existing users",
                                fontSize = 14.sp,
                                color = AppTheme.textSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            "→",
                            fontSize = 20.sp,
                            color = AppTheme.primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppTheme.primaryColor
                    ),
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                ) {
                    Text("← Back to Dashboard")
                }
            }
        }
    }
}
