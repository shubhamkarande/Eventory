package com.eventory.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eventory.ui.event.EventDetailScreen
import com.eventory.ui.event.TicketScreen
import com.eventory.ui.home.FilterBottomSheet
import com.eventory.ui.home.FilterState
import com.eventory.ui.home.HomeScreen
import com.eventory.ui.onboarding.*
import com.eventory.ui.organizer.CreateEventScreen
import com.eventory.ui.organizer.EventAttendeesScreen
import com.eventory.ui.organizer.OrganizerDashboardScreen
import com.eventory.ui.organizer.QrScannerScreen
import com.eventory.ui.profile.ProfileScreen
import com.eventory.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()

    // Determine start destination
    val startDestination = when {
        authState.isLoggedIn && authState.isOnboardingComplete -> Screen.Home.route
        authState.isLoggedIn -> Screen.LocationPermission.route
        else -> Screen.Welcome.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                isLoading = authState.isLoading,
                error = authState.error,
                onLoginClick = { email, password -> authViewModel.login(email, password) },
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onClearError = { authViewModel.clearError() }
            )

            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    if (authState.isOnboardingComplete) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.LocationPermission.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                isLoading = authState.isLoading,
                error = authState.error,
                onRegisterClick = { name, email, password, role ->
                    authViewModel.register(name, email, password, role)
                },
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onClearError = { authViewModel.clearError() }
            )

            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Screen.LocationPermission.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.LocationPermission.route) {
            LocationPermissionScreen(
                onPermissionGranted = { navController.navigate(Screen.InterestSelection.route) },
                onSkip = { navController.navigate(Screen.InterestSelection.route) }
            )
        }

        composable(Screen.InterestSelection.route) {
            InterestSelectionScreen(
                isLoading = authState.isLoading,
                onContinue = { interests ->
                    authViewModel.updateInterests(interests)
                }
            )

            LaunchedEffect(authState.isOnboardingComplete) {
                if (authState.isOnboardingComplete) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
        }

        // Home
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeState by homeViewModel.state.collectAsState()
            var showFilters by remember { mutableStateOf(false) }

            HomeScreen(
                events = homeState.filteredEvents,
                isLoading = homeState.isLoading,
                selectedCategory = homeState.selectedCategory,
                onEventClick = { event ->
                    navController.navigate(Screen.EventDetail.createRoute(event.id))
                },
                onFilterClick = { showFilters = true },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onCategorySelect = { homeViewModel.filterByCategory(it) },
                onRefresh = { homeViewModel.refresh() }
            )

            if (showFilters) {
                FilterBottomSheet(
                    currentFilter = FilterState(selectedCategory = homeState.selectedCategory),
                    onApplyFilter = { filter ->
                        homeViewModel.filterByCategory(filter.selectedCategory)
                        showFilters = false
                    },
                    onDismiss = { showFilters = false }
                )
            }
        }

        // Event Detail
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            val eventViewModel: EventViewModel = hiltViewModel()
            val eventState by eventViewModel.state.collectAsState()

            LaunchedEffect(eventId) {
                eventViewModel.loadEvent(eventId)
            }

            EventDetailScreen(
                event = eventState.event,
                hasRsvped = eventState.hasRsvped,
                isLoading = eventState.isLoading,
                onBackClick = { navController.popBackStack() },
                onRsvpClick = { eventViewModel.rsvpToEvent() },
                onViewTicketClick = {
                    eventState.userRsvp?.let { rsvp ->
                        navController.navigate(Screen.Ticket.createRoute(rsvp.id))
                    }
                }
            )

            LaunchedEffect(eventState.rsvpSuccess) {
                if (eventState.rsvpSuccess) {
                    eventState.userRsvp?.let { rsvp ->
                        navController.navigate(Screen.Ticket.createRoute(rsvp.id))
                    }
                    eventViewModel.clearRsvpSuccess()
                }
            }
        }

        // Ticket
        composable(
            route = Screen.Ticket.route,
            arguments = listOf(navArgument("rsvpId") { type = NavType.StringType })
        ) {
            val eventViewModel: EventViewModel = hiltViewModel()
            val eventState by eventViewModel.state.collectAsState()

            TicketScreen(
                rsvp = eventState.userRsvp,
                onBackClick = { navController.popBackStack() },
                onAddToCalendarClick = { /* Calendar intent handled in TicketScreen */ }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val profileState by profileViewModel.state.collectAsState()

            ProfileScreen(
                user = profileState.user ?: authState.user,
                rsvps = profileState.rsvps,
                isLoading = profileState.isLoading,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRsvpClick = { rsvp ->
                    navController.navigate(Screen.Ticket.createRoute(rsvp.id))
                },
                onOrganizerDashboardClick = {
                    navController.navigate(Screen.OrganizerDashboard.route)
                }
            )
        }

        // Organizer Dashboard
        composable(Screen.OrganizerDashboard.route) {
            val organizerViewModel: OrganizerViewModel = hiltViewModel()
            val organizerState by organizerViewModel.state.collectAsState()

            OrganizerDashboardScreen(
                events = organizerState.events,
                isLoading = organizerState.isLoading,
                onBackClick = { navController.popBackStack() },
                onCreateEventClick = { navController.navigate(Screen.CreateEvent.route) },
                onEventClick = { event ->
                    navController.navigate(Screen.EventAttendees.createRoute(event.id))
                },
                onScanClick = { event ->
                    navController.navigate(Screen.QrScanner.createRoute(event.id))
                }
            )
        }

        // Create Event
        composable(Screen.CreateEvent.route) {
            val organizerViewModel: OrganizerViewModel = hiltViewModel()
            val organizerState by organizerViewModel.state.collectAsState()

            CreateEventScreen(
                isLoading = organizerState.isLoading,
                error = organizerState.error,
                onCreateEvent = { request ->
                    organizerViewModel.createEvent(request)
                },
                onBackClick = { navController.popBackStack() },
                onClearError = { organizerViewModel.clearMessages() }
            )

            LaunchedEffect(organizerState.successMessage) {
                if (organizerState.successMessage != null) {
                    navController.popBackStack()
                    organizerViewModel.clearMessages()
                }
            }
        }

        // Event Attendees
        composable(
            route = Screen.EventAttendees.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            val organizerViewModel: OrganizerViewModel = hiltViewModel()
            val organizerState by organizerViewModel.state.collectAsState()

            LaunchedEffect(eventId) {
                organizerViewModel.loadEventAttendees(eventId)
                organizerViewModel.loadEventStats(eventId)
            }

            val event = organizerState.events.find { it.id == eventId }

            EventAttendeesScreen(
                eventTitle = event?.title ?: "Event",
                attendees = organizerState.attendees,
                isLoading = organizerState.isLoading,
                totalRsvps = organizerState.eventStats?.totalRsvps ?: 0,
                checkedIn = organizerState.eventStats?.checkedIn ?: 0,
                onBackClick = { navController.popBackStack() },
                onScanClick = { navController.navigate(Screen.QrScanner.createRoute(eventId)) }
            )
        }

        // QR Scanner
        composable(
            route = Screen.QrScanner.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            val organizerViewModel: OrganizerViewModel = hiltViewModel()
            val organizerState by organizerViewModel.state.collectAsState()

            val event = organizerState.events.find { it.id == eventId }

            QrScannerScreen(
                eventTitle = event?.title ?: "Event",
                isLoading = organizerState.isLoading,
                checkInResult = organizerState.checkInResult,
                error = organizerState.error,
                onBackClick = { navController.popBackStack() },
                onQrScanned = { qrCode -> organizerViewModel.checkIn(qrCode) },
                onClearMessages = { organizerViewModel.clearMessages() }
            )
        }
    }
}
