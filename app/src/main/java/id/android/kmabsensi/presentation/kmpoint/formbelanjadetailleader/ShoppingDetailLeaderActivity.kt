package id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data
import id.android.kmabsensi.databinding.ActivityShoppingDetailManagementBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanja.ShoppingCartActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader.items.GoodsItems
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader.items.TalentItems
import id.android.kmabsensi.presentation.kmpoint.tambahdaftarbelanja.AddShoppingListActivity
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.visible
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class ShoppingDetailLeaderActivity : BaseActivity() {
    private val TAG = "_shoppingDetail"
    private val vm: ShoppingDetailLeaderViewModel by inject()
    private val binding by lazy {
        ActivityShoppingDetailManagementBinding.inflate(layoutInflater)
    }
    private var idDetail: Int = 0
    private lateinit var dataShopping: Data
    private val talentAdapter = GroupAdapter<GroupieViewHolder>()
    private val itemsAdapter = GroupAdapter<GroupieViewHolder>()
//    private val dialog by lazy {
//        MaterialDialog(this).show {
//            cornerRadius(5f)
//            customView(
//                    R.layout.dialog_batal_pengajuan_belanja,
//                    scrollable = false,
//                    horizontalPadding = false,
//                    noVerticalPadding = true
//            )
//        }
//    }

    private var skeletonPage: SkeletonScreen? = null

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
                    is UiState.Loading ->{
                        skeletonPage = Skeleton.bind(binding.layout)
                                .load(R.layout.skeleton_detail_km_poin)
                                .show()
                    }
                    is UiState.Success -> {
                        skeletonPage?.hide()
                        dataShopping = it.data.data!!
                        setupView(it.data.data)
                    }
                    is UiState.Error -> {
                        skeletonPage?.hide()
                    }
                }
            })
        }
    }

    private fun setupView(data: Data) {
        binding.etNotes.isEnabled = false
        try {
            binding.txUsername.text = data.partner!!.user?.fullName
            binding.txNoPartner.text = "No. Partner : "+data.partner.noPartner
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

        Glide.with(this)
            .load(data.partner?.user?.photoProfileUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_my_profile)
            .into(binding.imgProfile)

        binding.txTotalPrice.text = data.total.toString()
        binding.etNotes.text = data.notes?.toEditable()
        talentAdapter.clear()
        data.shoopingRequestParticipants?.forEach {
            // todo testing detail user
            try {
                talentAdapter.add(TalentItems(it.user))
            }catch (e: Exception){}
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
        val dialog = MaterialDialog(this).show {
            cornerRadius(5f)
            customView(
                    R.layout.dialog_batal_pengajuan_belanja,
                    scrollable = false,
                    horizontalPadding = false,
                    noVerticalPadding = true
            )
        }
        val customView = dialog.getCustomView()
        val btnYa = customView.findViewById<Button>(R.id.btn_ya)
        val btnTidak = customView.findViewById<Button>(R.id.btn_tidak)
        var listItems = ArrayList<UpdateShoppingRequestParams.UpdateItem>()
        dataShopping.shoopingRequestItems?.forEach {
            listItems.add(UpdateShoppingRequestParams.UpdateItem("", it.id!!, it.name!!, it.total!!, false))
        }
        var idParticipant = ArrayList<Int>()
        dataShopping.shoopingRequestParticipants?.forEach {
            idParticipant.add(it.user.id)
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
                    is UiState.Loading -> setupLoadAnimation(true)
                    is UiState.Success -> {
                        if (it.data.success){
                            setupLoadAnimation(false)
                            dialog.dismiss()
                            onBackPressed()
                        }else{
                            toast(it.data.message!!)
                        }
                    }
                    is UiState.Error -> setupLoadAnimation(false)
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

    private fun setupLoadAnimation(isPlay: Boolean){
        if (isPlay){
            binding.loadAnimation.visible()
            binding.layout.isEnabled = false
        }else{
            binding.loadAnimation.gone()
            binding.layout.isEnabled = true
        }
    }
}