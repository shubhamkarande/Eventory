package com.eventory.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eventory.app.presentation.navigation.EventoryNavigation
import com.eventory.app.presentation.navigation.Screen
import com.eventory.app.ui.theme.EventoryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventoryTheme {
                EventoryApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoryApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Event, contentDescription = "Events") },
                        label = { Text("Events") },
                        selected = currentRoute == Screen.Events.route,
                        onClick = {
                            navController.navigate(Screen.Events.route) {
                                popUpTo(Screen.Events.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Schedule, contentDescription = "My Events") },
                        label = { Text("My Events") },
                        selected = currentRoute == Screen.MyEvents.route,
                        onClick = {
                            navController.navigate(Screen.MyEvents.route) {
                                popUpTo(Screen.Events.route)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scanner") },
                        label = { Text("QR Scanner") },
                        selected = currentRoute == Screen.QRScanner.route,
                        onClick = {
                            navController.navigate(Screen.QRScanner.route)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        EventoryNavigation(
            navController = navController
        )
    }
}

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        Screen.Events.route,
        Screen.MyEvents.route -> true
        else -> false
    }
}