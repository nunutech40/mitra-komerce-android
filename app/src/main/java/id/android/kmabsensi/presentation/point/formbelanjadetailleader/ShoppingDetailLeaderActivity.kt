package id.android.kmabsensi.presentation.point.formbelanjadetailleader

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data
import id.android.kmabsensi.databinding.ActivityShoppingDetailManagementBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.point.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.point.formbelanjadetailleader.items.GoodsItems
import id.android.kmabsensi.presentation.point.formbelanjadetailleader.items.TalentItems
import id.android.kmabsensi.presentation.point.tambahdaftarbelanja.AddShoppingListActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class ShoppingDetailLeaderActivity : BaseActivity() {
    private val vm: ShoppingDetailLeaderViewModel by inject()
    private val binding by lazy {
        ActivityShoppingDetailManagementBinding.inflate(layoutInflater)
    }
    private var idDetail: Int = 0
    private lateinit var dataShopping: Data
    private val talentAdapter = GroupAdapter<GroupieViewHolder>()
    private val itemsAdapter = GroupAdapter<GroupieViewHolder>()
    private val dialog by lazy {
        MaterialDialog(this).show {
            cornerRadius(5f)
            customView(
                    R.layout.dialog_batal_pengajuan_belanja,
                    scrollable = false,
                    horizontalPadding = false,
                    noVerticalPadding = true
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        idDetail = intent.getIntExtra("idDetailSHopping", 0)
        binding.toolbar.txtTitle.text = getString(R.string.text_detail_belanja)
        vm.getShoppingDetail(idDetail)
        setupListener()
        setupObserver()
        setupRv()
    }

    private fun setupRv() {
        binding.rvTalent.apply {
            adapter = talentAdapter
            setHasFixedSize(true)
        }
        binding.rvTools.apply {
            adapter = itemsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObserver() {
        if (idDetail != 0) {
            vm.shoppingDetail.observe(this, {
                when (it) {
                    is UiState.Loading -> Log.d("_testingTAG", "loading ...")
                    is UiState.Success -> {
                        dataShopping = it.data.data!!
                        setupView(it.data.data)
                    }
                    is UiState.Error -> Log.d("_testingTAG", "Error ${it.throwable}")
                }
            })
        }
    }

    private fun setupView(data: Data) {
        binding.etNotes.isEnabled = false
        try {
            binding.txUsername.text = data.userRequester!!.fullName
            binding.txNoPartner.text = data.partner?.noPartner
        } catch (e: Exception) {
            Log.d("_Exception", "setupView: ${e.message}")
        }
        binding.txNoTransaksi.text = "No. " + data.transactionNo

        /**
        set rupiah format
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.txTotalPrice.setText(formatRupiah.format(data.total!!.toDouble()))
         */

        binding.txTotalPrice.text = data.total.toString()
        binding.etNotes.text = data.notes?.toEditable()
        talentAdapter.clear()
        data.shoopingRequestParticipants?.forEach {
            talentAdapter.add(TalentItems(it.user!!))
        }
        itemsAdapter.clear()
        data.shoopingRequestItems?.forEach {
            itemsAdapter.add(GoodsItems(it))
        }

        binding.txStatus.apply {
            text = vm.getTextStatus(data.status!!)
            setTextColor(resources.getColor(vm.getStatusTextColor(data.status)))
            setBackgroundColor(resources.getColor(vm.getStatusBackgroundColor(data.status)))
        }

        if (!vm.editable(data.status!!)) {
            binding.btnEdit.isClickable = false
            binding.btnBatalkan.isClickable = false
            binding.btnEdit.alpha = 0.6f
            binding.btnBatalkan.alpha = 0.6f
        }
    }

    private fun setupListener() {
        binding.btnBatalkan.setOnClickListener {
            showDialogCancle()
        }
        binding.btnEdit.setOnClickListener {
            startActivity<AddShoppingListActivity>(
                    "_editMode" to true,
                    "_editDetailShopping" to dataShopping
            )
        }
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showDialogCancle() {
        val customView = dialog.getCustomView()
        val btnYa = customView.findViewById<Button>(R.id.btn_ya)
        val btnTidak = customView.findViewById<Button>(R.id.btn_tidak)
        var listItems = ArrayList<UpdateShoppingRequestParams.UpdateItem>()
        dataShopping.shoopingRequestItems?.forEach {
            listItems.add(UpdateShoppingRequestParams.UpdateItem("", it.id!!, it.name!!, it.total!!, false))
        }
        var idParticipant = ArrayList<Int>()
        dataShopping.shoopingRequestParticipants?.forEach {
            idParticipant.add(it.user?.id!!)
        }
        btnYa.setOnClickListener {
            vm.updateShoppingRequest(id = idDetail,
                    body = UpdateShoppingRequestParams(
                            items = listItems,
                            notes = dataShopping.notes.toString(),
                            participant_user_ids = idParticipant,
                            status = "canceled")
            ).observe(this, {
                when (it) {
                    is UiState.Loading -> {
                        Log.d("_cancleShopping", "loading ...")
                        binding.loadAnimation.visible()
                    }
                    is UiState.Success -> {
                        binding.loadAnimation.gone()
                        dialog.dismiss()
                        onBackPressed()
                    }
                    is UiState.Error -> {
                        binding.loadAnimation.gone()
                        Log.d("_cancleShopping", "Error ${it.throwable}")
                    }
                }
            })
        }
        btnTidak.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onBackPressed() {
        startActivity<ShoppingCartActivity>()
        finishAffinity()
    }
}