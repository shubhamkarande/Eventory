# Eventory - Discover Events, RSVP Instantly

A modern Android app built with Kotlin and Jetpack Compose that allows users to explore local events, RSVP using QR codes, and sync events with their device calendar.

## Tech Stack

### Frontend (Mobile)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **IDE**: Android Studio
- **QR Code**: ZXing
- **Calendar**: Android Calendar Provider API

### Backend (API)
- **Language**: Java
- **Framework**: Spring Boot (RESTful API)
- **Database**: PostgreSQL
- **Deployment**: Azure App Service

## Key Features

1. **Discover Events Nearby** - Location-based event discovery with GPS
2. **Event Details** - Full descriptions with venue info and Google Maps integration
3. **Calendar Sync** - Automatic sync with Android Calendar
4. **RSVP via QR Code** - Unique QR codes for event entry verification
5. **Event Reminders** - Configurable notifications and status tracking

## Project Structure

```
eventory/
├── android/                 # Android app (Kotlin + Jetpack Compose)
├── backend/                 # Spring Boot API (Java)
├── docs/                   # Documentation and API specs
└── README.md
```

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- PostgreSQL database
- Azure account (for deployment)

### Setup Instructions
1. Clone the repository
2. Set up the backend database and API
3. Configure the Android app with API endpoints
4. Build and run the application

## Color Scheme
- Primary: #5C6BC0 (Indigo)
- Secondary: #FF7043 (Orange accent)
- Background: #F9F9F9 (light) / #121212 (dark)