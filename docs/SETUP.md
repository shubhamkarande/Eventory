# Eventory Setup Guide

This guide will help you set up and run the Eventory app locally and deploy it to production.

## Prerequisites

### For Android Development
- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK API level 24 or higher
- Physical Android device or emulator

### For Backend Development
- JDK 17 or later
- Maven 3.6 or later
- PostgreSQL 12 or later
- Azure account (for deployment)

## Local Development Setup

### 1. Database Setup

#### Install PostgreSQL
```bash
# On macOS with Homebrew
brew install postgresql
brew services start postgresql

# On Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql

# On Windows
# Download and install from https://www.postgresql.org/download/windows/
```

#### Create Database
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database and user
CREATE DATABASE eventory;
CREATE USER eventory WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE eventory TO eventory;
```

### 2. Backend Setup

#### Clone and Configure
```bash
cd backend

# Copy application.yml and update database credentials if needed
# The default configuration should work with the database setup above
```

#### Run the Backend
```bash
# Install dependencies and run
mvn clean install
mvn spring-boot:run

# The API will be available at http://localhost:8080
```

#### Test the API
```bash
# Test health endpoint
curl http://localhost:8080/actuator/health

# Test events endpoint (should return sample data)
curl "http://localhost:8080/api/v1/events?latitude=37.7749&longitude=-122.4194"
```

### 3. Android App Setup

#### Open in Android Studio
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the `android` folder and select it
4. Wait for Gradle sync to complete

#### Update API Configuration
1. Open `android/app/src/main/java/com/eventory/app/di/NetworkModule.kt`
2. Update the base URL to point to your backend:
   ```kotlin
   .baseUrl("http://10.0.2.2:8080/api/v1/") // For emulator
   // or
   .baseUrl("http://YOUR_LOCAL_IP:8080/api/v1/") // For physical device
   ```

#### Run the App
1. Connect an Android device or start an emulator
2. Click "Run" in Android Studio
3. The app should install and launch

## Production Deployment

### 1. Azure App Service Setup

#### Create App Service
```bash
# Install Azure CLI
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Login to Azure
az login

# Create resource group
az group create --name eventory-rg --location "East US"

# Create App Service plan
az appservice plan create --name eventory-plan --resource-group eventory-rg --sku B1 --is-linux

# Create web app
az webapp create --resource-group eventory-rg --plan eventory-plan --name eventory-backend --runtime "JAVA:17-java17"
```

#### Configure Database
```bash
# Create PostgreSQL server
az postgres server create --resource-group eventory-rg --name eventory-db --location "East US" --admin-user eventory --admin-password "YourSecurePassword123!" --sku-name GP_Gen5_2

# Create database
az postgres db create --resource-group eventory-rg --server-name eventory-db --name eventory

# Configure firewall (allow Azure services)
az postgres server firewall-rule create --resource-group eventory-rg --server eventory-db --name AllowAzureServices --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
```

#### Set Environment Variables
```bash
# Set database connection
az webapp config appsettings set --resource-group eventory-rg --name eventory-backend --settings DATABASE_URL="jdbc:postgresql://eventory-db.postgres.database.azure.com:5432/eventory?sslmode=require"

az webapp config appsettings set --resource-group eventory-rg --name eventory-backend --settings DATABASE_USERNAME="eventory@eventory-db"

az webapp config appsettings set --resource-group eventory-rg --name eventory-backend --settings DATABASE_PASSWORD="YourSecurePassword123!"

az webapp config appsettings set --resource-group eventory-rg --name eventory-backend --settings ADMIN_PASSWORD="YourAdminPassword123!"
```

### 2. Deploy Backend

#### Manual Deployment
```bash
cd backend
mvn clean package -DskipTests

# Deploy JAR file
az webapp deploy --resource-group eventory-rg --name eventory-backend --src-path target/eventory-backend-0.0.1-SNAPSHOT.jar --type jar
```

#### GitHub Actions Deployment
1. Fork this repository
2. Go to your repository settings → Secrets and variables → Actions
3. Add the following secrets:
   - `AZURE_WEBAPP_PUBLISH_PROFILE`: Download from Azure portal
   - `DATABASE_URL`: Your PostgreSQL connection string
   - `DATABASE_USERNAME`: Your database username
   - `DATABASE_PASSWORD`: Your database password
   - `ADMIN_PASSWORD`: Your admin password

4. Push changes to the main branch to trigger deployment

### 3. Update Android App

#### Update API URL
1. Open `android/app/src/main/java/com/eventory/app/di/NetworkModule.kt`
2. Update the base URL:
   ```kotlin
   .baseUrl("https://eventory-backend.azurewebsites.net/api/v1/")
   ```

#### Build Release APK
```bash
cd android
./gradlew assembleRelease

# The APK will be in app/build/outputs/apk/release/
```

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Android Tests
```bash
cd android
./gradlew test
./gradlew connectedAndroidTest  # Requires connected device/emulator
```

## API Documentation

### Events Endpoints
- `GET /api/v1/events` - Get events by location
- `GET /api/v1/events/{id}` - Get event details
- `POST /api/v1/events/{id}/rsvp` - RSVP to event
- `DELETE /api/v1/events/{id}/rsvp` - Cancel RSVP

### Categories Endpoint
- `GET /api/v1/categories` - Get all event categories

### User Endpoints
- `GET /api/v1/user/rsvps` - Get user's RSVPs

## Troubleshooting

### Common Issues

#### Android App Can't Connect to Backend
- Check if backend is running on correct port
- Verify API URL in NetworkModule.kt
- For emulator, use `10.0.2.2` instead of `localhost`
- For physical device, use your computer's IP address

#### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in application.yml
- Ensure database and user exist

#### Location Permission Issues
- Grant location permissions in Android settings
- Check if GPS is enabled on device

#### QR Code Scanner Not Working
- Grant camera permissions
- Ensure device has a working camera
- Test in good lighting conditions

### Logs and Debugging

#### Backend Logs
```bash
# View application logs
tail -f logs/application.log

# Or check Azure logs
az webapp log tail --resource-group eventory-rg --name eventory-backend
```

#### Android Logs
```bash
# View device logs
adb logcat | grep Eventory
```

## Next Steps

1. **Add Authentication**: Implement user registration and login
2. **Push Notifications**: Add Firebase Cloud Messaging for event reminders
3. **Maps Integration**: Add Google Maps for venue locations
4. **Social Features**: Add event sharing and user profiles
5. **Analytics**: Implement event tracking and analytics
6. **Offline Support**: Add offline caching for events

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Review the logs for error messages
3. Create an issue in the repository with detailed information

## License

This project is licensed under the MIT License.