package id.android.kmabsensi.presentation.invoice

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailItem
import id.android.kmabsensi.presentation.invoice.item.OnInvoiceDetailListener
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_add_invoice.*
import kotlinx.android.synthetic.main.activity_manage_invoice_detail.*
import kotlinx.android.synthetic.main.activity_manage_invoice_detail.rvInvoiceDetail
import kotlinx.android.synthetic.main.activity_manage_invoice_detail.textTotalTagihan
import kotlinx.android.synthetic.main.item_row_invoice_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageInvoiceDetailActivity : BaseActivity() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    private val invoiceDetailItems = mutableListOf<InvoiceDetailItem>()
    private val deleteItemSelected = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_invoice_detail)

        setupToolbar("Tagihan", isWhiteBackground = true)
        initRv()

        btnAdd.setOnClickListener {
            showCrudDialog()
        }

        btnOke.setOnClickListener {
            finish()
        }

        btnDelete.setOnClickListener {
            invoiceDetailItems.forEach {
                it.showDeleteSession()
                initDeleteViewSession()
            }
        }

        observeInvoiceItems()
    }

    private fun initDeleteViewSession(){
        btnAdd.gone()
        btnDelete.gone()
        layoutTotalTagian.gone()
        layoutItemDelete.visible()
        btnPilihSemua.visible()

        btnDeleteItem.setOnClickListener {
            if (deleteItemSelected.isNotEmpty()){
                deleteItemSelected.forEach {
                    InvoiceDetailData.removeItem(it)
                }
                layoutTotalTagian.visible()
                layoutItemDelete.gone()
                Handler().postDelayed({
                    invoiceDetailItems.forEach {
                        it.cancelDeleteSession()
                    }
                }, 300)

            } else {
                invoiceDetailItems.forEach {
                    it.cancelDeleteSession()
                }
                layoutTotalTagian.visible()
                layoutItemDelete.gone()
                btnAdd.visible()
                btnDelete.visible()
                btnPilihSemua.gone()
            }
        }

        btnPilihSemua.setOnClickListener {
            invoiceDetailItems.forEach {
                it.viewHolder.itemView.checkBoxDelete.isChecked = true
            }
        }
    }

    private fun observeInvoiceItems() {
        InvoiceDetailData.invoiceItemsData.observe(this, Observer {
            groupAdapter.clear()
            invoiceDetailItems.clear()
            it.forEach {
                val item = InvoiceDetailItem(it, object: OnInvoiceDetailListener{
                    override fun onEditClicked(invoiceDetail: InvoiceDetail, position: Int) {
                        showCrudDialog(invoiceDetail.itemName, invoiceDetail.itemPrice, position)
                    }

                    override fun onCheckBoxClicked(invoiceDetail: InvoiceDetail, position: Int) {
                        d { deleteItemSelected.toString() }
                        if (invoiceDetail.selected){
                            deleteItemSelected.add(position)
                        } else {
                            deleteItemSelected.removeAt(deleteItemSelected.indexOfFirst { it == position})
                        }
                        textDeleteSelected.text = "${deleteItemSelected.size} item terpilih"
                        d { invoiceDetail.toString() }
                        d { position.toString() }
                        d { deleteItemSelected.toString() }

                        if (deleteItemSelected.isEmpty()) btnDeleteItem.text = "BATAL" else btnDeleteItem.text = "HAPUS ITEM"
                    }
                })
                invoiceDetailItems.add(item)
                groupAdapter.add(item)
            }
            textTotalTagihan.text = convertRp(it.sumBy { it.itemPrice }.toDouble())

            if (it.isNotEmpty()) btnDelete.visible() else btnDelete.gone()

        })
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvInvoiceDetail.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this.context, linearLayoutManager.orientation))
            adapter = groupAdapter
        }
    }


    private fun showCrudDialog(itemName: String? = null, itemPrice: Int? = null, position: Int = 0) {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_crud_detail_tagihan, noVerticalPadding = true)
        val customView = dialog.getCustomView()
        val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
        val edtItemName = customView.findViewById<EditText>(R.id.edtItemName)
        val edtItemPrice = customView.findViewById<EditText>(R.id.edtItemPrice)
        val btnOke = customView.findViewById<Button>(R.id.btnOke)

        itemName?.let { edtItemName.setText(it) }
        itemPrice?.let { edtItemPrice.setText(it.toString()) }

        btnOke.setOnClickListener {
            if (itemName == null){
                InvoiceDetailData.addInvoiceItem(
                    InvoiceDetail(
                        edtItemName.text.toString(),
                        edtItemPrice.text.toString().toInt()
                    )
                )
            } else {
                InvoiceDetailData.updateInvoiceItem(
                    InvoiceDetail(
                        edtItemName.text.toString(),
                        edtItemPrice.text.toString().toInt()
                    ),
                    position
                )
            }

            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
