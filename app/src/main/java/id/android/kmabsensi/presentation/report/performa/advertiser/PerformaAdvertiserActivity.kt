package id.android.kmabsensi.presentation.report.performa.advertiser

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.FilterSdmReportParams
import id.android.kmabsensi.data.remote.response.AdvertiserReport
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.DateHelper
import id.android.kmabsensi.utils.NO_PARTNER_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.getDateString
import kotlinx.android.synthetic.main.activity_performa_advertiser.*
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class PerformaPeriode {
    TODAY, YESTERDAY, LAST7DAYS, THISMONTH, LASTMONTH
}

class PerformaAdvertiserActivity : BaseActivity() {

    private val sdmVM: SdmViewModel by viewModel()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var noPartner: String = "0"

    private var sdm = mutableListOf<User>()
    private var sdmIdSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performa_advertiser)
        setupToolbar("Performa")
        initRv()

        noPartner = intent.getStringExtra(NO_PARTNER_KEY) ?: "0"

        PerformaAdvertiserReportData.generateData()
        PerformaAdvertiserReportData.getData().forEach {
            groupAdapter.add(PerformaReportAdvertiserItem(it))
        }

        observeData()
        sdmVM.getSdm(positionId = 2, noPartner = noPartner.toInt())
    }

    private fun initSpinner(){
        val sdmNames = mutableListOf<String>()
        sdmNames.add("Semua")
        sdm.forEach {
            sdmNames.add(it.full_name)
        }

        ArrayAdapter(this, R.layout.spinner_item, sdmNames).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSdm.adapter = adapter

            spinnerSdm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    sdmIdSelected = if (p2 == 0) 0 else sdm[p2-1].id
                    fetchPerforma()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        }
    }

    private fun fetchPerforma(){
        getPerformances(
            getDateString(DateHelper.getTodayDate()),
            getDateString(DateHelper.getTodayDate()),
            PerformaPeriode.TODAY
        )
        getPerformances(
            getDateString(DateHelper.getYesterdayDate()),
            getDateString(DateHelper.getYesterdayDate()),
            PerformaPeriode.YESTERDAY
        )
        getPerformances(
            getDateString(DateHelper.get7DaysAgoDate()),
            getDateString(DateHelper.getTodayDate()),
            PerformaPeriode.LAST7DAYS
        )
        getPerformances(
            getDateString(
                DateHelper.getFirstDateOfMonth(
                    DateHelper.getCurrentMonth(),
                    DateHelper.getCurrentYear()
                )
            ),
            getDateString(
                DateHelper.getLastDateOfMonth(
                    DateHelper.getCurrentMonth(),
                    DateHelper.getCurrentYear()
                )
            ),
            PerformaPeriode.THISMONTH
        )
        getPerformances(
            getDateString(
                DateHelper.getFirstDateOfMonth(
                    DateHelper.getLastMonth(),
                    DateHelper.getCurrentYear()
                )
            ),
            getDateString(
                DateHelper.getLastDateOfMonth(
                    DateHelper.getLastMonth(),
                    DateHelper.getCurrentYear()
                )
            ),
            PerformaPeriode.LASTMONTH
        )
    }

    private fun getPerformances(startDate: String, endDate: String, periode: PerformaPeriode) {
        sdmVM.filterAdvertiserReportSummary(
            FilterSdmReportParams(
                user_id = sdmIdSelected,
                user_management_id = sdmVM.getUserData().id,
                no_partner = noPartner,
                start_date = startDate,
                end_date = endDate
            ),
            periode
        )
    }

    private fun observeData(){
        sdmVM.sdm.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    sdm.addAll(state.data.data)
                    initSpinner()
                }
                is UiState.Error -> {

                }
            }
        })

        sdmVM.advertiserReportSummaryToday.observe(this, Observer {
            if (it.data.isNotEmpty()){
                PerformaAdvertiserReportData.setToday(it.data[0])
                populateData()
            }
        })
        sdmVM.advertiserReportSummaryYesteday.observe(this, Observer {
            if (it.data.isNotEmpty()){
                PerformaAdvertiserReportData.setYesterday(it.data[0])
                populateData()
            }
        })
        sdmVM.advertiserReportSummaryLast7Days.observe(this, Observer {
            if (it.data.isNotEmpty()){
                PerformaAdvertiserReportData.setLast7days(it.data[0])
                populateData()
            }
        })
        sdmVM.advertiserReportSummaryThisMonth.observe(this, Observer {
            if (it.data.isNotEmpty()){
                PerformaAdvertiserReportData.setThisMonth(it.data[0])
                populateData()
            }
        })
        sdmVM.advertiserReportSummaryLastMonth.observe(this, Observer {
            if (it.data.isNotEmpty()){
                PerformaAdvertiserReportData.setLastMonth(it.data[0])
                populateData()
            }
        })
    }

    private fun populateData(){
        groupAdapter.clear()
        PerformaAdvertiserReportData.getData().forEach {
            groupAdapter.add(PerformaReportAdvertiserItem(it))
        }
    }

    fun initRv(){
        rvPerforma.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}