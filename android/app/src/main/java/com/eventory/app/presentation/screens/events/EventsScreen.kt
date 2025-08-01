package com.eventory.app.presentation.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventory.app.presentation.components.EventCard
import com.eventory.app.presentation.components.CategoryChip
import com.eventory.app.presentation.components.LoadingIndicator
import com.eventory.app.presentation.components.ErrorMessage
import com.eventory.app.util.LocationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit,
    onQRScanClick: () -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val categories = viewModel.categories.value
    val context = LocalContext.current
    val listState = rememberLazyListState()
    
    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Get user location and load events
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                viewModel.loadEvents(latitude, longitude)
            }
        } else {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    
    // Load more events when reaching the end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && 
                    lastVisibleIndex >= state.events.size - 3 && 
                    state.hasNextPage && 
                    !state.isLoading) {
                    LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                        viewModel.loadMoreEvents(latitude, longitude, state.selectedCategory)
                    }
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Events") },
                actions = {
                    IconButton(onClick = onQRScanClick) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scanner")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filters
            if (categories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryChip(
                            text = "All",
                            isSelected = state.selectedCategory == null,
                            onClick = { 
                                viewModel.filterByCategory(null)
                                LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                                    viewModel.refreshEvents(latitude, longitude)
                                }
                            }
                        )
                    }
                    items(categories) { category ->
                        CategoryChip(
                            text = category.name,
                            isSelected = state.selectedCategory == category.id,
                            onClick = { 
                                viewModel.filterByCategory(category.id)
                                LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                                    viewModel.refreshEvents(latitude, longitude, category.id)
                                }
                            }
                        )
                    }
                }
            }
            
            // Events list
            when {
                state.isLoading && state.events.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
                state.error != null && state.events.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorMessage(
                            message = state.error,
                            onRetry = {
                                LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                                    viewModel.refreshEvents(latitude, longitude, state.selectedCategory)
                                }
                            }
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.events) { event ->
                            EventCard(
                                event = event,
                                onClick = { onEventClick(event.id) }
                            )
                        }
                        
                        if (state.isLoading && state.events.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}