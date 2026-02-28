package com.nevaxr.foundation.car.demo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nevaxr.foundation.car.Measurement
import com.nevaxr.foundation.car.MeasurementUnit
import com.nevaxr.foundation.car.MeasurementUnitRange
import com.nevaxr.foundation.car.Dimension
import com.nevaxr.foundation.car.normalized
import com.nevaxr.foundation.car.convert

@Composable
fun <BaseUnit: MeasurementUnit> SensorDisplay(value: Measurement<BaseUnit>, modifier: Modifier = Modifier) {
    val unitDisplayName = stringResource(value.unit.symbolRes)
    Text("${value.value} $unitDisplayName", modifier = modifier)
}

@Composable
fun <BaseUnit> SensorDisplayNormalized(value: Measurement<BaseUnit>, range: MeasurementUnitRange<BaseUnit>, modifier: Modifier = Modifier) where BaseUnit : Dimension<BaseUnit> {
    Row(modifier) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            progress = { value.normalized(range) },
        )

        Text("%.2f %".format(value.normalized(range) * 100f))
    }
}
