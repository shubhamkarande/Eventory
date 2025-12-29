# Eventory 🎉

**Android-first event discovery and RSVP app** with Spring Boot backend. Find nearby events, RSVP in one tap, and check in using QR codes.

## Features

### For Attendees

- 📍 **Location-based Discovery** - Find events happening near you
- 🎟️ **One-tap RSVP** - Quick and easy event registration
- 📱 **QR Tickets** - Digital tickets with scannable QR codes
- 📅 **Calendar Sync** - Add events to your calendar
- 🏷️ **Interest-based Personalization** - Get recommendations based on your interests

### For Organizers

- ✨ **Event Creation** - Create and manage events
- 📊 **Live Dashboard** - Track RSVPs and check-ins in real-time
- 📷 **QR Check-in Scanner** - Scan attendee tickets at venue entry
- 👥 **Attendee Management** - View and manage your event attendees

## Tech Stack

### Backend

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with PostgreSQL (H2 for development)
- **Flyway** for database migrations
- **Maven** for build management

### Android App

- **Kotlin** + **Jetpack Compose**
- **Material 3** Design System
- **Hilt** for Dependency Injection
- **Retrofit** + **OkHttp** for networking
- **Room** for local caching
- **DataStore** for preferences
- **CameraX** + **ZXing** for QR scanning
- **Coil** for image loading
- **Navigation Compose** for routing

## Project Structure

```
Eventory/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/com/eventory/
│   │   ├── config/            # Security & app configuration
│   │   ├── controller/        # REST API controllers
│   │   ├── dto/               # Data transfer objects
│   │   ├── exception/         # Global exception handling
│   │   ├── model/             # JPA entities
│   │   ├── repository/        # Data repositories
│   │   ├── security/          # JWT & auth components
│   │   └── service/           # Business logic
│   └── src/main/resources/
│       ├── db/migration/      # Flyway migrations
│       └── application.yml    # App configuration
│
└── app/                        # Android application
    └── mobile/
        └── src/main/java/com/eventory/
            ├── data/          # API, models, repositories
            ├── di/            # Hilt modules
            ├── navigation/    # Navigation routes
            ├── ui/            # Compose screens
            └── viewmodel/     # ViewModels
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Android Studio Hedgehog or later
- Android SDK 34

### Backend Setup

1. Navigate to the backend directory:

   ```bash
   cd backend
   ```

2. Run with Maven:

   ```bash
   mvn spring-boot:run
   ```

3. The server will start on `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`

### Android Setup

1. Open the `app` folder in Android Studio

2. Update the base URL in `NetworkModule.kt` if needed:

   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8080/" // Emulator
   // OR
   private const val BASE_URL = "http://YOUR_IP:8080/" // Physical device
   ```

3. Build and run on emulator or device

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user
- `PUT /api/auth/interests` - Update interests

### Events

- `GET /api/events` - List events (with location/category filters)
- `GET /api/events/{id}` - Get event details
- `POST /api/events` - Create event (Organizer)
- `PUT /api/events/{id}` - Update event (Organizer)
- `DELETE /api/events/{id}` - Delete event (Organizer)
- `GET /api/events/organizer` - Get organizer's events

### RSVPs

- `POST /api/events/{id}/rsvp` - RSVP to event
- `GET /api/events/{id}/rsvp` - Get user's RSVP for event
- `DELETE /api/events/{id}/rsvp` - Cancel RSVP
- `GET /api/rsvps` - Get user's RSVPs
- `POST /api/rsvps/checkin` - Check in attendee (Organizer)

## Environment Variables

### Backend

```yaml
JWT_SECRET: your-secret-key-here  # Required for production
DATABASE_URL: jdbc:postgresql://localhost:5432/eventory
DATABASE_USERNAME: postgres
DATABASE_PASSWORD: your-password
```

## User Roles

| Role | Capabilities |
|------|-------------|
| **ATTENDEE** | Browse events, RSVP, view tickets |
| **ORGANIZER** | All attendee + create/manage events, scan check-ins |
| **ADMIN** | All organizer + admin panel access |

## Screenshots

The app follows the reference designs in `stitch_welcome_to_eventory/`:

- Welcome Screen
- Event Discovery Feed
- Event Details
- Interest Selection
- Organizer Dashboard
- QR Ticket

## License

MIT License - feel free to use this project for learning or building your own event app!
