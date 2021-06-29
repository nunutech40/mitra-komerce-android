package id.android.kmabsensi.presentation.kmpoint.tambahdaftarbelanja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.chip.Chip
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.body.kmpoint.CreateShoppingRequestParams
import id.android.kmabsensi.data.remote.body.kmpoint.Item
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams
import id.android.kmabsensi.data.remote.body.kmpoint.UpdateShoppingRequestParams.UpdateItem
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.data.remote.response.kmpoint.DetailShoppingResponse.Data
import id.android.kmabsensi.databinding.ActivityAddShoppingListBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailfinance.TalentModel
import id.android.kmabsensi.presentation.kmpoint.formbelanjadetailleader.ShoppingDetailLeaderActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_add_invoice.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddShoppingListActivity : BaseActivity() {
    private val addShoppingVM: AddShoppingViewModel by viewModel()
    private val vm: HomeViewModel by inject()
    private lateinit var user: User
    private var saveTalent: MutableList<TalentModel> = arrayListOf()
    private var talentNameData: MutableList<String> = arrayListOf()
    private var listItems: ArrayList<Item> = arrayListOf()
    private var partners = mutableListOf<Partner>()
    private val PICK_PARTNER_RC = 112
    private var partnerEditMode: Data.Partner? = null // data partner when edit/update mode conditiion
    private var partnerSelected: Partner? = null // // data partner when normal conditiion
    private var dataTalent = ArrayList<User>()
    private lateinit var detailShopping: Data
    private var shoopingRequestItems: Data.ShoopingRequestItem? = null

    /**
    edit mode is used when user access this activity from button 'edit' which in shopping detail activity
     */
    private val editMode by lazy { intent.getBooleanExtra("_editMode", false) }
    private var listItemsUpdate: ArrayList<UpdateItem> = arrayListOf()
    private var updateListItems: ArrayList<UpdateItem> = arrayListOf() // new data for update items
    private var status = ""
    private val binding by lazy {
        ActivityAddShoppingListBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupChips()
        setupListener()
        setupObserver()
    }

    private fun setupView() {
        addShoppingVM.getAllSdm()
        user = vm.getUserData()
        if (editMode) {
            try {
                binding.toolbar.txtTitle.text = getString(R.string.text_edit_daftar_belanja)
                binding.edtPilihPartner.isEnabled = false
                detailShopping = intent.getParcelableExtra<Data>("_editDetailShopping")!!
                detailShopping.let {
                    partnerEditMode = it.partner
                    binding.edtPilihPartner.setText(partnerEditMode!!.fullName)

                    it.shoopingRequestParticipants!!.forEach {
                        saveTalent.add(TalentModel(it.userId!!, it.user?.fullName!!, it.user.roleId.toString()))
                    }

                    binding.txTotalPoint.text = it.total.toString()
                    binding.etNotes.setText(it.notes, TextView.BufferType.EDITABLE)
                    status = it.status!!
                    binding.btnTambahBarang.gone()
                    it.shoopingRequestItems?.forEach {
                        listItemsUpdate.add(UpdateItem(id = it.id!!, name = it.name!!, total = it.total!!, will_delete = false))
                        shoopingRequestItems = it
                        editFormTools()
                    }
                }
            } catch (e: Exception) {
            }
        } else {
            binding.toolbar.txtTitle.text = getString(R.string.text_tambah_daftar_belanja)
            addFormTools()
        }

    }

    private fun setupObserver() {
        addShoppingVM.getDataPartners().observe(this, {
            when (it) {
                is UiState.Loading -> {
                    binding.edtPilihPartner.isEnabled = false
                    binding.edtPilihPartner.hint = getString(R.string.text_loading)

                }
                is UiState.Success -> {
                    binding.edtPilihPartner.isEnabled = true

                    if (editMode) binding.edtPilihPartner.setText(partnerEditMode!!.fullName)
                    else binding.edtPilihPartner.hint = getString(R.string.pilih_partner)

                    it.data.partners.forEach {
                        if (!it.partnerDetail.partnerCategoryName.isNullOrEmpty()) {
                            partners.add(it)
                        }
                    }
                }
                is UiState.Error -> Log.d("_Partner", "ERROR ${it.throwable.message}")
            }
        })

        addShoppingVM.sdm.observe(this, {
            when (it) {
                is UiState.Loading -> {
                    binding.autoCompleteTagTalent.isEnabled = false
                    binding.autoCompleteTagTalent.hint = getString(R.string.text_loading)
                }
                is UiState.Success -> {
                    binding.autoCompleteTagTalent.isEnabled = true
                    binding.autoCompleteTagTalent.hint = getString(R.string.text_pilih_talent)
                    var idx = 0
                    it.data.data.forEach {
                        dataTalent.add(it)
                        talentNameData.add("$idx - ${it.full_name}")
                        idx++
                    }
                }
                is UiState.Error -> Log.d("_Talent", "ERROR ${it.throwable.message}")
            }
        })
    }

    private fun setupListener() {
        binding.btnTambahBarang.setOnClickListener {
            addFormTools()
        }
        binding.edtPilihPartner.setOnClickListener {
            startActivityForResult<PartnerPickerActivity>(
                    PICK_PARTNER_RC,
                    "listPartner" to partners)
        }
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                submitDataShopping()
            } else {
                toast("Form harus terisi semua.")
            }
        }
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun submitDataShopping() {
        var userIds: ArrayList<Int> = arrayListOf()
        saveTalent.forEach {
            userIds.add(it.id)
        }
        var note = if (binding.etNotes.text.toString() == "") "-" else binding.etNotes.text.toString()

        if (editMode) {
            addShoppingVM.updateShoppingRequest(
                    id = detailShopping.id!!,
                    UpdateShoppingRequestParams(
                            items = updateListItems,
                            notes = note,
                            status = status,
                            participant_user_ids = userIds
                    )).observe(this, {
                when (it) {
                    is UiState.Loading -> Log.d("_createShoppList", "Loading .... ")
                    is UiState.Success -> {
                        val response = it.data.data
                        Log.d("_createShoppList", "Success $response ")
                        startActivity<ShoppingDetailLeaderActivity>(
                                "idDetailSHopping" to response!!.id)
                    }
                    is UiState.Error -> Log.d("_createShoppList", "Error.... ${it.throwable}")
                }
            })
        } else {
            addShoppingVM.createShoppingRequest(CreateShoppingRequestParams(
                    items = listItems,
                    notes = note,
                    participant_user_ids = userIds,
                    partner_id = partnerSelected!!.partnerDetail.id,
                    user_requester_id = user.id
            )).observe(this, {
                when (it) {
                    is UiState.Loading -> Log.d("_createShoppList", "Loading boss.... ")
                    is UiState.Success -> {
                        val response = it.data.data
                        startActivity<ShoppingDetailLeaderActivity>(
                                "idDetailSHopping" to response!!.id)
                    }
                    is UiState.Error -> Log.d("_createShoppList", "Error boss.... ${it.throwable}")
                }
            })
        }
    }

    private fun addFormTools() {
        val inflater = LayoutInflater.from(this).inflate(R.layout.item_row_form_belanja, null, false)
        binding.llFormTools.addView(inflater, binding.llFormTools.childCount)
        val btnRemove: ImageView = inflater.findViewById(R.id.btn_remove)
        val toolsPrice: EditText = inflater.findViewById(R.id.tx_price_forecasts)
        var total = 0
        toolsPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TAGTAGTAG", "onTextChanged editmode: $s")
                total = 0
                saveDataFormTools()
                listItems.forEach {
                    total += it.total
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.txTotalPoint.text = total.toString()
            }
        })

        if (!editMode) {
            if (binding.llFormTools.childCount == 1) btnRemove.invis() else btnRemove.visible()
        } else btnRemove.visible()

        btnRemove.setOnClickListener {
            removeData(inflater)
            saveDataFormTools()
        }
    }

    private fun editFormTools() {
        val inflater = LayoutInflater.from(this).inflate(R.layout.item_row_form_belanja, null, false)
        binding.llFormTools.addView(inflater, binding.llFormTools.childCount)
        val btnRemove: ImageView = inflater.findViewById(R.id.btn_remove)
        val toolname: EditText = inflater.findViewById(R.id.et_name_tools)
        val toolPrice: EditText = inflater.findViewById(R.id.tx_price_forecasts)
        toolPrice.setText(shoopingRequestItems?.total.toString(), TextView.BufferType.EDITABLE)
        toolname.setText(shoopingRequestItems?.name.toString(), TextView.BufferType.EDITABLE)
        var total = 0
        toolPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                total = 0
                saveDataFormTools()
                updateListItems.forEach {
                    total += it.total
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.txTotalPoint.text = total.toString()
            }
        })
        btnRemove.visible()
        btnRemove.setOnClickListener {
            if (editMode) {
                toolname.isEnabled = !toolname.isEnabled
                toolPrice.isEnabled = !toolPrice.isEnabled
                if (!toolname.isEnabled) {
                    toolname.alpha = 0.3f
                    toolPrice.alpha = 0.3f
                    btnRemove.setImageDrawable(resources.getDrawable(R.drawable.ic_resource_return))
                    toolPrice.setText("0", TextView.BufferType.EDITABLE)
                    toolname.setText("-", TextView.BufferType.EDITABLE)
                } else {
                    toolname.alpha = 1f
                    toolPrice.alpha = 1f
                    btnRemove.setImageDrawable(resources.getDrawable(R.drawable.ic_trash))
                    toolPrice.setText(shoopingRequestItems?.total.toString(), TextView.BufferType.EDITABLE)
                    toolname.setText(shoopingRequestItems?.name.toString(), TextView.BufferType.EDITABLE)
                }
            } else removeData(inflater)
            saveDataFormTools()
        }
    }

    private fun saveDataFormTools() {
        listItems.clear()
        updateListItems.clear()
        val count = binding.llFormTools.childCount
        var v: View? = null
        var checked = true

        for (i in 0 until count) {
            v = binding.llFormTools.getChildAt(i)
            val toolsName: EditText = v.findViewById(R.id.et_name_tools)
            val toolsPrice: EditText = v.findViewById(R.id.tx_price_forecasts)
            val btnRemove: ImageView = v.findViewById(R.id.btn_remove)

            if (!editMode) {
                if (i != 0) btnRemove.visible() else btnRemove.invis()
            } else btnRemove.visible()

            if (toolsName.text.toString().equals("")) {
                toolsName.error = "form nama barang tidak boleh kosong!"
                checked = false
                binding.btnSubmit.isEnabled = false
            } else binding.btnSubmit.isEnabled = true

            if (toolsPrice.text.toString().equals("")) {
                toolsPrice.error = "form tidak boleh kosong!"
                checked = false
                binding.btnSubmit.isEnabled = false
            } else binding.btnSubmit.isEnabled = true

            if (checked) {
                val total = if (toolsPrice.isEnabled) toolsPrice.text.toString() else "0"
                val datatools = ToolsModel(i, toolsName.text.toString(), total)
                if (editMode) updateListItems.add(UpdateItem(
                        id = listItemsUpdate[i].id,
                        name = datatools.name!!,
                        total = datatools.price!!.toInt(),
                        will_delete = !toolsName.isEnabled))
                else listItems.add(Item("", datatools.name!!, datatools.price!!.toInt()))
            }
        }
    }

    private fun removeData(view: View) {
        binding.llFormTools.removeView(view)
    }

    private fun setupChips() {
        val adapter = ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                talentNameData
        )
        binding.autoCompleteTagTalent.setAdapter(adapter)

        fun addTag(data: TalentModel) {
            var isChecked = true
            saveTalent.forEach {
                if (it.name.equals(data.name)) isChecked = false
            }
            if (isChecked) {
                addChipToGroup(data)
                saveTalent.add(data)
            } else {
                Log.d("_checkAutoComplete", "Nama sudah terdaftar.")
            }
        }

        binding.autoCompleteTagTalent.setOnItemClickListener { adapterView, _, position, _ ->
            binding.autoCompleteTagTalent.text = null
            val name = adapterView.getItemAtPosition(position) as String
            val index = name.split(" ")[0].toInt()
            val talent = dataTalent[index]
            addTag(TalentModel(talent.id, talent.full_name, talent.position_name))
        }

        // initialize
        for (tag in saveTalent) {
            addChipToGroup(tag)
        }

    }

    private fun addChipToGroup(data: TalentModel) {
        var chip = Chip(this)
        chip.text = data.name
        chip.isClickable = true
        chip.isCheckable = false
        chip.isCloseIconVisible = true
        chip.setTextColor(resources.getColorStateList(R.color.white))
        chip.closeIconTint = resources.getColorStateList(R.color.white)
        chip.chipBackgroundColor = resources.getColorStateList(id.android.kmabsensi.R.color.cl_orange)
        binding.mainTagChipGroup.addView(chip)

        chip.setOnCloseIconClickListener {
            binding.mainTagChipGroup.removeView(chip)
            saveTalent.remove(data)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PARTNER_RC && resultCode == Activity.RESULT_OK) {
            partnerSelected = data?.getParcelableExtra<Partner>(PARTNER_DATA_KEY)
            edtPilihPartner.error = null
            edtPilihPartner.setText(partnerSelected?.fullName)
        }
    }

    fun validateForm(): Boolean {
        var isChecked = true
        saveDataFormTools()
        if (binding.edtPilihPartner.text.isNullOrEmpty()) {
            isChecked = false
            binding.edtPilihPartner.requestFocus()
            binding.edtPilihPartner.error = "Kamu belum memilih partner!"
        }

        if (saveTalent.size == 0) {
            binding.autoCompleteTagTalent.requestFocus()
            binding.autoCompleteTagTalent.error = "Kamu belum memilih talent!"
            isChecked = false
        }

        // saveDataFormTools update list Items
        saveDataFormTools()
        if (editMode) {
            if (updateListItems.size == 0) {
                binding.llFormTools.requestFocus()
                isChecked = false
            }
        } else {
            if (listItems.size == 0) {
                binding.llFormTools.requestFocus()
                isChecked = false
            }
        }

        return isChecked
    }
}