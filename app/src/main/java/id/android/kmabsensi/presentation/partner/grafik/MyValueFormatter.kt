package id.android.kmabsensi.presentation.partner.grafik

import com.github.mikephil.charting.formatter.ValueFormatter

class MyValueFormatter(private val values: List<String>) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return values[value.toInt()]
    }

}