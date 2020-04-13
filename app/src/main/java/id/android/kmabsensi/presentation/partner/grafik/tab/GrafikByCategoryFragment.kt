package id.android.kmabsensi.presentation.partner.grafik.tab

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Dashboard
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.PartnerCategoryStatistic
import id.android.kmabsensi.data.remote.response.PartnerProvinceStatistic
import id.android.kmabsensi.presentation.partner.grafik.MyValueFormatter
import id.android.kmabsensi.utils.DASHBOARD_DATA_KEY
import kotlinx.android.synthetic.main.fragment_grafik_by_category.*

/**
 * A simple [Fragment] subclass.
 */
class GrafikByCategoryFragment : Fragment(), OnChartValueSelectedListener {

    companion object {
        fun newInstance(dashboard: Dashboard?) : GrafikByCategoryFragment {
            val fragment = GrafikByCategoryFragment()
            val bundle = bundleOf(DASHBOARD_DATA_KEY to dashboard)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grafik_by_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dashboard : Dashboard? = arguments?.getParcelable(DASHBOARD_DATA_KEY)
        dashboard?.let {
            setSkillGraph(it.partner_category_statistic)
        }

    }


    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private fun setSkillGraph(data: List<PartnerCategoryStatistic>){

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
        xl.position = XAxis.XAxisPosition.BOTTOM
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
        setGraphData(data)

    }

    /**
     * Set the bar entries i.e. the percentage of users who rated the skill with
     * a certain number of stars.
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private fun setGraphData(data: List<PartnerCategoryStatistic>) {

        //Add a list of bar entries
        val entries = ArrayList<BarEntry>()
        var x = 0f
        data.forEach {
            entries.add(BarEntry(x, it.total.toFloat()))
            x += 1
        }

//        entries.add(BarEntry(1f, 45f))
//        entries.add(BarEntry(2f, 65f))
//        entries.add(BarEntry(3f, 77f))
//        entries.add(BarEntry(4f, 93f))

        //Note : These entries can be replaced by real-time data, say, from an API

        val barDataSet = BarDataSet(entries, "Bar Data Set")

        //Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
            ContextCompat.getColor(requireContext(), R.color._46B77A)
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
