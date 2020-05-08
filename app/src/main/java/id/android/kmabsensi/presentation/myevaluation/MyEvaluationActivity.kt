package id.android.kmabsensi.presentation.myevaluation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_my_evaluation.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyEvaluationActivity : BaseActivity() {

    private val evaluationViewModel : EvaluationViewModel by viewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_evaluation)
        setupToolbar("Evaluasi Saya")
        initRv()
        evaluationViewModel.getMyEvaluation()
        observeMyEvaluation()

        swipeRefresh.setOnRefreshListener {
            evaluationViewModel.getMyEvaluation()
        }
    }

    private fun observeMyEvaluation(){
        evaluationViewModel.myEvaluations.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                swipeRefresh.isRefreshing = true
            }
            is UiState.Success -> {
                swipeRefresh.isRefreshing = false
                if (state.data.evaluations.isEmpty()) layout_empty.visible() else layout_empty.gone()
                state.data.evaluations.forEach {
                    groupAdapter.add(MyEvaluationItem(it))
                }
            }
            is UiState.Error -> {
                swipeRefresh.isRefreshing = false
            }
        } })
    }

    fun initRv(){
        val linearLayoutManager = LinearLayoutManager(this)
        rvMyEvaluation.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }
}
