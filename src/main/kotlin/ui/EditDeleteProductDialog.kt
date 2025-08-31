package org.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.data.Category
import org.example.data.Product
import org.example.ui.theme.AppTheme

@Composable
fun EditDeleteProductDialog(
    product: Product,
    categories: List<Category>,
    onUpdate: (name: String, price: Double, stock: Int, categoryId: Int) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var selectedCategory by remember { mutableStateOf(product.categoryId) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = AppTheme.surfaceColor,
        shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
        title = { Text("Edit Product", fontWeight = FontWeight.Bold, color = AppTheme.primaryColor) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.width(320.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor
                    )
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor
                    )
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Available") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor
                    )
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategory }?.name ?: "Select Category",
                        onValueChange = {},
                        label = { Text("Category") },
                        readOnly = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = AppTheme.primaryColor,
                            cursorColor = AppTheme.primaryColor
                        ),
                        trailingIcon = {
                            IconButton(onClick = { categoryDropdownExpanded = true }) {
                                Icon(
                                    Icons.Default.ArrowDropDown, 
                                    contentDescription = "Dropdown",
                                    tint = AppTheme.primaryColor
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().clickable { categoryDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.width(300.dp)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                selectedCategory = category.id
                                categoryDropdownExpanded = false
                            }) {
                                Text(category.name)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val priceVal = price.toDoubleOrNull() ?: 0.0
                        val stockVal = stock.toIntOrNull() ?: 0
                        if (name.isNotBlank() && priceVal > 0 && stockVal >= 0 && selectedCategory != 0) {
                            onUpdate(name, priceVal, stockVal, selectedCategory)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.primaryColor,
                        contentColor = AppTheme.textOnPrimary
                    )
                ) { Text("Save") }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.errorColor,
                        contentColor = AppTheme.textOnPrimary
                    )
                ) { Text("Delete") }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppTheme.primaryColor
                )
            ) { Text("Cancel") }
        }
    )
}
