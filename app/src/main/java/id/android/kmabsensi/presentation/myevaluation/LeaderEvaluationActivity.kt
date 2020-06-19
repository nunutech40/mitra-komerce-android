package id.android.kmabsensi.presentation.myevaluation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.EVALUATION_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_leader_evaluation.*
import kotlinx.android.synthetic.main.activity_my_evaluation.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LeaderEvaluationActivity : BaseActivity() {

    private val vm: KelolaDataSdmViewModel by viewModel()
    private val evaluationViewModel : EvaluationViewModel by viewModel()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val leaders = mutableListOf<User>()
    private var leaderId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_evaluation)
        setupToolbar("Evaluasi Leader")

        vm.getUserManagement(2)

        vm.userManagementData.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    leaders.addAll(it.data.data.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })

                    val leaderNames = mutableListOf<String>()
                    leaders.forEach { leaderNames.add(it.full_name) }
                    ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        leaderNames
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
                                    leaderId = leaders[position].id
                                    evaluationViewModel.getLeaderEvaluation(leaderId)
                                }

                            }
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                    Timber.e { it.throwable.message.toString() }
                }
            }
        })

        evaluationViewModel.leaderEvaluation.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                progressBar.visible()
                groupAdapter.clear()
            }
            is UiState.Success -> {
                progressBar.gone()
                if (state.data.evaluations.isEmpty()) layout_empty.visible() else layout_empty.gone()
                state.data.evaluations.forEach {
                    groupAdapter.add(MyEvaluationItem(it){
                        startActivity<EvaluationDetailActivity>(EVALUATION_KEY to it)
                    })
                }
            }
            is UiState.Error -> {
                progressBar.gone()
            }
        } })
    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(this)
        rvEvaluation.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }
}