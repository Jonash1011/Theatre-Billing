package org.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import org.example.data.Category
import org.example.data.ProductDao
import org.example.data.PurchaseDao
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.example.ui.theme.AppTheme
import org.example.util.PdfBillGenerator

@Composable
fun StatisticDialog(
    categories: List<Category>,
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var fromDate by remember { mutableStateOf(LocalDate.now()) }
    var toDate by remember { mutableStateOf(LocalDate.now()) }
    var fromDateStr by remember { mutableStateOf(fromDate.format(formatter)) }
    var toDateStr by remember { mutableStateOf(toDate.format(formatter)) }
    var stats by remember { mutableStateOf<List<CategoryStat>>(emptyList()) }
    var showStats by remember { mutableStateOf(false) }
    var dailyPayments by remember { mutableStateOf<List<DailyPaymentStat>>(emptyList()) }
    var overallCash by remember { mutableStateOf(0.0) }
    var overallGPay by remember { mutableStateOf(0.0) }
    var overallTotal by remember { mutableStateOf(0.0) }
    val scrollState = rememberScrollState()

    // Scroll to top when new statistics are loaded
    LaunchedEffect(showStats) {
        if (showStats) {
            scrollState.animateScrollTo(0)
        }
    }

    // Custom static dialog instead of AlertDialog to prevent dragging
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent overlay
    ) {
        Card(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.Center)
                .width(900.dp)
                .height(650.dp),
            backgroundColor = AppTheme.surfaceColor,
            shape = RoundedCornerShape(AppTheme.cornerRadius.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.primaryColor)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        " ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.textOnPrimary
                    )
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppTheme.textOnPrimary
                        )
                    }
                }
                
                // Main content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                // LEFT SIDE - Controls and Title
                Column(
                    modifier = Modifier
                        .weight(0.35f) // 35% of width
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        backgroundColor = AppTheme.primaryColor,
                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                    ) {
                        Text(
                            "Sales Statistics",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.textOnPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    
                    // Date Inputs Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 1.dp,
                        backgroundColor = AppTheme.cardColor,
                        shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Select Date Range",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.primaryColor
                            )
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("From:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    OutlinedTextField(
                                        value = fromDateStr,
                                        onValueChange = {
                                            fromDateStr = it
                                            runCatching { fromDate = LocalDate.parse(it, formatter) }
                                        },
                                        label = { Text("YYYY-MM-DD") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("To:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    OutlinedTextField(
                                        value = toDateStr,
                                        onValueChange = {
                                            toDateStr = it
                                            runCatching { toDate = LocalDate.parse(it, formatter) }
                                        },
                                        label = { Text("YYYY-MM-DD") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    
                    // Show Statistics Button
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
                        Text("Show Statistics", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    }
                }
                
                // RIGHT SIDE - Statistics Results
                Column(
                    modifier = Modifier
                        .weight(0.65f) // 65% of width
                        .fillMaxHeight()
                ) {
                    // SCROLLABLE STATISTICS CONTENT
                    if (showStats) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .border(
                                    width = 1.dp,
                                    color = AppTheme.primaryColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                ),
                            elevation = 2.dp,
                            backgroundColor = AppTheme.cardColor,
                            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Fixed header for scroll area
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = AppTheme.primaryColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(
                                        topStart = AppTheme.cornerRadius.dp,
                                        topEnd = AppTheme.cornerRadius.dp
                                    )
                                ) {
                                    Text(
                                        "📊 Statistics Results",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.primaryColor,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                
                                // Scrollable content
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .verticalScroll(scrollState)
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    if (stats.isEmpty() && dailyPayments.isEmpty()) {
                                        Text(
                                            "No sales found for selected dates.",
                                            fontSize = 16.sp,
                                            color = AppTheme.textSecondary,
                                            modifier = Modifier.padding(20.dp)
                                        )
                                    } else {
                                        // Category-wise statistics
                                        if (stats.isNotEmpty()) {
                                            stats.forEach { stat ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    elevation = 1.dp,
                                                    backgroundColor = AppTheme.surfaceColor,
                                                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            "Category: ${stat.categoryName}", 
                                                            fontSize = 16.sp, 
                                                            fontWeight = FontWeight.Bold,
                                                            color = AppTheme.primaryColor
                                                        )
                                                        Spacer(Modifier.height(8.dp))
                                                        stat.products.forEach { prod ->
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    prod.name, 
                                                                    fontSize = 14.sp,
                                                                    color = AppTheme.textPrimary,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                Text(
                                                                    "Sold: ${prod.soldQty} | ₹${prod.totalAmount}", 
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    color = AppTheme.accentColor
                                                                )
                                                            }
                                                            if (prod != stat.products.last()) {
                                                                Spacer(Modifier.height(4.dp))
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            Spacer(Modifier.height(16.dp))
                                        }
                                        
                                        // Daily payment breakdown
                                        if (dailyPayments.isNotEmpty()) {
                                            Text(
                                                "Daily Payment Breakdown:", 
                                                fontSize = 16.sp, 
                                                fontWeight = FontWeight.Bold,
                                                color = AppTheme.primaryColor
                                            )
                                            Spacer(Modifier.height(8.dp))
                                            
                                            dailyPayments.forEach { day ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    elevation = 1.dp,
                                                    backgroundColor = AppTheme.surfaceColor,
                                                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            day.date, 
                                                            fontSize = 14.sp, 
                                                            fontWeight = FontWeight.Bold,
                                                            color = AppTheme.textPrimary
                                                        )
                                                        Spacer(Modifier.height(4.dp))
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
                                            
                                            Spacer(Modifier.height(16.dp))
                                            
                                            // Overall summary - highlighted
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                elevation = 4.dp,
                                                backgroundColor = AppTheme.primaryColor,
                                                shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Text(
                                                        "Overall Summary", 
                                                        fontSize = 16.sp, 
                                                        fontWeight = FontWeight.Bold,
                                                        color = AppTheme.textOnPrimary
                                                    )
                                                    Spacer(Modifier.height(8.dp))
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
                                            
                                            Spacer(Modifier.height(16.dp))
                                            
                                            // Print and Cancel buttons below total summary
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        generateStatisticsReport(stats, dailyPayments, overallCash, overallGPay, overallTotal, fromDate, toDate)
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        backgroundColor = AppTheme.accentColor,
                                                        contentColor = AppTheme.textOnPrimary
                                                    ),
                                                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                                ) {
                                                    Text("Print Report", fontWeight = FontWeight.Medium)
                                                }
                                                
                                                Button(
                                                    onClick = onDismiss,
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        backgroundColor = AppTheme.textSecondary,
                                                        contentColor = AppTheme.textOnPrimary
                                                    ),
                                                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                                                ) {
                                                    Text("Cancel", fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Placeholder when no stats are shown
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            elevation = 1.dp,
                            backgroundColor = AppTheme.surfaceColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text(
                                    "Select dates and click 'Show Statistics' to view results",
                                    fontSize = 14.sp,
                                    color = AppTheme.textSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    }
                }
                }
                
                // Bottom buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showStats && (stats.isNotEmpty() || dailyPayments.isNotEmpty())) {
                        Button(
                            onClick = {
                                generateStatisticsReport(stats, dailyPayments, overallCash, overallGPay, overallTotal, fromDate, toDate)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = AppTheme.primaryColor,
                                contentColor = AppTheme.textOnPrimary
                            ),
                            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                        ) { 
                            Text("Print", fontWeight = FontWeight.Medium) 
                        }
                    }
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
            }
        }
    }
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
    println("Getting stats from ${from} 6:00 AM to ${to.plusDays(1)} 5:59 AM")
    
    val productDao = ProductDao()
    val products = productDao.getAll()
    val purchases = PurchaseDao.getAll()
    
    println("Total purchases in DB: ${purchases.size}")
    
    val startDateTime = from.atTime(6, 0, 0) // 6:00:00 AM on start date
    val endDateTime = to.plusDays(1).atTime(5, 59, 59) // 5:59:59 AM next day
    
    println("Start datetime: $startDateTime")
    println("End datetime: $endDateTime")
    
    val filteredPurchases = purchases.filter { purchase ->
        try {
            // Parse ISO format date (handles the 'T' separator and optional nanoseconds)
            val purchaseDateTime = LocalDateTime.parse(purchase.dateTime)
            println("Purchase datetime: $purchaseDateTime for product ${purchase.productId}")
            
            // Include exact boundary times (6:00:00 AM and 5:59:59 AM)
            val isInRange = (purchaseDateTime.isEqual(startDateTime) || purchaseDateTime.isAfter(startDateTime)) &&
                          (purchaseDateTime.isEqual(endDateTime) || purchaseDateTime.isBefore(endDateTime))
            
            if (isInRange) {
                println("Purchase is in range: ${purchase.dateTime}")
            } else {
                println("Purchase outside range: ${purchase.dateTime}")
            }
            isInRange
        } catch (e: Exception) {
            println("Error parsing date ${purchase.dateTime}: ${e.message}")
            false // Skip invalid dates
        }
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

// Helper function to get the latest sales date
fun getLatestSalesDate(): LocalDate? {
    val purchases = PurchaseDao.getAll()
    if (purchases.isEmpty()) return null
    
    val latestDateTime = purchases.maxOfOrNull { it.dateTime }
    return if (latestDateTime != null) {
        LocalDate.parse(latestDateTime.substring(0, 10))
    } else null
}

// Helper function for daily payment stats
fun getDailyPaymentStats(from: LocalDate, to: LocalDate): List<DailyPaymentStat> {
    println("Getting payment stats for date range: $from to $to")
    
    val purchases = PurchaseDao.getAll()
    println("Total purchases found: ${purchases.size}")
    
    val productDao = ProductDao()
    val products = productDao.getAll().associateBy { it.id }
    val startDateTime = from.atTime(6, 0, 0) // 6:00:00 AM on start date
    val endDateTime = to.plusDays(1).atTime(5, 59, 59) // 5:59:59 AM next day
    
    println("Filtering purchases between $startDateTime and $endDateTime")
    
    val filtered = purchases.filter { purchase ->
        try {
            // Parse ISO format date (handles the 'T' separator and optional nanoseconds)
            val purchaseDateTime = LocalDateTime.parse(purchase.dateTime)
            println("Checking purchase: ${purchase.dateTime} (${purchase.productId})")
            
            // Include exact boundary times (6:00:00 AM and 5:59:59 AM)
            val isInRange = (purchaseDateTime.isEqual(startDateTime) || purchaseDateTime.isAfter(startDateTime)) &&
                          (purchaseDateTime.isEqual(endDateTime) || purchaseDateTime.isBefore(endDateTime))
            
            if (isInRange) {
                println("Purchase is in range: ${purchase.dateTime}")
            } else {
                println("Purchase outside range: ${purchase.dateTime}")
            }
            isInRange
        } catch (e: Exception) {
            println("Failed to parse date: ${purchase.dateTime} - ${e.message}")
            false // Skip invalid dates
        }
    }
    // Group by purchase date (calendar day), using the parsed LocalDateTime
    val grouped = filtered.groupBy { 
        try {
            LocalDateTime.parse(it.dateTime).toLocalDate().toString()
        } catch (e: Exception) {
            // This shouldn't happen as we've already filtered invalid dates
            it.dateTime.substring(0, 10)
        }
    }
    return grouped.map { (date, list) ->
        val cash = list.filter { it.paymentMode == "Cash" }.sumOf { (products[it.productId]?.price ?: 0.0) * it.quantity }
        val gpay = list.filter { it.paymentMode == "GPay" }.sumOf { (products[it.productId]?.price ?: 0.0) * it.quantity }
        val total = cash + gpay
        DailyPaymentStat(date, cash, gpay, total)
    }.sortedBy { it.date }
}

// Function to generate and print statistics report
fun generateStatisticsReport(
    stats: List<CategoryStat>,
    dailyPayments: List<DailyPaymentStat>,
    overallCash: Double,
    overallGPay: Double,
    overallTotal: Double,
    fromDate: LocalDate,
    toDate: LocalDate
) {
    try {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val fromDateStr = fromDate.format(formatter)
        val toDateStr = toDate.format(formatter)
        
        // Create a bill-style text report
        val report = buildString {
            appendLine("LAKSHMI MULTIPLEX")
            appendLine("Theatre Canteen")
            appendLine("Sales Statistics Report")
            appendLine("=".repeat(49))
            appendLine("Period: $fromDateStr to $toDateStr")
            appendLine("Generated: ${java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))}")
            appendLine()
            
            if (stats.isNotEmpty()) {
                appendLine("CATEGORY WISE SALES:")
                appendLine("-".repeat(32))
                stats.forEach { stat ->
                    appendLine("Category: ${stat.categoryName}")
                    stat.products.forEach { prod ->
                        val itemName = if (prod.name.length > 15) prod.name.substring(0, 15) else prod.name.padEnd(15)
                        val qtyStr = prod.soldQty.toString().padStart(3)
                        val amountStr = "₹${prod.totalAmount.toInt()}".padStart(8)
                        appendLine("$itemName  $qtyStr    $amountStr")
                    }
                    appendLine()
                }
            }
            
            
            appendLine("OVERALL SUMMARY:")
            appendLine("-".repeat(32))
            appendLine("Total Cash: ₹${overallCash.toInt()}")
            appendLine("Total GPay: ₹${overallGPay.toInt()}")
            appendLine("GRAND TOTAL: ₹${overallTotal.toInt()}")
            appendLine()
            appendLine("Thank you!")
        }
        
        // Generate PDF and print the report
        PdfBillGenerator.printStatisticsReport(report)
        
        // Show success message
        println("✅ Statistics report generated successfully!")
        println("📄 PDF saved to Documents folder")
        
    } catch (e: Exception) {
        println("❌ Error generating statistics report: ${e.message}")
        e.printStackTrace()
    }
}