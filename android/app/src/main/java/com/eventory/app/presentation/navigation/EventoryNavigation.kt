package com.eventory.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eventory.app.presentation.screens.events.EventsScreen
import com.eventory.app.presentation.screens.event_detail.EventDetailScreen
import com.eventory.app.presentation.screens.my_events.MyEventsScreen
import com.eventory.app.presentation.screens.qr_scanner.QRScannerScreen

@Composable
fun EventoryNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Events.route
    ) {
        composable(Screen.Events.route) {
            EventsScreen(
                onEventClick = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onQRScanClick = {
                    navController.navigate(Screen.QRScanner.route)
                }
            )
        }
        
        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onQRCodeClick = { qrCode ->
                    // Handle QR code display
                }
            )
        }
        
        composable(Screen.MyEvents.route) {
            MyEventsScreen(
                onEventClick = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                }
            )
        }
        
        composable(Screen.QRScanner.route) {
            QRScannerScreen(
                onQRCodeScanned = { qrCode ->
                    // Handle QR code verification
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Events : Screen("events")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object MyEvents : Screen("my_events")
    object QRScanner : Screen("qr_scanner")
}