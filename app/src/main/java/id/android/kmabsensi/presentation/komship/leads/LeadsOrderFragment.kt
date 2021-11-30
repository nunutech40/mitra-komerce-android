package id.android.kmabsensi.presentation.komship.leads

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.body.komship.InputNotesParams
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.data.remote.response.komship.LeadsItem
import id.android.kmabsensi.databinding.FragmentLeadsOrderBinding
import id.android.kmabsensi.presentation.base.BaseFragmentRf
import id.android.kmabsensi.presentation.komship.MyOrderViewModel
import id.android.kmabsensi.presentation.viewmodels.SdmViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.fragment_leads_order.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//baru
class LeadsOrderFragment : BaseFragmentRf<FragmentLeadsOrderBinding>(
    FragmentLeadsOrderBinding::inflate
) {
    private val vm: MyOrderViewModel by inject()
    private val sdmVM: SdmViewModel by viewModel()
    private val prefHelper: PreferencesHelper by inject()

    private lateinit var leadsOrderAdapter: LeadsOrderAdapter
    private var listLeads: MutableList<LeadsItem> = ArrayList()
    private var dataPartner: MutableList<KomPartnerItem> = ArrayList()
    private lateinit var mLayoutManager: LinearLayoutManager
    private var sklList: SkeletonScreen? = null
    private var idPartner = 0
    private var userId: Int? = null
    private lateinit var myDialog: MyDialog
    private lateinit var dataLocal: KomPartnerResponse
    private lateinit var isToday: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupList()
        setupListner()
        setupObserver()
    }

    private fun setupView() {
        myDialog = MyDialog(requireContext())
        userId = sdmVM.getUserData().id
        Log.i("idUser", "setupView: $userId")
        dataLocal =
            Gson().fromJson<KomPartnerResponse>(
                prefHelper.getString(PreferencesHelper.DATA_PARTNER),
                KomPartnerResponse::class.java
            )
        Log.d("Size", "setupView: ${dataLocal.data?.size}")

        if (dataLocal.data?.size!! > 1) {

            idPartner = prefHelper.getInt(PreferencesHelper.ID_PARTNER.toString())
            binding?.tvName?.text = prefHelper.getString(PreferencesHelper.NAME_PARTNER)
        } else {

            idPartner = dataLocal.data!![0].partnerId!!
            binding?.tvName?.text = dataLocal.data!![0].partnerName

        }

        if (idPartner == 0) {
            showDialogSetPartner(requireContext(), object : OnSingleCLick {
                override fun onCLick() {
                    val intentSetKomboard = Intent()
                    intentSetKomboard.component =
                        ComponentName(
                            requireActivity(),
                            "id.android.kmabsensi.presentation.komboard.KomboardActivity"
                        )
                    intentSetKomboard.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentSetKomboard)
                    requireActivity().finish()
                }
            })
        } else {
            vm.getLeadsFilter(userId!!, idPartner, getTodayDate())
        }
    }

    private fun setupListner() {
        binding?.apply {
            srLeadsOrder.setOnRefreshListener {
                srLeadsOrder.isRefreshing = true
                userId?.let { vm.getLeadsFilter(it, idPartner, getTodayDate()) }
                binding?.tiePartner?.isEnabled = getTodayDate() == getTodayDate()
            }

//            tvName.text = prefHelper.getString(PreferencesHelper.NAME_PARTNER)
            tiePartner.addTextChangedListener(cekNotesTextWatcher)
            btnSaveNb.setOnClickListener { inputNotesLeads() }
        }
    }

    private fun inputNotesLeads() {
        val notes = binding?.tiePartner?.text.toString()
        //date today
        val formatDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateTime = formatDateTime.format(Date())
        val inputParams = userId?.let { it1 ->
            InputNotesParams(
                it1,
                idPartner,
                notes,
                dateTime
            )
        }
        inputParams?.let { it1 ->
            vm.inputNotesLeads(it1)
            vm.inputNotesState.observe(requireActivity(), {
                when (it) {
                    is UiState.Loading -> {
                        myDialog.show()
                    }
                    is UiState.Success -> {
                        myDialog.dismiss()
                        Toast.makeText(requireContext(), "Berhasil ditambahkan", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is UiState.Error -> {
                        myDialog.dismiss()
                        Toast.makeText(requireContext(), it.throwable.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }
    }

    fun filterLeads(idUser: Int, idPartners: Int, filterDate: String) {
        vm.getLeadsFilter(idUser, idPartner, filterDate)
        binding?.tiePartner?.isEnabled = filterDate == getTodayDate()
    }

    private val cekNotesTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val notes: String = binding?.tiePartner?.text.toString()
            if (notes.isNotEmpty()) {
                binding?.btnSaveNb?.isEnabled = true
                binding?.btnSaveNb?.background =
                    resources.getDrawable(R.drawable.background_orange_10dp)
            } else {
                binding?.btnSaveNb?.isEnabled = false
                binding?.btnSaveNb?.background =
                    resources.getDrawable(R.drawable.bg_button_disable_komboard)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun setupObserver() {
        vm.leadsFilterState.observe(requireActivity(), {
            when (it) {
                is UiState.Loading -> {
                    Timber.tag("_leadsFilter").d("on Loading")
                    showSkeleton()
                }
                is UiState.Success -> {
                    if (it.data.data == null) {
                        binding?.apply {
                            tvEmptyCart.visible()
                            tiePartner.setText("")
                            rvLeads.gone()
                            srLeadsOrder.isRefreshing = false
                        }

                    } else {
                        binding?.apply {
                            srLeadsOrder.isRefreshing = false
                            tvEmptyCart.gone()
                            rvLeads.visible()
                            tiePartner.setText(it.data.data.notes)
                        }
                        listLeads.clear()
                        it.data.data?.leads?.let { it1 -> listLeads.addAll(it1) }
                        leadsOrderAdapter.setData(listLeads)
                        if (getTodayDate() != it.data.data.leads?.get(0)?.date_leads) {
                            binding?.btnSaveNb?.isEnabled = false
                            binding?.btnSaveNb?.background = resources.getDrawable(R.drawable.bg_button_disable_komboard)
                            binding?.tiePartner?.isEnabled = false
                        } else {
                            binding?.tiePartner?.isEnabled = true
                        }
                    }
                    sklList?.hide()
                }
                is UiState.Error -> {
                    Timber.tag("_leadsFilter").d("on error ${it.throwable}")
                    sklList?.hide()
                    binding?.srLeadsOrder?.isRefreshing = false
                    Timber.d(it.throwable)
                }
            }
        })
    }

    private fun setupList() {
        mLayoutManager = LinearLayoutManager(requireContext())
        leadsOrderAdapter =
            LeadsOrderAdapter(requireContext(), object : LeadsOrderAdapter.onAdapterListener {
                override fun onCLick(data: LeadsItem) {
                    showDialogConfirmDeleteLeads(requireContext(), object : OnSingleCLick {
                        override fun onCLick() {
                            data.id?.let { vm.deleteLeadsOrder(it) }
                            val position = listLeads.indexOf(data)
                            listLeads.removeAt(position)
                            leadsOrderAdapter.removeItem(position)
                        }
                    })
                }
            })

        binding?.rvLeads?.apply {
            adapter = leadsOrderAdapter
            layoutManager = mLayoutManager
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.getLeadsFilter(userId!!, idPartner, getTodayDate())
    }

    private fun showSkeleton() {
        if (sklList == null) {
            sklList = Skeleton.bind(binding?.rvLeads)
                .adapter(leadsOrderAdapter)
                .load(R.layout.skeleton_list_leads)
                .show()
        } else {
            sklList?.show()
        }
    }
}
