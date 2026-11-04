package com.distinctroutes.app.ui.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.distinctroutes.app.R
import com.distinctroutes.app.domain.model.BikeType

@Composable
fun OnboardingDialog(
    supportedCities: List<String>,
    initialCity: String,
    initialBikeType: BikeType,
    onCompleteOnboarding: (city: String, bikeType: BikeType) -> Unit,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedCity by remember { mutableStateOf(initialCity) }
    var selectedBikeType by remember { mutableStateOf(initialBikeType) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }
    var bikeTypeDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header Branding
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.direct_route),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..3) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (i == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(if (i == currentPage) 12.dp else 8.dp)
                        ) {}
                    }
                }

                when (currentPage) {
                    0 -> {
                        // Page 0: Welcome & Safety Mission
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "Safety-Aware Cycling Navigation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Distinct Routes routes cyclists, cargo bikes, wheelchairs, and mobility devices away from dangerous multi-lane highways and onto quiet backstreets, cycleways, and flat paths.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Strict 100% Privacy: Zero telemetry tracking, zero ads, zero user accounts. All data stays local.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    1 -> {
                        // Page 1: Location Permission
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "Set Up Location Access",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Location access allows Distinct Routes to center the map on your position and route from your current GPS location.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (hasLocationPermission) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GPS Location Permission Granted!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            Button(
                                onClick = onRequestLocationPermission,
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Grant Location Access 🎯")
                            }
                        }
                    }

                    2 -> {
                        // Page 2: Default City & Vehicle Profile Setup
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "Choose Default City & Vehicle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // City Selector
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { cityDropdownExpanded = true }
                        ) {
                            OutlinedTextField(
                                value = selectedCity,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Default BBBike City Context") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { cityDropdownExpanded = true }
                            )
                            DropdownMenu(
                                expanded = cityDropdownExpanded,
                                onDismissRequest = { cityDropdownExpanded = false }
                            ) {
                                supportedCities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city) },
                                        onClick = {
                                            selectedCity = city
                                            cityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Vehicle Profile Selector
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { bikeTypeDropdownExpanded = true }
                        ) {
                            OutlinedTextField(
                                value = selectedBikeType.displayName,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Default Vehicle / Mobility Device") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { bikeTypeDropdownExpanded = true }
                            )
                            DropdownMenu(
                                expanded = bikeTypeDropdownExpanded,
                                onDismissRequest = { bikeTypeDropdownExpanded = false }
                            ) {
                                BikeType.entries.forEach { bikeType ->
                                    DropdownMenuItem(
                                        text = { Text(bikeType.displayName) },
                                        onClick = {
                                            selectedBikeType = bikeType
                                            bikeTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // Page 3: Map Long-Press & Feature Guide
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "You're All Set to Ride!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📍 Long-Press Map Navigation: Long-press anywhere on the map to set Departure, Detours, or Destination.")
                            Text("⭐ Save Favourites & Ride Journal: Save frequent routes or auto-log completed rides on-device.")
                            Text("⚙️ Flexible Overlay: Collapse the search inputs card anytime for an unobstructed 90%+ map view.")
                        }
                    }
                }

                // Bottom Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentPage > 0) {
                        OutlinedButton(
                            onClick = { currentPage-- },
                            shape = CircleShape
                        ) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (currentPage < 3) {
                                currentPage++
                            } else {
                                onCompleteOnboarding(selectedCity, selectedBikeType)
                            }
                        },
                        shape = CircleShape
                    ) {
                        Text(if (currentPage == 3) "Start Riding ➔" else "Next")
                        if (currentPage < 3) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}