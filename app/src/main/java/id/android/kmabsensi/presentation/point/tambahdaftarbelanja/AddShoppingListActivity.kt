package id.android.kmabsensi.presentation.point.tambahdaftarbelanja

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import com.google.android.material.chip.Chip
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Partner
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.databinding.ActivityAddShoppingListBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.partner.partnerpicker.PartnerPickerActivity
import id.android.kmabsensi.presentation.point.formbelanjadetailfinance.TalentModel
import id.android.kmabsensi.utils.PARTNER_DATA_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.invis
import id.android.kmabsensi.utils.visible
import kotlinx.android.synthetic.main.activity_add_invoice.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AddShoppingListActivity : BaseActivity() {
    private val dataTags: ArrayList<TalentModel> = arrayListOf()
    private var tampData: MutableList<TalentModel> = arrayListOf()
    private var talentNameData: MutableList<String> = arrayListOf()

    private var listTools: ArrayList<ToolsModel> = arrayListOf()
    private val addShoppingVM: AddShoppingViewModel by viewModel()
    private var partners = mutableListOf<Partner>()
    private val PICK_PARTNER_RC = 112
    private var partnerSelected: Partner? = null
    private var dataTalent = ArrayList<User>()

    private val binding by lazy { ActivityAddShoppingListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        addShoppingVM.getAllSdm()
//        setupDataDummy()
        setupChips()
        addFormTools()
        setupListener()
        setupObserver()
    }

    private fun setupObserver() {
        addShoppingVM.getDataPartners().observe(this, {
            when (it) {
                is UiState.Loading -> {
                    binding.edtPilihPartner.isEnabled = false
                    binding.edtPilihPartner.hint = getString(R.string.text_loading)
                    Log.d("_Partner", "LOADING")
                }
                is UiState.Success -> {
                    binding.edtPilihPartner.isEnabled = true
                    binding.edtPilihPartner.hint = getString(R.string.pilih_partner)
                    partners.addAll(it.data.partners)
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
                    binding.autoCompleteTagTalent.hint = "Pilih Talent"
                    it.data.data.forEach {
                        dataTalent.add(it)
                        talentNameData.add("${it.full_name}")
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
    }

    private fun addFormTools() {
        val inflater = LayoutInflater.from(this).inflate(R.layout.item_row_form_belanja, null, false)
        binding.llFormTools.addView(inflater, binding.llFormTools.childCount)
        val btnRemove: ImageView = inflater.findViewById(R.id.btn_remove)
        val toolsName: EditText = inflater.findViewById(R.id.et_name_tools)
        val toolsPrice: EditText = inflater.findViewById(R.id.tx_price_forecasts)
        var current: String = ""
        toolsPrice.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s != current) {
                    toolsPrice.removeTextChangedListener(this)

                    val locale = Locale("id", "ID")
                    val currency = Currency.getInstance(locale)
                    val cleanString = s!!.replace("[${currency.symbol},.]".toRegex(), "")
                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance(locale).format(parsed / 100)

                    current = formatted
                    toolsPrice.setText(formatted)
                    toolsPrice.setSelection(formatted.length)
                    toolsPrice.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        if (binding.llFormTools.childCount == 1) btnRemove.invis() else btnRemove.visible()
        btnRemove.setOnClickListener {
            removeData(inflater)
        }
    }

    private fun saveDataFormTools() {
        listTools.clear()
        val count = binding.llFormTools.childCount
        var v: View? = null

        for (i in 0 until count) {
            v = binding.llFormTools.getChildAt(i)
            val toolsName: EditText = v.findViewById(R.id.et_name_tools)
            val toolsPrice: EditText = v.findViewById(R.id.tx_price_forecasts)
            val btnRemove: ImageView = v.findViewById(R.id.btn_remove)
            btnRemove.visible()

            var datatools = ToolsModel(i, toolsName.text.toString(), toolsPrice.text.toString())
            listTools.add(datatools)
        }
        getDataFormTools()
    }

    private fun getDataFormTools() {
        val count = binding.llFormTools.childCount
        for (i in 0 until count) {
            Log.d("TAGTAGTAG", "getDataFormTools: ${listTools[i]}")
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
            tampData.forEach {
                if (it.name.equals(data.name)) isChecked = false
            }
            if (isChecked) {
                addChipToGroup(data, tampData)
                tampData.add(data)
            } else {
                Log.d("_checkAutoComplete", "Nama sudah terdaftar.")
            }
        }

        // select from auto complete
        binding.autoCompleteTagTalent.setOnItemClickListener { adapterView, _, position, _ ->
            binding.autoCompleteTagTalent.text = null
            val name = adapterView.getItemAtPosition(position) as String
            addTag(TalentModel(tampData.size, name, "posisi ${tampData.size}"))
        }

        // done keyboard button is pressed
        binding.autoCompleteTagTalent.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textView.text = null
                toast("Nama tidak terdaftar.")
                return@setOnEditorActionListener true
            }
            false
        }

        // initialize
        for (tag in tampData) {
            addChipToGroup(tag, tampData)
        }

    }

    private fun setupDataDummy() {
        for (index in 0..10) {
            dataTags.add(TalentModel(index, "talent $index", "posisi ke $index"))
        }
        dataTags.forEach {
            talentNameData.add("${it.id} - ${it.name}")
        }
    }

    private fun addChipToGroup(data: TalentModel, tampData: MutableList<TalentModel>) {
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
            tampData.remove(data)
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
}