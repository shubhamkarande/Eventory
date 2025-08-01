package com.eventory.app.presentation.screens.event_detail

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eventory.app.presentation.components.CategoryTag
import com.eventory.app.presentation.components.LoadingIndicator
import com.eventory.app.presentation.components.ErrorMessage
import com.eventory.app.presentation.components.QRCodeDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onQRCodeClick: (String) -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    var showQRDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(eventId) {
        viewModel.loadEventDetails(eventId)
    }
    
    // Show RSVP error as snackbar
    state.rsvpError?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or toast
            viewModel.clearRsvpError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    state.event?.let { event ->
                        IconButton(
                            onClick = {
                                val shareText = "Check out this event: ${event.title}\n" +
                                        "Date: ${dateFormatter.format(event.startDateTime)}\n" +
                                        "Location: ${event.venue.name}, ${event.venue.city}"
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Event"))
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorMessage(
                        message = state.error,
                        onRetry = { viewModel.loadEventDetails(eventId) }
                    )
                }
            }
            state.event != null -> {
                EventDetailContent(
                    event = state.event,
                    rsvp = state.rsvp,
                    isRsvpLoading = state.isRsvpLoading,
                    onRSVPClick = { showReminderDialog = true },
                    onCancelRSVPClick = { viewModel.cancelRSVP(eventId) },
                    onShowQRCode = { showQRDialog = true },
                    onAddToCalendar = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, state.event.title)
                            putExtra(CalendarContract.Events.DESCRIPTION, state.event.description)
                            putExtra(CalendarContract.Events.EVENT_LOCATION, "${state.event.venue.name}, ${state.event.venue.address}")
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, state.event.startDateTime.time)
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, state.event.endDateTime.time)
                        }
                        context.startActivity(intent)
                    },
                    onOpenMaps = {
                        val uri = Uri.parse("geo:${state.event.venue.latitude},${state.event.venue.longitude}?q=${state.event.venue.name}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
    
    // QR Code Dialog
    if (showQRDialog && state.rsvp != null) {
        QRCodeDialog(
            qrCode = state.rsvp.qrCode,
            eventTitle = state.event?.title ?: "",
            onDismiss = { showQRDialog = false }
        )
    }
    
    // Reminder Settings Dialog
    if (showReminderDialog) {
        ReminderDialog(
            onConfirm = { reminderMinutes ->
                viewModel.rsvpToEvent(eventId, "user123", reminderMinutes) // TODO: Get actual user ID
                showReminderDialog = false
            },
            onDismiss = { showReminderDialog = false }
        )
    }
}

@Composable
private fun EventDetailContent(
    event: com.eventory.app.data.model.Event,
    rsvp: com.eventory.app.data.model.RSVP?,
    isRsvpLoading: Boolean,
    onRSVPClick: () -> Unit,
    onCancelRSVPClick: () -> Unit,
    onShowQRCode: () -> Unit,
    onAddToCalendar: () -> Unit,
    onOpenMaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Event Image
        event.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }
        
        // Event Content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title and Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                CategoryTag(
                    category = event.category,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date and Time Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = dateFormatter.format(event.startDateTime),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${timeFormatter.format(event.startDateTime)} - ${timeFormatter.format(event.endDateTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Location Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenMaps
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.venue.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = event.venue.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${event.venue.city}, ${event.venue.state}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open in Maps",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = "About this event",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Event Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EventStat(
                    icon = Icons.Default.People,
                    label = "Attendees",
                    value = event.currentAttendees.toString()
                )
                EventStat(
                    icon = Icons.Default.AttachMoney,
                    label = "Price",
                    value = if (event.price != null && event.price > 0) {
                        "$${String.format("%.2f", event.price)}"
                    } else {
                        "Free"
                    }
                )
                EventStat(
                    icon = Icons.Default.Person,
                    label = "Organizer",
                    value = event.organizerName
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons
            if (event.isRsvped && rsvp != null) {
                // Already RSVPed - Show QR Code and Cancel options
                Column {
                    Button(
                        onClick = onShowQRCode,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Show QR Code")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddToCalendar,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add to Calendar")
                        }
                        
                        OutlinedButton(
                            onClick = onCancelRSVPClick,
                            modifier = Modifier.weight(1f),
                            enabled = !isRsvpLoading
                        ) {
                            if (isRsvpLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Cancel, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancel RSVP")
                            }
                        }
                    }
                }
            } else {
                // Not RSVPed - Show RSVP button
                Button(
                    onClick = onRSVPClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRsvpLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (isRsvpLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RSVPing...")
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RSVP to Event")
                    }
                }
            }
        }
    }
}

@Composable
private fun EventStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReminderDialog(
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedReminder by remember { mutableStateOf(60) }
    val reminderOptions = listOf(
        30 to "30 minutes before",
        60 to "1 hour before",
        1440 to "1 day before"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Reminder") },
        text = {
            Column {
                Text("When would you like to be reminded about this event?")
                Spacer(modifier = Modifier.height(16.dp))
                reminderOptions.forEach { (minutes, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedReminder == minutes,
                            onClick = { selectedReminder = minutes }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedReminder) }) {
                Text("RSVP")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}