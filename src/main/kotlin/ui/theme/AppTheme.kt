package org.example.ui.theme

import androidx.compose.ui.graphics.Color

// Consistent color palette for the entire application
object AppTheme {
    // Primary colors
    val primaryColor = Color(0xFF3F51B5) // Indigo
    val primaryLightColor = Color(0xFF757DE8)
    val primaryDarkColor = Color(0xFF002984)
    
    // Background colors
    val backgroundColor = Color(0xFFE8EAF6) // Light Indigo
    val surfaceColor = Color.White
    val cardColor = Color.White
    
    // Text colors
    val textPrimary = Color(0xFF212121)
    val textSecondary = Color(0xFF757575)
    val textOnPrimary = Color.White
    
    // Accent colors
    val accentColor = Color(0xFFFF9800) // Orange
    val accentLightColor = Color(0xFFFFB74D)
    
    // Status colors
    val successColor = Color(0xFF4CAF50) // Green
    val errorColor = Color(0xFFF44336) // Red
    val warningColor = Color(0xFFFFEB3B) // Yellow
    
    // Shapes
    val cornerRadius = 8 // For rounded corners, not too extreme
    
    // Elevation
    val cardElevation = 4 // Subtle card elevation
}
