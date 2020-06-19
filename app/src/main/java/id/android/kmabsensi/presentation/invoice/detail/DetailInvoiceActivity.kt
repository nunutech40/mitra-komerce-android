package id.android.kmabsensi.presentation.invoice.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.invoice.InvoiceDetail
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.InvoiceViewModel
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailBasic
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailBasicItem
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailGajiItem
import id.android.kmabsensi.presentation.invoice.payment.BuktiPembayaranActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_detail_invoice.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailInvoiceActivity : BaseActivity() {

    private val invoiceVM: InvoiceViewModel by viewModel()
    private val groupAdaper = GroupAdapter<GroupieViewHolder>()

    private var invoiceId: Int = 0
    private var invoiceType: Int = 0

    private var updateMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_invoice)

        invoiceId = intent.getIntExtra(INVOICE_ID_KEY, 0)
        invoiceType = intent.getIntExtra(INVOICE_TYPE_KEY, 0)

        setupToolbar(if (invoiceType == 1) getString(R.string.label_detail_invoice_admin) else getString(
                    R.string.label_detail_invoice_gaji))

        d { "invoice id : ${invoiceId}" }

        initRv()
        observeInvoiceDetail()
        if (invoiceType == 1){
            invoiceVM.getInvoiceAdminDetail(invoiceId)
        } else {
            invoiceVM.getInvoiceGajiDetail(invoiceId)
        }
    }

    private fun observeInvoiceDetail() {
        invoiceVM.invoiceDetail.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        showInvoiceDetailData(state.data.invoice)
                        if (updateMessage.isNotEmpty()) createAlertSuccess(this, updateMessage)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    private fun showInvoiceDetailData(data: InvoiceDetail) {
        scrollView.visible()
        data.apply {
            textInvoiceNumber.text = invoiceKmId
            textDate.text = getDateWithDay(parseStringDate(createdAt))
            textInvoiceCreator.text = userRequester.fullName
            textNoPartner.text = userTo.noPartner
            textPartner.text = userTo.fullName
            txtJudul.text = title
            textTotal.text = convertRp(amount.toDouble())

            invoiceDetailAdmin?.map { InvoiceDetailBasic(it.item, it.total.toInt(), it.description) }?.forEach {
                groupAdaper.add(InvoiceDetailBasicItem(it))
            }

            invoiceDetailGaji?.forEach {
                groupAdaper.add(InvoiceDetailGajiItem(this@DetailInvoiceActivity, it))
            }

            when (status) {
                1 -> {
                    textInvoiceStatus.text = getString(R.string.label_open)
                    textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_open)
                    textInvoiceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@DetailInvoiceActivity,
                            R.color._64C3F9
                        )
                    )
                    layoutButton.visible()
                    layoutExpired.visible()
                }
                2 -> {
                    textInvoiceStatus.text = getString(R.string.label_completed)
                    textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_complete)
                    textInvoiceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@DetailInvoiceActivity,
                            R.color._5AB04F
                        )
                    )
                }
                3 -> {
                    textInvoiceStatus.text = getString(R.string.label_expired)
                    textInvoiceStatus.setBackgroundResource(R.drawable.bg_status_invoice_cancel)
                    textInvoiceStatus.setTextColor(
                        ContextCompat.getColor(
                            this@DetailInvoiceActivity,
                            R.color._DE4D4E
                        )
                    )
                }
            }

            if(this@DetailInvoiceActivity.invoiceType == 1){
                expiryDate?.let { textDueDate.text = getDateWithDay(parseStringDate(it, "yyyy-MM-dd HH:mm:ss"))  }
                btnLihatBuktiPembayararn.gone()
            } else {
                layoutExpired.gone()
            }

            if (status != 1) {
                layoutButton.gone()
                layoutExpired.gone()
            }

            btnComplete.setOnClickListener {
                showDialogAction(false, getString(R.string.txt_complete_invoice_confirmation))
            }

            btnCancel.setOnClickListener {
                showDialogAction(true, getString(R.string.txt_cancel_invoice_confirmation))
            }

            btnLihatBuktiPembayararn.setOnClickListener {
                startActivity<BuktiPembayaranActivity>(INVOICE_DATA_KEY to this)
            }
        }

        var isExpanded = false
        toggleExpand.setOnClickListener {
            if (!isExpanded) {
                isExpanded = true
                toggleExpand.setImageResource(R.drawable.ic_keyboard_arrow_up)
                rvInvoice.visible()
            } else {
                isExpanded = false
                toggleExpand.setImageResource(R.drawable.ic_keyboard_arrow_down)
                rvInvoice.gone()
            }

        }

    }

    private fun updateInvoiceStatus(status: Int) {
        invoiceVM.updateInvoice(invoiceId, status)
        invoiceVM.updateInvoiceResponse.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    showDialog()
                }
                is UiState.Success -> {
                    hideDialog()
                    if (state.data.status) {
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Warning!", state.data.message, duration = 3000)
                    }
                }
                is UiState.Error -> {
                    hideDialog()
                }
            }
        })
    }

    fun initRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvInvoice.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            adapter = groupAdaper
        }
    }

    private fun showDialogAction(isCancel: Boolean, message: String){
        MaterialDialog(this).show {
            title(text ="Konfirmasi")
            message(text = message)
            positiveButton(text = "Ya"){
                if (isCancel) updateInvoiceStatus(3) else updateInvoiceStatus(2)
                it.dismiss()
            }
            negativeButton(text = "Batal") {
                it.dismiss()
            }
        }
    }
}
