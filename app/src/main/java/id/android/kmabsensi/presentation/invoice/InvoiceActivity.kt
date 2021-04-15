package id.android.kmabsensi.presentation.invoice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.tabs.TabLayoutMediator
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.SimplePartner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.invoice.searchspinner.*
import id.android.kmabsensi.presentation.partner.PartnerViewModel
import id.android.kmabsensi.presentation.sdm.KelolaDataSdmViewModel
import id.android.kmabsensi.utils.UiState
import kotlinx.android.synthetic.main.activity_invoice.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvoiceActivity : BaseActivity() {

    private val sdmVM: KelolaDataSdmViewModel by viewModel()
    private val vm: PartnerViewModel by viewModel()

    // tab titles
    private val tabTitles = arrayOf("Aktif", "Riwayat")
    private var invoiceType = 1
    private var partnerFilterSelectedId = 0
    private val partners = mutableListOf<SimplePartner>()
    private var leaderIdSelected = 0
    private var leaders = mutableListOf<User>()

    private lateinit var activeInvoiceFragment: InvoiceActiveFragment
    private lateinit var historyInvoiceFragment: HistoryInvoiceFragment

    private lateinit var dialogFilter: MaterialDialog
    private var editTextLeader: EditText? = null
    private var editTextPartner: EditText? = null
    private var userSpinnerLeader = ArrayList<UserSpinner>()
    private var userSpinnerPartner = ArrayList<UserSpinner>()
    private val sharedPref by lazy { PreferencesHelper(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        setupToolbar("Invoice", isFilterVisible = true)

        activeInvoiceFragment = InvoiceActiveFragment()
        historyInvoiceFragment = HistoryInvoiceFragment()

        val fragments = mutableListOf<Fragment>()
        fragments.add(activeInvoiceFragment)
        fragments.add(historyInvoiceFragment)

        pager.isUserInputEnabled = false
        pager.adapter = ViewPagerOrderFragmentAdapter(fragments, supportFragmentManager, lifecycle)
        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()


        btnFilter.setOnClickListener {
            showDialogFilter()
            if (leaders.isEmpty()) sdmVM.getUserManagement(2)
            if (partners.isEmpty()) vm.getSimplePartners()
        }

        observePartners()

    }

    private fun observePartners() {
        vm.simplePartners.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    editTextPartner?.setText(getString(R.string.text_loading))
                }
                is UiState.Success -> {
                    editTextPartner?.setText(getString(R.string.pilih_partner))
                    editTextPartner?.isEnabled = true
                    hideSkeleton()
                    if (state.data.status) {
                        partners.addAll(state.data.partners)
                        partners.forEach {
                            userSpinnerPartner.add(UserSpinner(it.id, getString(R.string.pilih_partner), it.fullName, getString(R.string.partner)))
                        }
                    }
                }
                is UiState.Error -> {
                    hideSkeleton()
                }
            }
        })

        sdmVM.userManagementData.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    editTextLeader?.setText(getString(R.string.text_loading))
                }
                is UiState.Success -> {
                    editTextLeader?.setText(getString(R.string.text_pilih_leader))
                    editTextLeader?.isEnabled = true
                    val userData = ArrayList<User>()
                    state.data.data.forEach {
                        if (it.position_name != null){
                            userData.add(it)
                        }
                    }
                    leaders.addAll(userData.filter {
                        it.position_name.toLowerCase().contains("leader")
                    })

                    leaders.forEach {
                        userSpinnerLeader.add(UserSpinner(it.id, getString(R.string.text_pilih_leader),it.full_name,getString(R.string.text_leader)))
                    }
                }
                is UiState.Error -> {

                }
            }
        })
    }

    private fun showDialogFilter() {
        if (!::dialogFilter.isInitialized) {

            dialogFilter = MaterialDialog(this)
                .customView(R.layout.dialog_filter_invoice, noVerticalPadding = true)
            val customView = dialogFilter.getCustomView()
            val btnClose = customView.findViewById<AppCompatImageView>(R.id.btnClose)
            val spinnerInvoiceType = customView.findViewById<Spinner>(R.id.spinnerInvoiceType)
            editTextPartner = customView.findViewById(R.id.editTextPartner)
            editTextLeader = customView.findViewById(R.id.editTextLeader)

            setupDialogListener()

            val buttonFilter = customView.findViewById<Button>(R.id.buttonFilter)

            // spinner invoice type
            ArrayAdapter.createFromResource(
                this,
                R.array.invoice_type,
                R.layout.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerInvoiceType.adapter = adapter

                spinnerInvoiceType.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            invoiceType = position
                        }

                    }
            }

            btnClose.setOnClickListener {
                dialogFilter.dismiss()
            }

            buttonFilter.setOnClickListener {
                dialogFilter.dismiss()
                if (pager.currentItem == 0) {
                    activeInvoiceFragment.filterInvoice(
                        invoiceType,
                        partnerFilterSelectedId,
                        leaderIdSelected
                    )
                } else {
                    historyInvoiceFragment.filterInvoice(
                        invoiceType,
                        partnerFilterSelectedId,
                        leaderIdSelected
                    )
                }
            }
        }
        dialogFilter.show()
    }

    private fun setupDialogListener() {

        editTextLeader?.setOnClickListener {
            Log.d("userSpinnerLeader", "data userSpinnerLeader: $userSpinnerLeader")
            startActivity(Intent(this, SearchableSpinnerActivity::class.java)
                    .putExtra("listUserSpinner", userSpinnerLeader))
        }

        editTextPartner?.setOnClickListener {
            Log.d("userSpinnerPartner", "data userSpinnerLeader: $userSpinnerPartner")
            startActivity(Intent(this, SearchableSpinnerActivity::class.java)
                    .putExtra("listUserSpinner", userSpinnerPartner))
        }
    }

    override fun onStart() {
        super.onStart()
        if (!sharedPref.getString(filterLeaderId).isNullOrEmpty()){
            Log.d("filterPartnerName", "filterLeaderName = "+sharedPref.getString(filterLeaderName))
            editTextLeader?.setText(sharedPref.getString(filterLeaderName))
            leaderIdSelected = sharedPref.getString(filterLeaderId).toInt()
        }else if (!sharedPref.getString(filterPartnerId).isNullOrEmpty()){
            Log.d("filterPartnerName", "filterPartnerName = "+sharedPref.getString(filterPartnerName))
            editTextPartner?.setText(sharedPref.getString(filterPartnerName))
            partnerFilterSelectedId = sharedPref.getString(filterPartnerId).toInt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!sharedPref.getString(filterLeaderId).isNullOrEmpty()){
            sharedPref.saveString(filterLeaderName, "")
            sharedPref.saveString(filterLeaderId, "")
        }else if (!sharedPref.getString(filterPartnerId).isNullOrEmpty()){
            sharedPref.saveString(filterPartnerName, "")
            sharedPref.saveString(filterPartnerId, "")
        }
    }

}

class ViewPagerOrderFragmentAdapter(
    private val fragments: List<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    /* because this is static, i think its ok if i hardcode that size */
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}


