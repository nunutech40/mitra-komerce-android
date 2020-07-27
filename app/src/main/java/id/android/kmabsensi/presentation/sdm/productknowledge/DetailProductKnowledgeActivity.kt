package id.android.kmabsensi.presentation.sdm.productknowledge

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.ProductKnowledge
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.sdm.productknowledge.item.FileItem
import id.android.kmabsensi.presentation.sdm.productknowledge.item.LinkItem
import id.android.kmabsensi.utils.PRODUCT_KNOWLEDGE_KEY
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import kotlinx.android.synthetic.main.activity_detail_product_knowledge.*


class DetailProductKnowledgeActivity : BaseActivity() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val groupAdapterFile = GroupAdapter<GroupieViewHolder>()

    private var data: ProductKnowledge? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product_knowledge)
        setupToolbar("Detail Product Knowledge")
        data = intent.getParcelableExtra(PRODUCT_KNOWLEDGE_KEY)

        initRv()

        data?.let {
            edtNamaProduct.setText(it.title)
            edtDeskripsi.setText(it.description)
            edtFaq.setText(it.faq)

            it.attachmentLinks.forEach {
                groupAdapter.add(LinkItem(it){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.attachmentLink)))
                })
            }

            it.attachmentFiles.forEach {
                groupAdapterFile.add(FileItem(it){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.attachmentUrl)))
                })
            }
        }
    }

    fun initRv(){
        rvLink.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecorator(
                ContextCompat.getDrawable(context, R.drawable.divider)
            )
            )
            adapter = groupAdapter
        }

        rvFile.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecorator(
                ContextCompat.getDrawable(context, R.drawable.divider)
            ))
            adapter = groupAdapterFile
        }
    }
}