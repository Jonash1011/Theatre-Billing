package org.example.ui

// ...existing code...
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import org.example.ui.CategoryDialog
import org.example.ui.AddProductDialog
import org.example.data.ProductDao
import org.example.data.Product
import org.example.data.CategoryDao
import org.example.data.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.ui.theme.AppTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.backgroundColor)
    ) {
        // Top bar with Logout and Statistic buttons
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
            ) {
                products.forEach { product ->
                    ProductTile(
                        product = product,
                        categoryName = categories.find { it.id == product.categoryId }?.name ?: "",
                        onClick = { editProduct = product }
                    )
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
    }
}

@Composable
fun ProductTile(product: Product, categoryName: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = Modifier
            .size(180.dp, 140.dp)
            .padding(8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { 
                onClick() 
            },
        elevation = AppTheme.cardElevation.dp,
        backgroundColor = if (isHovered) AppTheme.primaryLightColor else AppTheme.cardColor,
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
                color = if (product.stock <= 0) AppTheme.errorColor else AppTheme.textSecondary
            )
        }
    }
}


