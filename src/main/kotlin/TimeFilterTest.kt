import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    println("Testing time window logic for statistics")
    println("==================================================")
    
    // Test case: September 22, 2025
    val testDate = LocalDate.of(2025, 9, 22)
    val startDateTime = testDate.atTime(6, 0)  // 6:00 AM Sept 22
    val endDateTime = testDate.plusDays(1).atTime(5, 59)  // 5:59 AM Sept 23
    
    println("Test Date: $testDate")
    println("Start DateTime: $startDateTime")
    println("End DateTime: $endDateTime")
    println()
    
    // Test various purchase times
    val testPurchases = listOf(
        "2025-09-22T05:30:00" to false,  // 5:30 AM Sept 22 - should be excluded (before 6 AM)
        "2025-09-22T06:00:00" to true,   // 6:00 AM Sept 22 - should be included (start time)
        "2025-09-22T12:00:00" to true,   // 12:00 PM Sept 22 - should be included
        "2025-09-22T23:59:00" to true,   // 11:59 PM Sept 22 - should be included
        "2025-09-23T00:00:00" to true,   // 12:00 AM Sept 23 - should be included
        "2025-09-23T05:59:00" to true,   // 5:59 AM Sept 23 - should be included (end time)
        "2025-09-23T06:00:00" to false,  // 6:00 AM Sept 23 - should be excluded (after end time)
        "2025-09-23T12:00:00" to false   // 12:00 PM Sept 23 - should be excluded
    )
    
    var passedTests = 0
    var totalTests = 0
    
    testPurchases.forEach { (purchaseTimeStr, expectedInRange) ->
        totalTests++
        val purchaseDateTime = LocalDateTime.parse(purchaseTimeStr)
        val isInRange = !purchaseDateTime.isBefore(startDateTime) && !purchaseDateTime.isAfter(endDateTime)
        
        val result = if (isInRange == expectedInRange) "PASS" else "FAIL"
        if (isInRange == expectedInRange) passedTests++
        
        println("Purchase: $purchaseTimeStr -> In Range: $isInRange, Expected: $expectedInRange [$result]")
    }
    
    println()
    println("Test Results: $passedTests/$totalTests tests passed")
    
    if (passedTests == totalTests) {
        println("✅ All tests passed! Time window logic is working correctly.")
        println("Time window correctly includes purchases from 6:00 AM on the 'from' date")
        println("until 5:59 AM on the day after the 'to' date.")
    } else {
        println("❌ Some tests failed. Time window logic needs adjustment.")
    }
}