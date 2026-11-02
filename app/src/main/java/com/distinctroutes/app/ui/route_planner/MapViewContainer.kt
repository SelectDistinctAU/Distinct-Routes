package com.distinctroutes.app.ui.route_planner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.distinctroutes.app.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapViewContainer(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    modifier: Modifier = Modifier,
    viaWaypoints: List<Pair<Double, Double>> = emptyList(),
    routePoints: List<Pair<Double, Double>> = emptyList(),
    onStartPinMoved: (Double, Double) -> Unit = { _, _ -> },
    onEndPinMoved: (Double, Double) -> Unit = { _, _ -> },
    onViaPinMoved: (index: Int, Double, Double) -> Unit = { _, _, _ -> },
    onMapLongPress: (Double, Double) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    val startIcon = remember(context) {
        createBbbikeStartPin(context)
    }

    val endIcon = remember(context) {
        createBbbikeEndPin(context)
    }

    val viaIcon = remember(context) {
        createBbbikeViaPin(context)
    }

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            Configuration.getInstance().userAgentValue = "DistinctRoutes/0.1.0 (distinctroutes@pm.me; Android)"

            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(13.0)

                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            onMapLongPress(p.latitude, p.longitude)
                            return true
                        }
                        return false
                    }
                }
                overlays.add(0, MapEventsOverlay(mapEventsReceiver))
            }
        },
        onRelease = { map ->
            try {
                map.onPause()
            } catch (e: Exception) {
                // Non-blocking pause
            }
        },
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .semantics {
                this.contentDescription = "Interactive OpenStreetMap view showing Start, Destination, and optional Via Stop pins"
            },
        update = { map ->
            try {
                map.onResume()
            } catch (e: Exception) {
                // Ignore resume errors
            }

            // Keep base MapEventsOverlay at index 0
            val eventsOverlay = map.overlays.firstOrNull { it is MapEventsOverlay }
            map.overlays.clear()
            if (eventsOverlay != null) {
                map.overlays.add(eventsOverlay)
            }

            // Render route polyline if available
            if (routePoints.size >= 2) {
                val polyline = Polyline(map).apply {
                    val geoPoints = routePoints.map { GeoPoint(it.first, it.second) }
                    setPoints(geoPoints)
                    outlinePaint.color = android.graphics.Color.parseColor("#1565C0")
                    outlinePaint.strokeWidth = 14f
                    outlinePaint.strokeCap = Paint.Cap.ROUND
                    outlinePaint.strokeJoin = Paint.Join.ROUND
                }
                map.overlays.add(polyline)
            }

            // Start Pin Marker
            if (startLat != 0.0 && startLon != 0.0) {
                val startMarker = Marker(map).apply {
                    position = GeoPoint(startLat, startLon)
                    title = "Start Location"
                    icon = startIcon
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    isDraggable = true
                    setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                        override fun onMarkerDrag(marker: Marker) {}
                        override fun onMarkerDragStart(marker: Marker) {}
                        override fun onMarkerDragEnd(marker: Marker) {
                            onStartPinMoved(marker.position.latitude, marker.position.longitude)
                        }
                    })
                }
                map.overlays.add(startMarker)
            }

            // Multi-Stop Via Waypoint Markers
            viaWaypoints.forEachIndexed { idx, viaPair ->
                if (viaPair.first != 0.0 && viaPair.second != 0.0) {
                    val viaMarker = Marker(map).apply {
                        position = GeoPoint(viaPair.first, viaPair.second)
                        title = "Via Stop #${idx + 1}"
                        icon = viaIcon
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        isDraggable = true
                        setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                            override fun onMarkerDrag(marker: Marker) {}
                            override fun onMarkerDragStart(marker: Marker) {}
                            override fun onMarkerDragEnd(marker: Marker) {
                                onViaPinMoved(idx, marker.position.latitude, marker.position.longitude)
                            }
                        })
                    }
                    map.overlays.add(viaMarker)
                }
            }

            // Destination Pin Marker
            if (endLat != 0.0 && endLon != 0.0) {
                val endMarker = Marker(map).apply {
                    position = GeoPoint(endLat, endLon)
                    title = "Destination"
                    icon = endIcon
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    isDraggable = true
                    setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                        override fun onMarkerDrag(marker: Marker) {}
                        override fun onMarkerDragStart(marker: Marker) {}
                        override fun onMarkerDragEnd(marker: Marker) {
                            onEndPinMoved(marker.position.latitude, marker.position.longitude)
                        }
                    })
                }
                map.overlays.add(endMarker)
            }

            // Center map on Start location or Perth
            val centerLat = if (startLat != 0.0) startLat else -31.9505
            val centerLon = if (startLon != 0.0) startLon else 115.8605
            map.controller.setCenter(GeoPoint(centerLat, centerLon))
            map.invalidate()
        }
    )
}

private fun createBbbikeStartPin(context: Context): Drawable {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#1B5E20")
        style = Paint.Style.FILL
    }
    val path = Path().apply {
        moveTo(size / 2f, size.toFloat())
        cubicTo(size * 0.1f, size * 0.5f, 0f, size * 0.35f, size / 2f, 0f)
        cubicTo(size.toFloat(), size * 0.35f, size * 0.9f, size * 0.5f, size / 2f, size.toFloat())
        close()
    }
    canvas.drawPath(path, pinPaint)

    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size * 0.35f, size * 0.18f, innerPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun createBbbikeEndPin(context: Context): Drawable {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#B71C1C")
        style = Paint.Style.FILL
    }
    val path = Path().apply {
        moveTo(size / 2f, size.toFloat())
        cubicTo(size * 0.1f, size * 0.5f, 0f, size * 0.35f, size / 2f, 0f)
        cubicTo(size.toFloat(), size * 0.35f, size * 0.9f, size * 0.5f, size / 2f, size.toFloat())
        close()
    }
    canvas.drawPath(path, pinPaint)

    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size * 0.35f, size * 0.18f, innerPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun createBbbikeViaPin(context: Context): Drawable {
    val size = 48
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#F57F17")
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, outerPaint)

    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)

    return BitmapDrawable(context.resources, bitmap)
}