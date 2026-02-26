package com.nevaxr.foundation.car.demo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nevaxr.foundation.car.ClosedMeasurableRange
import com.nevaxr.foundation.car.Measurable
import com.nevaxr.foundation.car.MeasurementUnit
import com.nevaxr.foundation.car.SensorUnitConvertible
import com.nevaxr.foundation.car.normalized

@Composable
fun <BaseUnit: MeasurementUnit> SensorDisplay(value: Measurable<BaseUnit>, modifier: Modifier = Modifier) {
    val unitDisplayName = stringResource(value.unit.symbolRes)
    Text("${value.value} $unitDisplayName", modifier = modifier)
}

@Composable
fun <BaseUnit> SensorDisplayNormalized(value: Measurable<BaseUnit>, range: ClosedMeasurableRange<BaseUnit>, modifier: Modifier = Modifier) where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    Row(modifier) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            progress = { value.normalized(range) },
        )

        Text("%.2f %".format(value.normalized(range) * 100f))
    }
}