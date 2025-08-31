package org.example.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import org.example.data.Category
import org.example.data.CategoryDao
import org.example.ui.theme.AppTheme

@Composable
fun CategoryDialog(onDismiss: () -> Unit) {
    var categories by remember { mutableStateOf(emptyList<Category>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    val dao = remember { CategoryDao() }

    LaunchedEffect(Unit) {
        categories = dao.getAll()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = AppTheme.surfaceColor,
        shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
        properties = DialogProperties(dismissOnClickOutside = true),
        title = {
            // Empty title to allow for custom positioning
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Categories",
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.primaryColor,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 20.dp)
                )
                
                // Category list
                if (categories.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Text(
                            "No categories found. Add one below.",
                            color = AppTheme.textSecondary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { cat ->
                            Card(
                                elevation = 4.dp,
                                backgroundColor = AppTheme.surfaceColor,
                                shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = AppTheme.primaryLightColor.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                    )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 16.dp)
                                ) {
                                    Text(
                                        cat.name,
                                        modifier = Modifier.weight(1f),
                                        color = AppTheme.textPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = { showAddDialog = true; newCategoryName = cat.name },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = AppTheme.primaryColor
                                            ),
                                            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                        ) { 
                                            Text("Edit") 
                                        }
                                        Button(
                                            onClick = {
                                                dao.delete(cat.id)
                                                categories = dao.getAll()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = AppTheme.errorColor,
                                                contentColor = AppTheme.textOnPrimary
                                            ),
                                            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                        ) { 
                                            Text("Delete") 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Divider and Add Button
                Divider(
                    color = AppTheme.primaryLightColor.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(
                    onClick = { showAddDialog = true; newCategoryName = "" },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.primaryColor,
                        contentColor = AppTheme.textOnPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        "Add Category", 
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Box(modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.primaryColor,
                        contentColor = AppTheme.textOnPrimary
                    ),
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        "Close",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    )

    if (showAddDialog) {
        AddCategoryDialog(
            initialName = newCategoryName,
            onAdd = { name ->
                if (newCategoryName.isEmpty()) {
                    dao.add(name)
                } else {
                    val cat = categories.find { it.name == newCategoryName }
                    if (cat != null) dao.update(cat.id, name)
                }
                categories = dao.getAll()
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}
