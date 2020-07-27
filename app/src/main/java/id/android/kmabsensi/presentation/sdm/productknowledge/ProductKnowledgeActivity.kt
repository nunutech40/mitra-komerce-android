package id.android.kmabsensi.presentation.sdm.productknowledge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.viewmodels.ProductKnowledgeViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_product_knowledge.*
import kotlinx.android.synthetic.main.layout_empty.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductKnowledgeActivity : BaseActivity() {

    private val productKnowledgeVM: ProductKnowledgeViewModel by viewModel()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var noPartner = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_knowledge)
        setupToolbar("Product Knowledge")

        noPartner = intent.getIntExtra(NO_PARTNER_KEY, 0)

        initRv()
        observeData()
        productKnowledgeVM.getListProductKnowledge(noPartner)
    }

    fun observeData(){
        productKnowledgeVM.productKnowledges.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {
                cardLayout.invis()
                progressBar.visible()
            }
            is UiState.Success -> {
                progressBar.gone()
                cardLayout.visible()
                if (state.data.data.isEmpty()) layout_empty.visible() else layout_empty.gone()
                state.data.data.forEach {
                    groupAdapter.add(ProductKΩnowledgeItem(it){
                        startActivity<DetailProductKnowledgeActivity>(PRODUCT_KNOWLEDGE_KEY to it)
                    })
                }
            }
            is UiState.Error -> {
                progressBar.gone()
            }
        } })
    }

    fun initRv(){
        rvProductKnowledge.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(
                ContextCompat.getDrawable(context, R.drawable.divider)
            ))
            adapter = groupAdapter
        }
    }
}