package org.example.ui

// ...existing code...
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import org.example.ui.CategoryDialog
import org.example.ui.AddProductDialog
import org.example.ui.CreateUserDialog
import org.example.ui.UserListDialog
import org.example.ui.EditUserDialog
import org.example.ui.ManageUserDialog
import org.example.data.ProductDao
import org.example.data.Product
import org.example.data.CategoryDao
import org.example.data.Category
import org.example.data.User
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.ui.theme.AppTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun ProductListScreen(onLogout: () -> Unit) {
    val productDao = remember { ProductDao() }
    val categoryDao = remember { CategoryDao() }
    var products by remember { mutableStateOf(productDao.getAll()) }
    var categories by remember { mutableStateOf(categoryDao.getAll()) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var editProduct by remember { mutableStateOf<Product?>(null) }
    var showStatisticsDialog by remember { mutableStateOf(false) }
    var showManageUserDialog by remember { mutableStateOf(false) }
    var showCreateUserDialog by remember { mutableStateOf(false) }
    var showUserListDialog by remember { mutableStateOf(false) }
    var editUser by remember { mutableStateOf<User?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.backgroundColor)
    ) {
        // Top bar with Manage User, Statistic, and Logout buttons
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { showManageUserDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.primaryColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) {
                Text("Manage User")
            }
            Button(
                onClick = { showStatisticsDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.accentColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) {
                Text("Statistic")
            }
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppTheme.primaryColor
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) {
                Text("Logout")
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Canteen Products", 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Bold,
                color = AppTheme.primaryColor
            )
            Spacer(Modifier.height(16.dp))
            // Sort products alphabetically and group by category
            val sortedAndGroupedProducts = products
                .sortedBy { it.name }
                .groupBy { product -> 
                    categories.find { it.id == product.categoryId }?.name ?: ""
                }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sortedAndGroupedProducts.forEach { (category, productsInCategory) ->
                    // Category header spanning all columns
                    item(span = { GridItemSpan(4) }) {
                        Column {
                            if (category.isNotEmpty()) {
                                Text(
                                    category,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.primaryColor,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                                Divider(
                                    color = AppTheme.primaryLightColor,
                                    thickness = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    
                    // Products in the category
                    items(productsInCategory) { product ->
                        ProductTile(
                            product = product,
                            categoryName = category,
                            onClick = { editProduct = product }
                        )
                    }
                    
                    // Add spacing after each category
                    item(span = { GridItemSpan(4) }) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { showCategoryDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.primaryColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) {
                Text("Categories")
            }
            Button(
                onClick = { showAddProductDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.accentColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) {
                Text("Add Product")
            }
        }

        if (showCategoryDialog) {
            CategoryDialog(onDismiss = { 
                showCategoryDialog = false
                // Refresh categories when dialog is closed
                categories = categoryDao.getAll()
            })
        }
        if (showAddProductDialog) {
            AddProductDialog(
                categories = categories,
                onAdd = { name, price, stock, categoryId ->
                    productDao.add(Product(name = name, price = price, stock = stock, categoryId = categoryId))
                    products = productDao.getAll()
                    showAddProductDialog = false
                },
                onDismiss = { showAddProductDialog = false }
            )
        }
        if (editProduct != null) {
            EditDeleteProductDialog(
                product = editProduct!!,
                categories = categories,
                onUpdate = { name, price, stock, categoryId ->
                    productDao.update(Product(id = editProduct!!.id, name = name, price = price, stock = stock, categoryId = categoryId))
                    products = productDao.getAll()
                    editProduct = null
                },
                onDelete = {
                    productDao.delete(editProduct!!.id)
                    products = productDao.getAll()
                    editProduct = null
                },
                onDismiss = { editProduct = null }
            )
        }
        if (showStatisticsDialog) {
            StatisticDialog(
                categories = categories,
                onDismiss = { showStatisticsDialog = false }
            )
        }
        if (showManageUserDialog) {
            ManageUserDialog(
                onDismiss = { showManageUserDialog = false },
                onCreateNewUser = { showCreateUserDialog = true },
                onExistingUserDetails = { showUserListDialog = true }
            )
        }
        if (showCreateUserDialog) {
            CreateUserDialog(
                onDismiss = { 
                    showCreateUserDialog = false
                    showManageUserDialog = true // Go back to ManageUserDialog
                },
                onUserCreated = { 
                    showCreateUserDialog = false
                    showManageUserDialog = true // Go back to ManageUserDialog
                    // User created successfully
                }
            )
        }
        if (showUserListDialog) {
            UserListDialog(
                onDismiss = { 
                    showUserListDialog = false
                    showManageUserDialog = true // Go back to ManageUserDialog
                },
                onEditUser = { user ->
                    showUserListDialog = false
                    editUser = user
                }
            )
        }
        if (editUser != null) {
            EditUserDialog(
                user = editUser!!,
                onDismiss = { 
                    editUser = null
                    showUserListDialog = true // Go back to UserListDialog
                },
                onUserUpdated = { 
                    editUser = null
                    showUserListDialog = true // Go back to UserListDialog
                    // User updated successfully
                }
            )
        }
    }
}

@Composable
fun ProductTile(product: Product, categoryName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(180.dp, 140.dp)
            .padding(8.dp)
            .clickable { 
                onClick() 
            },
        elevation = AppTheme.cardElevation.dp,
        backgroundColor = when {
            product.stock <= 0 -> Color(0xFFFFE5E5) // Light red for out of stock
            else -> AppTheme.cardColor // Default white color for available stock
        },
        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                product.name, 
                fontSize = 20.sp, 
                fontWeight = FontWeight.Bold,
                color = AppTheme.textPrimary
            )
            Text(
                "Category: $categoryName", 
                fontSize = 14.sp,
                color = AppTheme.textSecondary
            )
            Text(
                "Price: ₹${product.price}", 
                fontSize = 14.sp,
                color = AppTheme.textSecondary
            )
            Text(
                "Stock: ${product.stock}", 
                fontSize = 14.sp,
                color = if (product.stock <= 0) AppTheme.errorColor else Color(0xFF90EE90) // Light green color
            )
        }
    }
}


