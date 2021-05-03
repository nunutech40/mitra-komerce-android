package id.android.kmabsensi.presentation.sdm.dataalfa

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.ListAlphaParams
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kantor.OfficeViewModel
import id.android.kmabsensi.presentation.kantor.report.PresenceReportViewModel
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.getDateString
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_data_alfa.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DataAlfaActivity : BaseActivity() {

    private val presenceVM: PresenceReportViewModel by viewModel()
    private val officeVM: OfficeViewModel by viewModel()
    private val periode = listOf("Bulan ini", "Bulan lalu")
    private val offices = mutableListOf<Office>()
    private val officeNames = mutableListOf<String>()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var officeId = 0
    private var isThisMonth = true

//    private val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_alfa)
        setupToolbar("Data Alfa")

        officeVM.getOffices()

        officeVM.officeState.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    offices.addAll(state.data.data)
                    officeNames.add("Semua")
                    offices.forEach { officeNames.add(it.office_name) }
                    initSpinnerOffice()
                }

                is UiState.Error -> {

                }
            }
        })

        initSpinnerPeriode()
        initRv()

        btnTampilkanData.setOnClickListener {
            val cal = Calendar.getInstance()
            var dateStart = ""
            var dateEnd = ""
            if (isThisMonth) {
                dateStart = getDateString(cal.time).substring(0, 7) + "-01"
                dateEnd = getDateString(cal.time)
            } else {
                cal.add(Calendar.MONTH, -1)
                dateStart = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)+1}-01"
                dateEnd = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)+1}-${cal.getActualMaximum(Calendar.DATE)}"
            }

            presenceVM.getListAlpha(ListAlphaParams(office_id = officeId, start_date = dateStart, end_date = dateEnd))

        }

        presenceVM.alphaAttendances.observe(this, Observer {
            state ->
            when(state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    txtInitial.gone()
                    groupAdapter.clear()
                    if (state.data.data.isEmpty()) {
                        layout_empty.visible()
                    } else {
                        layoutListData.visible()
                        layout_empty.gone()
                    }
                    state.data.data.forEach {
                        groupAdapter.add(AlphaItem(it))
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private fun initRv() {
        rvDataAlfa.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(context, R.drawable.divider),
                    true
                )
            )
            adapter = groupAdapter
        }
    }

    private fun initSpinnerOffice() {
        ArrayAdapter(this, R.layout.spinner_item, officeNames).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerKantor.adapter = adapter
            spinnerKantor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    officeId = if (position == 0) {
                        0
                    } else {
                        offices[position - 1].id
                    }
                }

            }
        }
    }

    private fun initSpinnerPeriode() {
        ArrayAdapter(this, R.layout.spinner_item, periode).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPeriode.adapter = adapter
            spinnerPeriode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    isThisMonth = position == 0
                }

            }
        }
    }
}