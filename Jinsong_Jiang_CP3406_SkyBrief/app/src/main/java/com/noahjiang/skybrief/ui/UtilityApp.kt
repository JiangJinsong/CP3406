package com.noahjiang.skybrief.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noahjiang.skybrief.data.repository.TemperatureUnit
import com.noahjiang.skybrief.data.repository.WeatherDashboard
import com.noahjiang.skybrief.domain.ComfortLevel
import com.noahjiang.skybrief.ui.theme.SkyBriefTheme

private enum class AppTab(val label: String) {
    UTILITY("Utility"),
    SETTINGS("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityApp(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedTab = AppTab.entries[selectedTabIndex]

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SkyBrief") },
                actions = {
                    if (selectedTab == AppTab.UTILITY) {
                        IconButton(onClick = viewModel::refreshWeather) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh weather")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == AppTab.UTILITY,
                    onClick = { selectedTabIndex = AppTab.UTILITY.ordinal },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(AppTab.UTILITY.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == AppTab.SETTINGS,
                    onClick = { selectedTabIndex = AppTab.SETTINGS.ordinal },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(AppTab.SETTINGS.label) }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.UTILITY -> UtilityScreen(
                uiState = uiState,
                contentPadding = innerPadding,
                onRefresh = viewModel::refreshWeather
            )

            AppTab.SETTINGS -> SettingsScreen(
                uiState = uiState,
                contentPadding = innerPadding,
                onCityInputChanged = viewModel::onCityInputChanged,
                onTemperatureUnitChanged = viewModel::onTemperatureUnitChanged,
                onShowDetailedMetricsChanged = viewModel::onShowDetailedMetricsChanged,
                onApplyCity = viewModel::applyCitySetting
            )
        }
    }
}

@Composable
private fun UtilityScreen(
    uiState: WeatherUiState,
    contentPadding: PaddingValues,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "At-a-glance outdoor decision",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "A compact weather utility for deciding whether to walk, commute, or stay indoors.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            uiState.isLoading -> LoadingCard()
            uiState.dashboard != null -> DashboardContent(
                dashboard = uiState.dashboard,
                showDetailedMetrics = uiState.showDetailedMetrics
            )
            else -> EmptyState(onRefresh = onRefresh)
        }

        uiState.errorMessage?.let { message ->
            ErrorCard(message = message, onRefresh = onRefresh)
        }
    }
}

@Composable
private fun DashboardContent(
    dashboard: WeatherDashboard,
    showDetailedMetrics: Boolean
) {
    SummaryCard(dashboard)
    ComfortCard(dashboard)
    if (showDetailedMetrics) {
        MetricGrid(dashboard)
    }
}

@Composable
private fun SummaryCard(dashboard: WeatherDashboard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Weather summary for ${dashboard.locationName}. Decision: ${dashboard.comfortLevel.decision}. Temperature: ${dashboard.formattedTemperature}."
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Text(
                    text = dashboard.locationName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = dashboard.comfortLevel.decision,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dashboard.formattedTemperature,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            StatusPill("${dashboard.condition} - ${dashboard.comfortLevel.label}")
            Text(
                text = dashboard.advice,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Updated: ${dashboard.updatedAt}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ComfortCard(dashboard: WeatherDashboard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Outdoor comfort score",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = dashboard.comfortLevel.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${dashboard.comfortScore}/100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { dashboard.comfortScore / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Score combines temperature, humidity, wind, rain probability, and UV risk.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricGrid(dashboard: WeatherDashboard) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Feels like", dashboard.formattedApparentTemperature, Modifier.weight(1f))
            MetricCard("Rain chance", "${dashboard.precipitationProbability}%", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Wind", "%.1f km/h".format(dashboard.windSpeedKmh), Modifier.weight(1f))
            MetricCard("Humidity", "${dashboard.humidity}%", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("UV index", "%.1f".format(dashboard.uvIndex), Modifier.weight(1f))
            MetricCard("Mode", "Live API", Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator()
            Text("Loading live weather data...")
        }
    }
}

@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("No dashboard data loaded.", style = MaterialTheme.typography.titleMedium)
            Button(onClick = onRefresh) {
                Text("Load weather")
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRefresh: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Text(
                    text = message,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = onRefresh) {
                Text("Try again")
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    uiState: WeatherUiState,
    contentPadding: PaddingValues,
    onCityInputChanged: (String) -> Unit,
    onTemperatureUnitChanged: (TemperatureUnit) -> Unit,
    onShowDetailedMetricsChanged: (Boolean) -> Unit,
    onApplyCity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "These controls update the utility screen. They are intentionally held in ViewModel state only, so persistent storage is not required.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = uiState.cityInput,
                    onValueChange = onCityInputChanged,
                    label = { Text("City") },
                    supportingText = { Text("Example: Singapore, Cairns, Brisbane") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = onApplyCity, modifier = Modifier.fillMaxWidth()) {
                    Text("Apply city")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Temperature unit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                UnitOption(
                    title = "Celsius",
                    selected = uiState.temperatureUnit == TemperatureUnit.CELSIUS,
                    onClick = { onTemperatureUnitChanged(TemperatureUnit.CELSIUS) }
                )
                UnitOption(
                    title = "Fahrenheit",
                    selected = uiState.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                    onClick = { onTemperatureUnitChanged(TemperatureUnit.FAHRENHEIT) }
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Detailed metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Show or hide the detailed cards on the main utility screen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.showDetailedMetrics,
                    onCheckedChange = onShowDetailedMetricsChanged
                )
            }
        }
    }
}

@Composable
private fun UnitOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UtilityScreenPreview() {
    SkyBriefTheme {
        UtilityScreen(
            uiState = WeatherUiState(dashboard = sampleDashboard),
            contentPadding = PaddingValues(0.dp),
            onRefresh = {}
        )
    }
}

private val sampleDashboard = WeatherDashboard(
    locationName = "Singapore",
    temperature = 29.4,
    apparentTemperature = 33.0,
    temperatureUnit = TemperatureUnit.CELSIUS,
    humidity = 72,
    windSpeedKmh = 12.5,
    precipitationProbability = 35,
    uvIndex = 7.1,
    condition = "Partly cloudy",
    comfortScore = 74,
    comfortLevel = ComfortLevel.GOOD,
    advice = "Generally suitable for outdoor plans, with normal preparation.",
    updatedAt = "2026-06-18T09:00"
)
