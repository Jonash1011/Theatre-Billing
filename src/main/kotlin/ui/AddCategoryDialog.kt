package org.example.ui

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
import org.example.ui.theme.AppTheme

@Composable
fun AddCategoryDialog(initialName: String, onAdd: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = AppTheme.surfaceColor,
        shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
        properties = DialogProperties(dismissOnClickOutside = true),
        title = { 
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (initialName.isEmpty()) "Add Category" else "Edit Category",
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.primaryColor,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.width(300.dp).padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor,
                        focusedLabelColor = AppTheme.primaryColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onAdd(name) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.primaryColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
                enabled = name.isNotBlank(),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) { 
                Text(
                    "Save", 
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) 
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppTheme.primaryColor
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) { 
                Text(
                    "Cancel",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) 
            }
        }
    )
}
