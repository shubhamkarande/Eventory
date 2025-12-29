package com.eventory.navigation

sealed class Screen(val route: String) {
    // Onboarding
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object LocationPermission : Screen("location_permission")
    data object InterestSelection : Screen("interest_selection")

    // Main
    data object Home : Screen("home")
    data object EventDetail : Screen("event/{eventId}") {
        fun createRoute(eventId: String) = "event/$eventId"
    }
    data object RsvpConfirmation : Screen("rsvp_confirmation/{eventId}") {
        fun createRoute(eventId: String) = "rsvp_confirmation/$eventId"
    }
    data object Ticket : Screen("ticket/{rsvpId}") {
        fun createRoute(rsvpId: String) = "ticket/$rsvpId"
    }

    // Profile
    data object Profile : Screen("profile")

    // Organizer
    data object OrganizerDashboard : Screen("organizer_dashboard")
    data object CreateEvent : Screen("create_event")
    data object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: String) = "edit_event/$eventId"
    }
    data object QrScanner : Screen("qr_scanner/{eventId}") {
        fun createRoute(eventId: String) = "qr_scanner/$eventId"
    }
    data object EventAttendees : Screen("event_attendees/{eventId}") {
        fun createRoute(eventId: String) = "event_attendees/$eventId"
    }
}
