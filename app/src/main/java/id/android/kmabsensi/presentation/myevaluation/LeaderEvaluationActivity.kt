package id.android.kmabsensi.presentation.myevaluation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_invoice_report.*
import kotlinx.android.synthetic.main.activity_leader_evaluation.*
import kotlinx.android.synthetic.main.activity_leader_evaluation.textLeaderName
import kotlinx.android.synthetic.main.activity_leader_evaluation.textPeriod
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class LeaderEvaluationActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by viewModel()
    private val evaluationViewModel: EvaluationViewModel by viewModel()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val leaders = mutableListOf<User>()
    private var leaderSelectedId = 0

    private var monthFromSelected = 0
    private var yearFromSelected = 0
    private var monthToSelected = 0
    private var yearToSelected = 0
    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()
    private var startPeriod = ""
    private var endPeriod = ""
    private var monthFromSelectedLabel = ""
    private var monthToSelectedLabel = ""


    private lateinit var dialogFilter: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_evaluation)
        setupToolbar("Evaluasi Leader", isFilterVisible = true)

        initRv()

        monthFromSelected = startCalendar.get(Calendar.MONTH)+1
        monthToSelected = startCalendar.get(Calendar.MONTH)+1
        yearFromSelected = startCalendar.get(Calendar.YEAR)
        yearToSelected = endCalendar.get(Calendar.YEAR)

        startPeriod = "$yearFromSelected-$monthFromSelected-01"
        endPeriod = "$yearToSelected-$monthToSelected-01"

        vm.getUserManagement(2)
        evaluationViewModel.getLeaderEvaluation(startPeriod, endPeriod)

        vm.userManagementData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
//                    showDialog()
                }
                is UiState.Success -> {
//                    hideDialog()
                    leaders.addAll(it.data.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })

                }
                is UiState.Error -> {
//                    hideDialog()
                    Timber.e { it.throwable.message.toString() }
                }
            }
        })

        evaluationViewModel.leaderEvaluation.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visible()
                    groupAdapter.clear()
                }
                is UiState.Success -> {
                    progressBar.gone()

                    monthFromSelectedLabel = "${resources.getStringArray(R.array.month_array).toList()[monthFromSelected]} $yearFromSelected"
                    monthToSelectedLabel = "${resources.getStringArray(R.array.month_array).toList()[monthToSelected]} $yearToSelected"

                    textLeaderName.text = if (leaderSelectedId == 0) "Semua" else leaders.first { it.id == leaderSelectedId }.full_name
                    textPeriod.text = "$monthFromSelectedLabel - $monthToSelectedLabel"

                    if (state.data.evaluations.isEmpty()) layout_empty.visible() else layout_empty.gone()
                    state.data.evaluations.forEach {
                        groupAdapter.add(MyEvaluationItem(it) {
                            startActivity<EvaluationDetailActivity>(EVALUATION_KEY to it)
                        })
                    }
                }
                is UiState.Error -> {
                    progressBar.gone()
                }
            }
        })

        btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvEvaluation.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }

    private fun showFilterDialog() {
        if (!::dialogFilter.isInitialized){
            dialogFilter = MaterialDialog(this)
                .customView(R.layout.dialog_invoice_report_filter_layout, noVerticalPadding = true)
            val customView = dialogFilter.getCustomView()
            val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
            val spinnerBulanDari = customView.findViewById<Spinner>(R.id.spinnerBulanDari)
            val spinnerTahunDari = customView.findViewById<Spinner>(R.id.spinnerTahunDari)
            val spinnerBulanKe = customView.findViewById<Spinner>(R.id.spinnerBulanKe)
            val spinnerTahunKe = customView.findViewById<Spinner>(R.id.spinnerTahunKe)
            val spinnerLeader = customView.findViewById<Spinner>(R.id.spinnerLeader)
            val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

            initSpinnerFilter(spinnerLeader, spinnerBulanDari, spinnerTahunDari, spinnerBulanKe, spinnerTahunKe)

            btnClose.setOnClickListener {
                dialogFilter?.dismiss()
            }

            buttonFilter.setOnClickListener {
                dialogFilter?.dismiss()
                val startMonth = if (monthFromSelected < 10) "0$monthFromSelected" else "$monthFromSelected"
                val endMonth = if (monthToSelected < 10) "0$monthToSelected" else "$monthToSelected"
                startPeriod = "$yearFromSelected-$startMonth-01"
                endPeriod = "$yearToSelected-$endMonth-01"

                evaluationViewModel.getLeaderEvaluation(startPeriod, endPeriod, leaderSelectedId)
            }
        }

        dialogFilter.show()
    }

    private fun initSpinnerFilter(
        spinnerLeader: Spinner, spinnerBulanDari: Spinner, spinnerTahunDari: Spinner, spinnerBulanKe: Spinner,
        spinnerTahunKe: Spinner
    ) {
        //spinnerLeader
        val userManagementNames = mutableListOf<String>()
        userManagementNames.add("Semua")
        leaders.forEach { userManagementNames.add(it.full_name) }
        ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            userManagementNames
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerLeader.adapter = adapter

            spinnerLeader.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        leaderSelectedId = if (position == 0){
                            0
                        } else {
                            leaders[position-1].id
                        }
                    }

                }
        }

        //spinner bulan dari
        ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerBulanDari.adapter = adapter

                spinnerBulanDari.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) monthFromSelected = position
                        }
                    }
                spinnerBulanDari.setSelection(monthFromSelected)
            }

        //spinner bulan ke
        ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerBulanKe.adapter = adapter

                spinnerBulanKe.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) monthToSelected = position
                        }

                    }

                spinnerBulanKe.setSelection(monthToSelected)
            }

        //spinner tahun dari
        ArrayAdapter(this, R.layout.spinner_item, getYearData())
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTahunDari.adapter = adapter

                spinnerTahunDari.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) yearFromSelected = spinnerTahunDari.selectedItem.toString().toInt()

                        }

                    }

                spinnerTahunDari.setSelection(getYearData().indexOfFirst { it == yearFromSelected.toString() })
            }

        //spinner tahun ke
        ArrayAdapter(this, R.layout.spinner_item, getYearData())
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTahunKe.adapter = adapter

                spinnerTahunKe.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) yearToSelected = spinnerTahunKe.selectedItem.toString().toInt()

                        }

                    }

                spinnerTahunKe.setSelection(getYearData().indexOfFirst { it == yearToSelected.toString() })
            }
    }
}