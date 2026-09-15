package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.location.AsrJuristicMethod
import com.example.data.location.CalculationMethod
import com.example.data.location.CityPreset
import com.example.data.location.LocationConfig
import com.example.data.location.PrayerCalculator
import com.example.data.location.QiblaInfo
import com.example.data.repository.SakinaRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaTertiaryContainer
import com.example.ui.theme.SakinaTertiaryFixed
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationSelectorDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val locationConfig by SakinaRepository.locationConfig.collectAsState()
    val isArabic = SakinaLocaleManager.currentLanguage.collectAsState().value == AppLanguage.ARABIC
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Cities, 1: Method & Fiqh

    // Location Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            obtainAndApplyGpsLocation(context)
        } else {
            Toast.makeText(
                context,
                if (isArabic) "تم رفض إذن الموقع، يرجى اختيار المدينة يدوياً" else "Location permission denied. Please select city manually.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            PrayerCalculator.PRESET_CITIES
        } else {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            PrayerCalculator.PRESET_CITIES.filter {
                it.name.lowercase(Locale.ROOT).contains(q) ||
                it.country.lowercase(Locale.ROOT).contains(q) ||
                it.arabicName.contains(searchQuery.trim())
            }
        }
    }

    val qiblaBearing: QiblaInfo = remember(locationConfig.latitude, locationConfig.longitude) {
        PrayerCalculator.calculateQibla(locationConfig.latitude, locationConfig.longitude)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.testTag("location_selector_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SakinaPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "موقع مواقيت الصلاة" else "Prayer Times Location",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = SakinaPrimary
                        )
                        Text(
                            text = if (isArabic) "حساب فلكي دقيق واتجاه القبلة" else "Astronomical calculations & Qibla",
                            fontSize = 11.sp,
                            color = SakinaSecondary
                        )
                    }
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Location Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SakinaSecondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) locationConfig.arabicCityName else locationConfig.cityName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = SakinaPrimary
                                )
                                Text(
                                    text = "${locationConfig.countryName} · ${String.format(Locale.US, "%.2f°N, %.2f°E", locationConfig.latitude, locationConfig.longitude)}",
                                    fontSize = 11.sp,
                                    color = SakinaSecondary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SakinaPrimary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = "Qibla",
                                    tint = SakinaTertiaryFixed,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${qiblaBearing.directionText} ${qiblaBearing.bearingDegrees}°",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = "${if (isArabic) "طريقة الحساب:" else "Method:"} ${locationConfig.calculationMethod.displayName} · ${if (isArabic) locationConfig.asrJuristicMethod.arabicName else locationConfig.asrJuristicMethod.displayName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // GPS Auto-detect Button
                Button(
                    onClick = {
                        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasFine || hasCoarse) {
                            obtainAndApplyGpsLocation(context)
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("detect_gps_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SakinaPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = SakinaTertiaryFixed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تحديد الموقع الحالي عبر GPS" else "Detect Current Location via GPS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Tab Switcher: Cities vs Methods
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SakinaSecondaryContainer.copy(alpha = 0.4f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 0) SakinaPrimary else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "المدن العالمية" else "Preset Cities",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) Color.White else SakinaSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 1) SakinaPrimary else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "طريقة الحساب والفقه" else "Calculation & Fiqh",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) Color.White else SakinaSecondary
                        )
                    }
                }

                if (selectedTab == 0) {
                    // Cities Search & List
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("city_search_field"),
                        placeholder = {
                            Text(
                                text = if (isArabic) "ابحث عن مدينة أو دولة..." else "Search city or country...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = SakinaSecondary)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SakinaPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredCities, key = { it.name }) { city ->
                            val isSelected = locationConfig.cityName.equals(city.name, ignoreCase = true)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        SakinaRepository.selectPresetCity(city)
                                        Toast.makeText(
                                            context,
                                            if (isArabic) "تم ضبط الموقع على ${city.arabicName}" else "Location updated to ${city.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .testTag("city_item_${city.name.lowercase(Locale.ROOT)}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) SakinaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SakinaPrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = city.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SakinaPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = city.arabicName,
                                                fontSize = 13.sp,
                                                color = SakinaSecondary
                                            )
                                        }
                                        Text(
                                            text = "${city.country} · ${city.recommendedMethod.displayName}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = SakinaPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Calculation Method & Juristic Method Selector
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isArabic) "الهيئة المعتمدة لحساب المواقيت:" else "Calculation Authority:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )

                        CalculationMethod.values().forEach { method ->
                            val isSelected = locationConfig.calculationMethod == method
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SakinaPrimary.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable { SakinaRepository.setCalculationMethod(method) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = method.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SakinaPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = method.arabicName,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = SakinaPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isArabic) "المذهب الفقهي لحساب وقت العصر:" else "Asr Juristic Method:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsrJuristicMethod.values().forEach { asr ->
                                val isSelected = locationConfig.asrJuristicMethod == asr
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { SakinaRepository.setAsrJuristicMethod(asr) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) SakinaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SakinaPrimary) else null
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = asr.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SakinaPrimary
                                        )
                                        Text(
                                            text = asr.arabicName,
                                            fontSize = 11.sp,
                                            color = SakinaSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = if (isArabic) "حفظ وإغلاق" else "Done",
                    color = SakinaPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    )
}

@SuppressLint("MissingPermission")
private fun obtainAndApplyGpsLocation(context: Context) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            Toast.makeText(context, "Location service not available", Toast.LENGTH_SHORT).show()
            return
        }

        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }

        if (bestLocation != null) {
            val lat = bestLocation.latitude
            val lng = bestLocation.longitude
            SakinaRepository.updateWithGpsLocation(lat, lng, "GPS Location")
            Toast.makeText(
                context,
                "Updated prayer times for (${String.format(Locale.US, "%.2f", lat)}, ${String.format(Locale.US, "%.2f", lng)})",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Waiting for GPS fix. Using preset coordinates in the meantime.",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not acquire location: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
