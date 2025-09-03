package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import org.example.ui.theme.AppTheme
import org.example.data.UserDao

@Composable
fun UserLoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }
    val loginButtonFocusRequester = remember { FocusRequester() }

    // Function to handle login
    fun performLogin() {
        val user = UserDao.getByUsername(username)
        if (user != null && user.password == password && !user.isAdmin) {
            error = ""
            onLoginSuccess()
        } else {
            error = "Invalid credentials or user not found."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(400.dp),
            elevation = AppTheme.cardElevation.dp,
            backgroundColor = AppTheme.surfaceColor,
            shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "User Login",
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.primaryColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { newValue -> 
                        username = newValue
                        error = ""
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor,
                        focusedLabelColor = AppTheme.primaryColor,
                        unfocusedBorderColor = Color.Gray,
                        backgroundColor = Color.White
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { newValue -> 
                        password = newValue
                        error = ""
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.primaryColor,
                        cursorColor = AppTheme.primaryColor,
                        focusedLabelColor = AppTheme.primaryColor,
                        unfocusedBorderColor = Color.Gray,
                        backgroundColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            performLogin()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = AppTheme.primaryColor
                            )
                        }
                    }
                )
                
                if (error.isNotEmpty()) {
                    Text(error, color = AppTheme.errorColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        performLogin()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.primaryColor,
                        contentColor = AppTheme.textOnPrimary
                    ),
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                ) {
                    Text("Login", fontWeight = FontWeight.Bold)
                }
                
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppTheme.primaryColor
                    ),
                    shape = RoundedCornerShape(AppTheme.cornerRadius.dp)
                ) {
                    Text("Back")
                }
            } // End of Column
        } // End of Card
    } // End of Box
} // End of Composable
