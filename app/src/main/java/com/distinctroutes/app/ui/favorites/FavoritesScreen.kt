package com.distinctroutes.app.ui.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.distinctroutes.app.R
import com.distinctroutes.app.data.local.db.SavedPlaceEntity
import com.distinctroutes.app.domain.model.FavoriteRoute
import com.distinctroutes.app.ui.route_planner.RoutePlannerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    routePlannerViewModel: RoutePlannerViewModel,
    onNavigateToPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsState()
    val journalHistory by viewModel.journalHistory.collectAsState()
    val savedPlaces by viewModel.savedPlaces.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Favourites, 1 = Saved Places, 2 = Ride Journal

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Branding Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.direct_route),
                contentDescription = "Distinct Routes logo",
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (selectedTab) {
                        0 -> stringResource(R.string.title_favourites)
                        1 -> "Saved Places (${savedPlaces.size})"
                        else -> "Ride Journal History"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tri-Tab Filter Chips: Favourites vs Saved Places vs Ride Journal History
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text(stringResource(R.string.title_favourites)) },
                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("Places (${savedPlaces.size})") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("Journal (${journalHistory.size})") },
                leadingIcon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        when (selectedTab) {
            1 -> {
                // SAVED PLACES TAB
                if (savedPlaces.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No saved places yet. Save Home, Work, or favorite riding spots in Route Planner.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedPlaces, key = { it.id }) { place ->
                            SavedPlaceCard(
                                place = place,
                                onDelete = { viewModel.deleteSavedPlace(place.id) },
                                onLoadPlanner = {
                                    routePlannerViewModel.startAddressText.value = place.addressText
                                    routePlannerViewModel.startLat = place.latitude
                                    routePlannerViewModel.startLon = place.longitude
                                    onNavigateToPlanner()
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                // FAVOURITES (0) OR RIDE JOURNAL (2) TAB
                val activeList = if (selectedTab == 0) favorites else journalHistory

                if (activeList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics {
                                contentDescription = if (selectedTab == 0) "No saved favourite routes yet" else "No ride history yet"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == 0) stringResource(R.string.msg_no_favorites) else "No ride journal history yet. Calculated routes will automatically be logged here on-device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (selectedTab == 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { viewModel.clearJournalHistory() }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear ride journal history")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Journal")
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(activeList, key = { it.id }) { route ->
                                FavoriteRouteCard(
                                    route = route,
                                    isJournalMode = selectedTab == 2,
                                    onDelete = {
                                        if (selectedTab == 0) viewModel.removeFavorite(route.id)
                                    },
                                    onLoadPlanner = {
                                        routePlannerViewModel.startAddressText.value = route.startAddress
                                        routePlannerViewModel.startLat = route.startLat
                                        routePlannerViewModel.startLon = route.startLon
                                        routePlannerViewModel.endAddressText.value = route.endAddress
                                        routePlannerViewModel.endLat = route.endLat
                                        routePlannerViewModel.endLon = route.endLon

                                        if (route.hasViaPoint && route.viaLat != null && route.viaLon != null) {
                                            routePlannerViewModel.addViaStop(
                                                address = route.viaAddress ?: "",
                                                lat = route.viaLat,
                                                lon = route.viaLon
                                            )
                                        }

                                        routePlannerViewModel.calculateRoute()
                                        onNavigateToPlanner()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedPlaceCard(
    place: SavedPlaceEntity,
    onDelete: () -> Unit,
    onLoadPlanner: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = when (place.iconType) {
                            "HOME" -> Icons.Default.Home
                            "WORK" -> Icons.Default.Work
                            else -> Icons.Default.Place
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = place.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove saved place",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = place.addressText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onLoadPlanner,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Set as Departure in Route Planner")
            }
        }
    }
}

@Composable
fun FavoriteRouteCard(
    route: FavoriteRoute,
    isJournalMode: Boolean,
    onDelete: () -> Unit,
    onLoadPlanner: () -> Unit
) {
    val formattedDate = remember(route.savedTimestamp) {
        if (route.savedTimestamp > 0) {
            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(route.savedTimestamp))
        } else {
            SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())
        }
    }

    val displayTitle = remember(route.startAddress, route.endAddress, route.title, formattedDate) {
        val isGenericStart = route.startAddress.isBlank() || route.startAddress.startsWith("Map Pin") || route.startAddress.startsWith("Departure Point")
        val isGenericEnd = route.endAddress.isBlank() || route.endAddress.startsWith("Map Pin") || route.endAddress.startsWith("Destination Point")

        if (isGenericStart || isGenericEnd) {
            "Ride on $formattedDate"
        } else {
            val startShort = route.startAddress.split(",").first().trim()
            val endShort = route.endAddress.split(",").first().trim()
            "$startShort → $endShort"
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Route $displayTitle, distance ${route.distanceKm} km, duration ${route.durationMinutes} minutes."
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isJournalMode) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isJournalMode) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.btn_remove_favorite),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "From: ${route.startAddress}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (route.hasViaPoint && !route.viaAddress.isNullOrEmpty()) {
                Text(
                    text = "Via: ${route.viaAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "To: ${route.endAddress}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${"%.2f".format(route.distanceKm)} km • ${route.durationMinutes} mins",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = route.bikeTypeDisplayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLoadPlanner,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.btn_load_planner))
                }
            }
        }
    }
}