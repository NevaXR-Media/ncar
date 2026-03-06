package com.nevaxr.foundation.car.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nevaxr.foundation.car.NCarGear
import com.nevaxr.foundation.car.UnitEnergy
import com.nevaxr.foundation.car.UnitSpeed
import com.nevaxr.foundation.car.convert
import com.nevaxr.foundation.car.demo.ui.theme.*
import com.nevaxr.foundation.car.format
import com.nevaxr.foundation.car.normalized
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class DashboardCategory(val label: String, val accent: Color) {
  Overview("Overview", DemoBlueS100),
  Climate("Climate", DemoOrangeS100),
  Cabin("Cabin", DemoGreenS300),
  Access("Access", DemoRedS100),
  Power("Power", DemoYellowS100),
  Device("Device", DemoVioletS100),
}

private data class DashboardField(
  val label: String,
  val value: String,
)

private data class DashboardCard(
  val category: DashboardCategory,
  val title: String,
  val accent: Color,
  val fields: List<DashboardField>,
)

@Composable
fun CarDashboardScreen(state: CarState, modifier: Modifier = Modifier) {
  var selectedCategory by remember { mutableStateOf(DashboardCategory.Overview) }
  var controlsVisible by rememberSaveable { mutableStateOf(false) }
  val gridState = rememberLazyGridState()
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  val cards = listOf(
    DashboardCard(
      category = DashboardCategory.Overview,
      title = "Drive Status",
      accent = DemoBlueS100,
      fields = listOf(
        DashboardField("Speed", state.speed.format(context, UnitSpeed.kilometersPerHour)),
        DashboardField("Normalized", "${(state.speed.normalized() * 100f).roundToInt()}%"),
        DashboardField("Gear", state.gear.name),
        DashboardField("Mode", state.drivingMode.toString()),
        DashboardField("Acceleration", state.acceleration.toCompactPercent()),
        DashboardField("Steering", state.steeringWheelAngle.format()),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Climate,
      title = "HVAC Temperature",
      accent = DemoOrangeS100,
      fields = listOf(
        DashboardField("Driver", state.hvacTemperature.driver.format(context)),
        DashboardField("Passenger", state.hvacTemperature.passenger.format(context)),
        DashboardField("Interior", state.hvacInteriorTemperature.format()),
        DashboardField("Exterior", state.hvacExteriorTemperature.format()),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Climate,
      title = "HVAC Controls",
      accent = DemoPinkS100,
      fields = listOf(
        DashboardField("AC", state.hvacStatus.toOnOff()),
        DashboardField("Dual", state.hvacDualStatus.toOnOff()),
        DashboardField("Max", state.hvacMaxStatus.toOnOff()),
        DashboardField("Fan Driver", state.hvacFanSpeed.format()),
        DashboardField("Fan Passenger", state.hvacPassengerSpeed.format()),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Cabin,
      title = "Seat Positions",
      accent = DemoGreenS300,
      fields = listOf(
        DashboardField("Front Left", state.seatOccupancy.frontLeft.toSeatStatus()),
        DashboardField("Front Right", state.seatOccupancy.frontRight.toSeatStatus()),
        DashboardField("Back Left", state.seatOccupancy.backLeft.toSeatStatus()),
        DashboardField("Back Right", state.seatOccupancy.backRight.toSeatStatus()),
        DashboardField("Ambient", state.ambientLight.name),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Access,
      title = "Doors",
      accent = DemoRedS100,
      fields = listOf(
        DashboardField("Front Left", state.doorState.frontLeft.toDoorStatus()),
        DashboardField("Front Right", state.doorState.frontRight.toDoorStatus()),
        DashboardField("Back Left", state.doorState.backLeft.toDoorStatus()),
        DashboardField("Back Right", state.doorState.backRight.toDoorStatus()),
        DashboardField("Trunk", state.trunkState.toDoorStatus()),
        DashboardField("Frunk", state.frunkState.toDoorStatus()),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Access,
      title = "Windows",
      accent = DemoNeoYellowS700,
      fields = listOf(
        DashboardField("Front Left", state.windowState.frontLeft.toCompactPercent()),
        DashboardField("Front Right", state.windowState.frontRight.toCompactPercent()),
        DashboardField("Back Left", state.windowState.backLeft.toCompactPercent()),
        DashboardField("Back Right", state.windowState.backRight.toCompactPercent()),
        DashboardField("Trunk Angle", state.trunkAngle.format()),
        DashboardField("Frunk Angle", state.frunkAngle.format()),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Power,
      title = "Battery & Charging",
      accent = DemoYellowS100,
      fields = listOf(
        DashboardField("Battery", state.battery.toCompactPercent()),
        DashboardField("Capacity", state.batteryCapacity.format(UnitEnergy.kilowattHours)),
        DashboardField("Charge Rate", state.evChargingRate.format()),
        DashboardField("Engine", state.engine.format()),
      )
    ),
    DashboardCard(
      category = DashboardCategory.Device,
      title = "Vehicle Identity",
      accent = DemoVioletS100,
      fields = listOf(
        DashboardField("Device ID", state.deviceInfo?.id ?: "N/A"),
        DashboardField("Brand", state.deviceInfo?.brand ?: "N/A"),
        DashboardField("Model", state.deviceInfo?.model ?: "N/A"),
      )
    ),
  )

  val firstIndexByCategory = remember(cards) {
    DashboardCategory.entries.associateWith { category ->
      cards.indexOfFirst { it.category == category }.coerceAtLeast(0)
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DemoBlackS500)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = DemoSpacing.BASE.dp, vertical = DemoSpacing.HALF.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DemoSpacing.NANO.dp, alignment = Alignment.CenterHorizontally),
      ) {
        Text(
          text = "NCar",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold,
          color = DemoWhiteS100,
        )
        Spacer(Modifier.width(DemoSpacing.NANO.dp))
        Image(painter = painterResource(R.drawable.neva_logo), contentDescription = null)
      }

      Spacer(Modifier.height(DemoSpacing.HALF.dp))

      LazyRow {
        items(DashboardCategory.entries.size) { index ->
          val category = DashboardCategory.entries[index]
          val selected = category == selectedCategory
          val container = if (selected) category.accent.copy(alpha = DemoOpacity.O4) else DemoBlackS700
          val content = if (selected) DemoWhiteS100 else DemoWhiteS500
          val border = if (selected) category.accent else DemoDarkBlueS300

          Surface(
            modifier = Modifier
              .padding(end = DemoSpacing.NANO.dp)
              .clickable {
                selectedCategory = category
                scope.launch {
                  gridState.animateScrollToItem(firstIndexByCategory.getValue(category))
                }
              },
            shape = RoundedCornerShape(DemoRadius.X4.dp),
            color = container,
            contentColor = content,
            border = BorderStroke(DemoBorder.BASE.dp, border),
          ) {
            Text(
              text = category.label,
              modifier = Modifier.padding(horizontal = DemoSpacing.HALF.dp, vertical = DemoSpacing.NANO.dp),
              style = MaterialTheme.typography.labelLarge,
              fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
          }
        }
      }

      Spacer(Modifier.height(DemoSpacing.HALF.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(DemoSpacing.HALF.dp),
        horizontalArrangement = Arrangement.spacedBy(DemoSpacing.HALF.dp),
      ) {
        items(cards, key = { "${it.category.name}-${it.title}" }) { card ->
          PropertyCard(card = card)
        }
      }
    }

    Surface(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(DemoSpacing.HALF.dp)
        .clickable { controlsVisible = !controlsVisible },
      shape = RoundedCornerShape(DemoRadius.X4.dp),
      color = DemoDarkBlueS500.copy(alpha = DemoOpacity.O6),
      border = BorderStroke(DemoBorder.BASE.dp, DemoNeoYellowS500.copy(alpha = DemoOpacity.O6)),
    ) {
      Text(
        text = if (controlsVisible) "Hide Demo Controls" else "Show Demo Controls",
        modifier = Modifier.padding(horizontal = DemoSpacing.HALF.dp, vertical = DemoSpacing.NANO.dp),
        color = DemoWhiteS100,
        style = MaterialTheme.typography.labelLarge,
      )
    }

    AnimatedVisibility(
      visible = controlsVisible,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 56.dp, end = DemoSpacing.HALF.dp)
    ) {
      DemoControlPanel(state = state, onClose = { controlsVisible = false })
    }
  }
}

@Composable
private fun DemoControlPanel(state: CarState, onClose: () -> Unit, modifier: Modifier = Modifier) {
  val scope = rememberCoroutineScope()
  val speedKmh = state.speed.convert(UnitSpeed.kilometersPerHour).value.coerceIn(0f, state.demoSpeedMaxKmh)
  var speedDraftKmh by remember { mutableFloatStateOf(speedKmh) }

  LaunchedEffect(speedKmh) {
    speedDraftKmh = speedKmh
  }

  Card(
    modifier = modifier
      .widthIn(min = 320.dp, max = 380.dp)
      .fillMaxHeight(0.9f),
    shape = RoundedCornerShape(DemoRadius.X2.dp),
    colors = CardDefaults.cardColors(containerColor = DemoBlackS900.copy(alpha = 0.92f)),
    border = BorderStroke(DemoBorder.BASE.dp, DemoNeoYellowS500.copy(alpha = DemoOpacity.O6)),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(DemoSpacing.HALF.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(DemoSpacing.HALF.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Demo Fallback Controls",
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.titleMedium,
          color = DemoNeoYellowS500,
          fontWeight = FontWeight.SemiBold,
        )
        Text(
          text = "Close",
          color = DemoWhiteS500,
          style = MaterialTheme.typography.labelLarge,
          modifier = Modifier.clickable(onClick = onClose)
        )
      }

      Text(
        text = "Controls are applied to fallback provider values when live vehicle data is unavailable.",
        style = MaterialTheme.typography.bodySmall,
        color = DemoWhiteS500,
      )

      ControlSection(title = "Speed") {
        Text(
          text = "${speedDraftKmh.roundToInt()} km/h",
          style = MaterialTheme.typography.bodyMedium,
          color = DemoWhiteS100,
          fontWeight = FontWeight.Medium,
        )
        Slider(
          value = speedDraftKmh,
          onValueChange = { value ->
            speedDraftKmh = value
            scope.launch { state.setDemoSpeedKmh(value) }
          },
          valueRange = 0f..state.demoSpeedMaxKmh,
          steps = 36,
        )
      }

      ControlSection(title = "Gear") {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(DemoSpacing.NANO.dp),
        ) {
          NCarGear.entries.forEach { gear ->
            FilterChip(
              selected = state.gear == gear,
              onClick = { scope.launch { state.setDemoGear(gear) } },
              label = { Text(gear.name) },
            )
          }
        }
      }

      ControlSection(title = "Doors") {
        DoorToggleRow(
          label = "Front Left",
          checked = state.doorState.frontLeft,
          onCheckedChange = { checked ->
            scope.launch { state.setDemoDoorState(state.doorState.copy(frontLeft = checked)) }
          }
        )
        DoorToggleRow(
          label = "Front Right",
          checked = state.doorState.frontRight,
          onCheckedChange = { checked ->
            scope.launch { state.setDemoDoorState(state.doorState.copy(frontRight = checked)) }
          }
        )
        DoorToggleRow(
          label = "Back Left",
          checked = state.doorState.backLeft,
          onCheckedChange = { checked ->
            scope.launch { state.setDemoDoorState(state.doorState.copy(backLeft = checked)) }
          }
        )
        DoorToggleRow(
          label = "Back Right",
          checked = state.doorState.backRight,
          onCheckedChange = { checked ->
            scope.launch { state.setDemoDoorState(state.doorState.copy(backRight = checked)) }
          }
        )
      }

      ControlSection(title = "Windows") {
        WindowSliderRow(
          label = "Front Left",
          percent = state.windowState.frontLeft.toPercentValue(),
          onPercentChange = { percent ->
            scope.launch { state.setDemoWindowState(state.windowState.copy(frontLeft = percent)) }
          }
        )
        WindowSliderRow(
          label = "Front Right",
          percent = state.windowState.frontRight.toPercentValue(),
          onPercentChange = { percent ->
            scope.launch { state.setDemoWindowState(state.windowState.copy(frontRight = percent)) }
          }
        )
        WindowSliderRow(
          label = "Back Left",
          percent = state.windowState.backLeft.toPercentValue(),
          onPercentChange = { percent ->
            scope.launch { state.setDemoWindowState(state.windowState.copy(backLeft = percent)) }
          }
        )
        WindowSliderRow(
          label = "Back Right",
          percent = state.windowState.backRight.toPercentValue(),
          onPercentChange = { percent ->
            scope.launch { state.setDemoWindowState(state.windowState.copy(backRight = percent)) }
          }
        )
      }
    }
  }
}

@Composable
private fun ControlSection(title: String, content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        color = DemoBlackS700.copy(alpha = DemoOpacity.O6),
        shape = RoundedCornerShape(DemoRadius.BASE.dp),
      )
      .padding(DemoSpacing.NANO.dp),
    verticalArrangement = Arrangement.spacedBy(DemoSpacing.NANO.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelLarge,
      color = DemoNeoYellowS700,
      fontWeight = FontWeight.SemiBold,
    )
    content()
  }
}

@Composable
private fun DoorToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.bodyMedium,
      color = DemoWhiteS500,
    )
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
    )
  }
}

@Composable
private fun WindowSliderRow(label: String, percent: Float, onPercentChange: (Float) -> Unit) {
  var draft by remember { mutableFloatStateOf(percent.coerceIn(0f, 100f)) }

  LaunchedEffect(percent) {
    draft = percent.coerceIn(0f, 100f)
  }

  Column(verticalArrangement = Arrangement.spacedBy(DemoSpacing.NANO.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = label,
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.bodyMedium,
        color = DemoWhiteS500,
      )
      Text(
        text = "${draft.roundToInt()}%",
        style = MaterialTheme.typography.bodyMedium,
        color = DemoWhiteS100,
      )
    }
    Slider(
      value = draft,
      onValueChange = {
        draft = it
        onPercentChange(it)
      },
      valueRange = 0f..100f,
      steps = 19,
    )
  }
}

@Composable
private fun PropertyCard(card: DashboardCard) {
  Card(
    modifier = Modifier.height(264.dp),
    shape = RoundedCornerShape(DemoRadius.X2.dp),
    colors = CardDefaults.cardColors(containerColor = DemoBlackS800),
    border = BorderStroke(DemoBorder.BASE.dp, card.accent.copy(alpha = DemoOpacity.O6))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(DemoSpacing.HALF.dp)
    ) {
      Text(
        text = card.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DemoNeoYellowS500,
      )

      Spacer(Modifier.height(DemoSpacing.NANO.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .background(
            color = DemoBlackS500.copy(alpha = DemoOpacity.O5),
            shape = RoundedCornerShape(DemoRadius.BASE.dp)
          )
          .padding(horizontal = DemoSpacing.NANO.dp, vertical = DemoSpacing.NANO.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(DemoSpacing.NANO.dp)
      ) {
        card.fields.forEach { field ->
          PropertyFieldRow(
            label = field.label,
            value = field.value,
            accent = card.accent
          )
        }
      }
    }
  }
}

@Composable
private fun PropertyFieldRow(label: String, value: String, accent: Color) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(DemoSpacing.NANO.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Box(
      modifier = Modifier
        .width(4.dp)
        .height(18.dp)
        .background(accent, RoundedCornerShape(DemoRadius.HALF.dp))
    )

    Text(
      text = label,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.labelMedium,
      color = DemoWhiteS500.copy(alpha = DemoOpacity.O6),
    )

    Text(
      text = value,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.End,
      color = DemoWhiteS100,
      fontWeight = FontWeight.Medium,
    )
  }
}

private fun Boolean.toOnOff(): String = if (this) "On" else "Off"

private fun Boolean.toSeatStatus(): String = if (this) "Occupied" else "Empty"

private fun Boolean.toDoorStatus(): String = if (this) "Open" else "Closed"

private fun Float.toCompactPercent(): String {
  val percent = if (this <= 1f) this * 100f else this
  return "${percent.roundToInt()}%"
}

private fun Float.toPercentValue(): Float = if (this <= 1f) this * 100f else this

