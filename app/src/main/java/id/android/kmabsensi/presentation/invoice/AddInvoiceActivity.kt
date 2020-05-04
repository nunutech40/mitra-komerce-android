package id.android.kmabsensi.presentation.invoice

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetail
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailBasicItem
import id.android.kmabsensi.presentation.invoice.item.InvoiceDetailItem
import id.android.kmabsensi.presentation.invoice.item.OnInvoiceDetailListener
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.utils.SIMPLE_PARTNER_DATA_KEY
import id.android.kmabsensi.utils.convertRp
import id.android.kmabsensi.utils.divider.DividerItemDecorator
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_add_invoice.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddInvoiceActivity : BaseActivity() {

    private val invoiceVM : InvoiceViewModel by viewModel()
    private val groupAdapter = GroupAdapter<ViewHolder>()

    private var listInvoiceDetail = mutableListOf<InvoiceDetail>()
    private var partnerSelected : SimplePartner? = null
    private val PICK_PARTNER_RC = 112

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_invoice)
        setupToolbar("Invoice Admin")
        initRv()

        switchStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                switchLabel.text = "Free"
                layoutDetailTagihan.gone()
                rvInvoiceDetail.gone()
                textTotalTagihan.text = "Rp 0"
            } else {
                switchLabel.text = "Not Free"
                layoutDetailTagihan.visible()
                rvInvoiceDetail.visible()
                textTotalTagihan.text = convertRp(listInvoiceDetail.sumBy { it.itemPrice }.toDouble())
            }
        }

        InvoiceDetailData.invoiceItemsData.observe(this, Observer {
            if (it.isNotEmpty()){
                listInvoiceDetail.clear()
                groupAdapter.clear()
                listInvoiceDetail.addAll(it)
                it.forEach {
                    groupAdapter.add(InvoiceDetailBasicItem(it))
                }
                textTotalTagihan.text = convertRp(it.sumBy { it.itemPrice }.toDouble())
            }
        })

        edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(
                PICK_PARTNER_RC
            )
        }

        btnUbahTagihan.setOnClickListener {
            startActivity<ManageInvoiceDetailActivity>()
        }

        //spinner month
        ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMonth.adapter = adapter

                spinnerMonth.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                        }

                    }
            }

        //spinner year
        ArrayAdapter(this, R.layout.spinner_item, getYearData())
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerYear.adapter = adapter

                spinnerYear.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                        }

                    }
            }
    }

    fun initRv(){
        rvInvoiceDetail.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this.context, R.drawable.divider), false))
            adapter = groupAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK){
            partnerSelected = data?.getParcelableExtra<SimplePartner>(SIMPLE_PARTNER_DATA_KEY)
            edtPilihPartner.setText(partnerSelected?.fullName)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getYearData(): List<String>{
        var year = calendar.get(Calendar.YEAR) - 1
        val years = mutableListOf<String>()
        years.add("Pilih Tahun")
        for (i in 1..6){
            years.add(year.toString())
            year += 1
        }
        return years
    }
}
