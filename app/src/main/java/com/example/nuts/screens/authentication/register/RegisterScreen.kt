package com.example.nuts.screens.authentication.register

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.screens.authentication.AuthViewModel
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.state.AuthState
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.nuts.R
import com.example.nuts.ui.theme.brown
import com.example.nuts.ui.theme.krem
import kotlinx.coroutines.delay

@Composable
fun Register(
    navController: NavHostController
) {
    val context = LocalContext.current

    val authRepository = remember {
        AuthRepository(
            sbClient = DatabaseSupabaseClient.client(),
            context = context
        )
    }
    val viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(authRepository)
    )
    val authState by viewModel.authState.observeAsState()

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val  state = authState) {
            is AuthState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
                isLoading = false
                delay(2000)
                navController.navigate(ScreenNuts.Login.route) {
                    popUpTo(ScreenNuts.Register.route) { inclusive = true }
                }
            }

            is AuthState.Loading -> {
                isLoading = true
            }

            is AuthState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
                viewModel.resetState()
                isLoading = false
            }
            else -> {
                isLoading = false
            }
        }
    }
    RegisterScreen(
        navController = navController,
        email = email,
        onEmailChange = { email = it },
        userName = username,
        onUserNameChange = { username = it },
        userPassword = password,
        onUserPasswordChange = { password = it },
        userConfirmPassword = confirmPassword,
        onUserConfirmPasswordChange = { confirmPassword = it },
        passwordVisibility = passwordVisibility,
        onPasswordVisibilityChange = { passwordVisibility = it },
        confirmPasswordVisibility = confirmPasswordVisibility,
        onConfirmPasswordVisibilityChange = { confirmPasswordVisibility = it },
        register = { viewModel.register(email,username,password,confirmPassword) },
        snackbarHostState = snackbarHostState,
        isLoading = isLoading,
    )
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    email: String = "",
    onEmailChange: (String) -> Unit = {},
    userName: String = "",
    onUserNameChange: (String) -> Unit = {},
    userPassword: String = "",
    onUserPasswordChange: (String) -> Unit = {},
    userConfirmPassword: String = "",
    onUserConfirmPasswordChange: (String) -> Unit = {},
    passwordVisibility: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    confirmPasswordVisibility: Boolean,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    register: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    isLoading: Boolean = false,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = krem,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.4f)
                    .aspectRatio(1f)
                    .padding(top = 70.dp),
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplikasi"
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Register",
                    fontWeight = FontWeight.Bold,
                    color = brown,
                    fontSize = 45.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = Color.Gray
                    )
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = Color.Gray
                    )
                )

                OutlinedTextField(
                    value = userPassword,
                    onValueChange = onUserPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.95f),
                    visualTransformation = if (passwordVisibility)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisibility)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { onPasswordVisibilityChange(!passwordVisibility) }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = Color.Gray
                    )
                )

                OutlinedTextField(
                    value = userConfirmPassword,
                    onValueChange = onUserConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.95f),
                    visualTransformation = if (confirmPasswordVisibility)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisibility)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = {
                            onConfirmPasswordVisibilityChange(!confirmPasswordVisibility)
                        }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle Confirm Password Visibility"
                            )
                        }
                    }, leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        Log.d("RegisterScreen", "Tombol Register diklik")
                        register()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(63.dp)
                        .padding(vertical = 8.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLoading) brown else Color.Gray,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "Loading...",
                            fontSize = 18.sp,
                        )
                    } else {
                        Text(
                            "Register",
                            fontSize = 18.sp,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Already have account? ",
                        fontSize = 13.sp,
                    )
                    Text(
                        text = "Login",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = brown,
                        modifier = Modifier.clickable { navController.navigate(ScreenNuts.Login.route) }
                    )
                }
            }
        }
    }
}





