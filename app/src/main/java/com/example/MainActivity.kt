package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CoralAlert
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FishViewModel
import com.example.ui.NavigationScreen
import com.example.ui.UserRole
import com.example.ui.StorefrontScreen
import com.example.ui.CartScreen
import com.example.ui.OrdersHistoryScreen
import com.example.ui.AdminDashboardScreen
import com.example.ui.AdminProductsScreen
import com.example.ui.LoginScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ui.SplashLoadingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:535054863656:android:43d73cd3875345d4")
                    .setApiKey(BuildConfig.GEMINI_API_KEY.ifBlank { "AIzaSyFakeKeyForAppCompatInitialization" })
                    .setProjectId("ekkyfish-43d7")
                    .build()
                FirebaseApp.initializeApp(this, options)
                android.util.Log.d("FirebaseInit", "Firebase App initialized programmatically!")
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseInit", "Error during manual Firebase init: ${e.message}")
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: FishViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val loggedInUser by viewModel.loggedInUser.collectAsState()
                val cart by viewModel.cart.collectAsState()
                val isAdmin = loggedInUser?.role == UserRole.ADMIN
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashLoadingScreen(onFinished = { showSplash = false })
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (currentScreen != NavigationScreen.LOGIN) {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = currentScreen == NavigationScreen.STORE,
                                        onClick = { viewModel.navigateTo(NavigationScreen.STORE) },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Store") },
                                        label = { Text("Store") },
                                        modifier = Modifier.testTag("nav_store_btn")
                                    )
                                    val totalInCart = cart.size
                                    NavigationBarItem(
                                        selected = currentScreen == NavigationScreen.CART,
                                        onClick = { viewModel.navigateTo(NavigationScreen.CART) },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (totalInCart > 0) {
                                                        Badge(
                                                            containerColor = CoralAlert,
                                                            contentColor = Color.White
                                                        ) {
                                                            Text("$totalInCart")
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                            }
                                        },
                                        label = { Text("Cart") },
                                        modifier = Modifier.testTag("nav_cart_btn")
                                    )
                                    
                                    if (loggedInUser != null) {
                                        val ordersLabel = if (isAdmin) "Ledger" else "My Orders"
                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.ORDERS,
                                            onClick = { viewModel.navigateTo(NavigationScreen.ORDERS) },
                                            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = ordersLabel) },
                                            label = { Text(ordersLabel) },
                                            modifier = Modifier.testTag("nav_ledger_btn")
                                        )
                                    }

                                    if (isAdmin) {
                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.PRODUCT,
                                            onClick = { viewModel.navigateTo(NavigationScreen.PRODUCT) },
                                            icon = { Icon(Icons.Default.List, contentDescription = "Product") },
                                            label = { Text("Product") },
                                            modifier = Modifier.testTag("nav_product_btn")
                                        )
                                        NavigationBarItem(
                                            selected = currentScreen == NavigationScreen.ADMIN,
                                            onClick = { viewModel.navigateTo(NavigationScreen.ADMIN) },
                                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                                            label = { Text("Admin") },
                                            modifier = Modifier.testTag("nav_admin_btn")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        val modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                        when (currentScreen) {
                            NavigationScreen.STORE -> {
                                StorefrontScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            NavigationScreen.CART -> {
                                CartScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            NavigationScreen.ORDERS -> {
                                OrdersHistoryScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            NavigationScreen.PRODUCT -> {
                                AdminProductsScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            NavigationScreen.ADMIN -> {
                                AdminDashboardScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                            NavigationScreen.LOGIN -> {
                                LoginScreen(
                                    viewModel = viewModel,
                                    modifier = modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
