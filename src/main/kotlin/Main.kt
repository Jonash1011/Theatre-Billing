package org.example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import org.example.ui.AdminLoginScreen
import org.example.ui.ProductListScreen
import org.example.ui.UserBillingScreen
import org.example.ui.UserLoginScreen

@Composable
@Preview
fun App() {
    var screen by remember { mutableStateOf("home") }
    val today = java.time.LocalDate.now().toString()

    Box(
        modifier = Modifier.fillMaxSize().background(org.example.ui.theme.AppTheme.backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                MaterialTheme {
                    when (screen) {
                        "home" -> Column(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    "Lakshmi Multiplex",
                                    fontSize = 56.sp,
                                    fontWeight = FontWeight.Black,
                                    color = org.example.ui.theme.AppTheme.primaryDarkColor,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                        letterSpacing = 3.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            HomeScreen(
                                onAdminClick = { screen = "adminLogin" },
                                onUserClick = { screen = "userLogin" }
                            )
                        }
                        "adminLogin" -> AdminLoginScreen(
                            onLoginSuccess = { screen = "productList" },
                            onBack = { screen = "home" }
                        )
                        "productList" -> ProductListScreen(onLogout = { screen = "home" })
                        "userLogin" -> UserLoginScreen(
                            onLoginSuccess = { screen = "userBilling" },
                            onBack = { screen = "home" }
                        )
                        "userBilling" -> UserBillingScreen(onBack = { screen = "home" })
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        "Date: $today",
                        color = org.example.ui.theme.AppTheme.primaryDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onAdminClick: () -> Unit, onUserClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            Tile("User", onClick = onUserClick)
            Tile("Admin", onClick = onAdminClick)
        }
    }
}

@Composable
fun Tile(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color(0xFF3B82F6), RoundedCornerShape(30.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Theatre Canteen Billing",
        state = rememberWindowState(placement = WindowPlacement.Fullscreen)
    ) {
        App()
    }
}