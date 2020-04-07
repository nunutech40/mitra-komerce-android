package id.android.kmabsensi.presentation.partner.grafik.tab

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.ajalt.timberkt.d
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.partner.grafik.MyValueFormatter
import kotlinx.android.synthetic.main.fragment_grafik_by_city.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class GrafikByCityFragment : Fragment(), OnChartValueSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grafik_by_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSkillGraph()
    }

    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private fun setSkillGraph(){

        // chart.setHighlightEnabled(false);
        chart.setDrawBarShadow(false)

        chart.setDrawValueAboveBar(true)

        val description = Description()
        description.text = ""
        chart.description = description
        chart.legend.isEnabled = false
        chart.setPinchZoom(false)
        chart.setDrawValueAboveBar(false)

        val xl = chart.xAxis
        xl.position = XAxisPosition.BOTTOM
        xl.setDrawAxisLine(false)
        xl.setDrawGridLines(false)
        xl.granularity = 1f

        //Set label count to 5 as we are displaying 5 star rating
//        xl.labelCount = 5

        //Now add the labels to be added on the vertical axis
        val values = listOf("Samarinda", "Balikpapan", "Bontang", "Tarakan", "Banjarmasin")
        xl.valueFormatter = MyValueFormatter(values)

        val yl = chart.axisLeft
        yl.setDrawAxisLine(true)
        yl.setDrawGridLines(true)
        yl.axisMinimum = 0f // this replaces setStartAtZero(true)


        val yr = chart.axisRight
        yr.setDrawAxisLine(true)
        yr.setDrawGridLines(false)
        yr.axisMinimum = 0f

        chart.animateY(2500)

        // setting data
        setGraphData()

    }

    /**
     * Set the bar entries i.e. the percentage of users who rated the skill with
     * a certain number of stars.
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private fun setGraphData() {

        //Add a list of bar entries
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 27f))
        entries.add(BarEntry(1f, 45f))
        entries.add(BarEntry(2f, 65f))
        entries.add(BarEntry(3f, 77f))
        entries.add(BarEntry(4f, 93f))

        //Note : These entries can be replaced by real-time data, say, from an API

        val barDataSet = BarDataSet(entries, "Bar Data Set")

        //Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
            ContextCompat.getColor(requireContext(), R.color._3277FF),
            ContextCompat.getColor(requireContext(), R.color._3277FF),
            ContextCompat.getColor(requireContext(), R.color._3277FF),
            ContextCompat.getColor(requireContext(), R.color._3277FF),
            ContextCompat.getColor(requireContext(), R.color._3277FF)
        )

        chart.setDrawBarShadow(true)
        barDataSet.barShadowColor = Color.argb(40, 150, 150, 150)
        val data = BarData(barDataSet)

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.barWidth = 0.5f

        //Finally set the data and refresh the graph
        chart.data = data
        chart.invalidate()
        chart.setPinchZoom(false)
    }

    override fun onNothingSelected() {

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

}
