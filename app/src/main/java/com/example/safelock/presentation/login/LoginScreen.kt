package com.example.safelock.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.safelock.R


@Composable
fun LoginScreen(
    navController: NavController?
) {
    val modifier: Modifier = Modifier
    var nav: NavController
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxSize()
        .padding(top = 55.dp)
        .background(Color(0xFFF5F7FB))
    ){
        Column(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Hi there!",
                style = MaterialTheme.typography.displayLarge,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Welcome to safe city",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(80.dp))

            //Email
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text("Email Address")},
                modifier = modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(30.dp))

            //Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.password_lock),
                        contentDescription = "Password Icon",
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(30.dp)
                    )
                },
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(88.dp))

            Button(
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Login"
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dont have an account?", color = Color.Gray)
                TextButton(onClick = {navController?.navigate("Sign up")}) {
                    Text("Sign up", color = Color.Blue)
                }

            }

        }

    }
}




@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(modifier: Modifier = Modifier) {
    LoginScreen(navController = null)
}