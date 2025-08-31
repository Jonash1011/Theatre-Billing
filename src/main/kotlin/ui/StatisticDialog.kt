package org.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import org.example.data.Category
import org.example.data.ProductDao
import org.example.data.PurchaseDao
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.example.ui.theme.AppTheme

@Composable
fun StatisticDialog(
    categories: List<Category>,
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var fromDate by remember { mutableStateOf(LocalDate.now().minusDays(7)) }
    var toDate by remember { mutableStateOf(LocalDate.now()) }
    var fromDateStr by remember { mutableStateOf(fromDate.format(formatter)) }
    var toDateStr by remember { mutableStateOf(toDate.format(formatter)) }
    var stats by remember { mutableStateOf<List<CategoryStat>>(emptyList()) }
    var showStats by remember { mutableStateOf(false) }
    var dailyPayments by remember { mutableStateOf<List<DailyPaymentStat>>(emptyList()) }
    var overallCash by remember { mutableStateOf(0.0) }
    var overallGPay by remember { mutableStateOf(0.0) }
    var overallTotal by remember { mutableStateOf(0.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = AppTheme.surfaceColor,
        shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
        title = { Text("Sales Statistics", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppTheme.primaryColor) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("From:", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = fromDateStr,
                        onValueChange = {
                            fromDateStr = it
                            runCatching { fromDate = LocalDate.parse(it, formatter) }
                        },
                        label = { Text("YYYY-MM-DD") },
                        modifier = Modifier.width(140.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text("To:", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = toDateStr,
                        onValueChange = {
                            toDateStr = it
                            runCatching { toDate = LocalDate.parse(it, formatter) }
                        },
                        label = { Text("YYYY-MM-DD") },
                        modifier = Modifier.width(140.dp)
                    )
                }
                Button(
                    onClick = {
                        stats = getCategoryStats(fromDate, toDate, categories)
                        val payments = getDailyPaymentStats(fromDate, toDate)
                        dailyPayments = payments
                        overallCash = payments.sumOf { it.cash }
                        overallGPay = payments.sumOf { it.gpay }
                        overallTotal = payments.sumOf { it.total }
                        showStats = true
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.accentColor,
                        contentColor = AppTheme.textOnPrimary
                    ),
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                ) {
                    Text("Show Statistics", fontWeight = FontWeight.Medium)
                }
                if (showStats) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (stats.isEmpty() && dailyPayments.isEmpty()) {
                            Text("No sales found for selected dates.")
                        } else {
                            stats.forEach { stat ->
                                Text(
                                    "Category: ${stat.categoryName}", 
                                    fontSize = 16.sp, 
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.primaryColor
                                )
                                stat.products.forEach { prod ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            prod.name, 
                                            fontSize = 14.sp,
                                            color = AppTheme.textPrimary
                                        )
                                        Text(
                                            "Sold: ${prod.soldQty} | ₹${prod.totalAmount}", 
                                            fontSize = 14.sp,
                                            color = AppTheme.accentColor
                                        )
                                    }
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Daily Payment Breakdown:", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.primaryColor
                            )
                            dailyPayments.forEach { day ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    elevation = 1.dp,
                                    backgroundColor = AppTheme.cardColor,
                                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            day.date, 
                                            fontSize = 14.sp, 
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.textPrimary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "Cash: ₹${day.cash}", 
                                                fontSize = 14.sp,
                                                color = AppTheme.textSecondary
                                            )
                                            Text(
                                                "GPay: ₹${day.gpay}", 
                                                fontSize = 14.sp,
                                                color = AppTheme.textSecondary
                                            )
                                            Text(
                                                "Total: ₹${day.total}", 
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppTheme.accentColor
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                elevation = 4.dp,
                                backgroundColor = AppTheme.primaryColor,
                                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Overall Summary", 
                                        fontSize = 16.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.textOnPrimary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                "Cash: ₹$overallCash", 
                                                fontSize = 14.sp,
                                                color = AppTheme.textOnPrimary
                                            )
                                            Text(
                                                "GPay: ₹$overallGPay", 
                                                fontSize = 14.sp,
                                                color = AppTheme.textOnPrimary
                                            )
                                        }
                                        Text(
                                            "Total: ₹$overallTotal", 
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.textOnPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.accentColor,
                    contentColor = AppTheme.textOnPrimary
                ),
                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
            ) { 
                Text("Close", fontWeight = FontWeight.Medium) 
            }
        }
    )
}

// Data classes for statistics

data class CategoryStat(
    val categoryName: String,
    val products: List<ProductStat>
)
data class ProductStat(
    val name: String,
    val soldQty: Int,
    val totalAmount: Double
)
data class DailyPaymentStat(
    val date: String,
    val cash: Double,
    val gpay: Double,
    val total: Double
)

// Helper function for category stats
fun getCategoryStats(from: LocalDate, to: LocalDate, categories: List<Category>): List<CategoryStat> {
    val productDao = ProductDao()
    val products = productDao.getAll()
    val purchases = PurchaseDao.getAll()
    val fromStr = from.toString()
    val toStr = to.toString()
    val filteredPurchases = purchases.filter {
        it.dateTime >= fromStr && it.dateTime <= toStr
    }
    return categories.map { cat ->
        val catProducts = products.filter { it.categoryId == cat.id }
        val prodStats = catProducts.map { prod ->
            val prodPurchases = filteredPurchases.filter { it.productId == prod.id }
            val soldQty = prodPurchases.sumOf { it.quantity }
            val totalAmount = soldQty * prod.price
            ProductStat(prod.name, soldQty, totalAmount)
        }.filter { it.soldQty > 0 }
        CategoryStat(cat.name, prodStats)
    }.filter { it.products.isNotEmpty() }
}

// Helper function for daily payment stats
fun getDailyPaymentStats(from: LocalDate, to: LocalDate): List<DailyPaymentStat> {
    val purchases = PurchaseDao.getAll()
    val productDao = ProductDao()
    val products = productDao.getAll().associateBy { it.id }
    val fromStr = from.toString()
    val toStr = to.toString()
    val filtered = purchases.filter { it.dateTime >= fromStr && it.dateTime <= toStr }
    val grouped = filtered.groupBy { it.dateTime.substring(0, 10) }
    return grouped.map { (date, list) ->
        val cash = list.filter { it.paymentMode == "Cash" }.sumOf { (products[it.productId]?.price ?: 0.0) * it.quantity }
        val gpay = list.filter { it.paymentMode == "GPay" }.sumOf { (products[it.productId]?.price ?: 0.0) * it.quantity }
        val total = cash + gpay
        DailyPaymentStat(date, cash, gpay, total)
    }.sortedBy { it.date }
}
