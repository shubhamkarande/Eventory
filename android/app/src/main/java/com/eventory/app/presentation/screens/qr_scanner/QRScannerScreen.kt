package com.eventory.app.presentation.screens.qr_scanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    onQRCodeScanned: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var flashEnabled by remember { mutableStateOf(false) }
    var captureManager: CaptureManager? by remember { mutableStateOf(null) }
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            flashEnabled = !flashEnabled
                            captureManager?.let { manager ->
                                if (flashEnabled) {
                                    manager.setTorchOn()
                                } else {
                                    manager.setTorchOff()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (flashEnabled) Icons.Default.FlashOff else Icons.Default.FlashOn,
                            contentDescription = if (flashEnabled) "Turn off flash" else "Turn on flash"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (cameraPermissionState.status.isGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Instructions
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Point your camera at a QR code to scan it",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Camera Preview
                AndroidView(
                    factory = { context ->
                        CompoundBarcodeView(context).apply {
                            val capture = CaptureManager(context as androidx.activity.ComponentActivity, this)
                            capture.initializeFromIntent(context.intent, null)
                            capture.decode()
                            captureManager = capture
                            
                            this.setStatusText("")
                            this.decodeContinuous { result ->
                                result.text?.let { qrCode ->
                                    onQRCodeScanned(qrCode)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                )
                
                // Bottom instructions
                Text(
                    text = "Make sure the QR code is clearly visible within the frame",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Camera permission not granted
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Camera Permission Required",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please grant camera permission to scan QR codes",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() }
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            captureManager?.onDestroy()
        }
    }
}