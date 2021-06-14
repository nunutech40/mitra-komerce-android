package id.android.kmabsensi.presentation.point.tambahdaftarbelanja

import android.R
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import id.android.kmabsensi.databinding.ActivityAddShoppingListBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.point.formbelanjadetailfinance.TalentModel
import id.android.kmabsensi.presentation.point.tambahdaftarbelanja.adapter.ShoppingFormAdapter
import id.android.kmabsensi.presentation.point.tambahdaftarbelanja.adapter.ToolsItems
import org.jetbrains.anko.toast

class AddShoppingListActivity : BaseActivity() {
    private val dataTags : ArrayList<TalentModel> = arrayListOf()
    private var tampData : MutableList<TalentModel> = arrayListOf()
    private var nameTalentData : MutableList<String> = arrayListOf()
    private val dataForm : ArrayList<ToolsModel> = arrayListOf()
    private val formAdapter = GroupAdapter<GroupieViewHolder>()
    private var idx = 0

    private lateinit var toolsAdapter : ShoppingFormAdapter
    private var sampleData : ArrayList<ToolsModel> = arrayListOf()

    private val binding by lazy { ActivityAddShoppingListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupDataDummy()
        setupChips()
        setupRv()

//        set first data list for first form
        dataForm.add(ToolsModel(idx))
        sampleData.add(ToolsModel(idx))

        setupListener()
        setupList(dataForm)
    }

    private fun setupList(dataForm: ArrayList<ToolsModel>) {
        formAdapter.clear()
        dataForm.forEach {
            formAdapter.add(ToolsItems(this, it){model, position ->
                Log.d("TAGTAGTAG", "dataForm1 : ${model.id}, $position")
                formAdapter.removeGroupAtAdapterPosition(position)
                dataForm.removeAt(position-1)
                setupList(dataForm)
            })
        }
    }

    private fun setupListener() {
        binding.btnTambahBarang.setOnClickListener {
            idx++
            dataForm.add(ToolsModel(idx))
            setupList(dataForm)
            sampleData.add(ToolsModel(idx))
            toolsAdapter.notifyDataSetChanged()
        }
    }

    private fun setupRv() {
        val layout = LinearLayoutManager(this)
        binding.rvFormDaftarBelanja.apply {
            layoutManager = layout
            adapter = formAdapter
        }

        toolsAdapter = ShoppingFormAdapter(sampleData,
                object : ShoppingFormAdapter.onAdapterListener {
                    override fun OnDeleteForm(dataForm: ToolsModel, position: Int) {
                        sampleData.remove(dataForm)
                        toolsAdapter.notifyDataSetChanged()
                        Log.d("TAGTAGTAG", "dataForm2: ${dataForm.id}, $position")
                    }
                })

        val layoutSample = LinearLayoutManager(this)
        binding.rvFormDaftarBelanjaSample.apply {
            layoutManager = layoutSample
            adapter = toolsAdapter
        }
    }

    private fun setupChips() {
        val adapter = ArrayAdapter<String>(
                this,
                R.layout.simple_dropdown_item_1line,
                nameTalentData
        )
        binding.mainTagAutoCompleteTextView.setAdapter(adapter)

        fun addTag(data: TalentModel) {
            var isChecked = true
            tampData.forEach {
                if (it.name.equals(data.name)) isChecked = false
            }
            if (isChecked) {
                addChipToGroup(data, tampData)
                tampData.add(data)
            }else{
                Log.d("_checkAutoComplete", "Nama sudah terdaftar.")
            }
        }

        // select from auto complete
        binding.mainTagAutoCompleteTextView.setOnItemClickListener { adapterView, _, position, _ ->
            binding.mainTagAutoCompleteTextView.text = null
            val name = adapterView.getItemAtPosition(position) as String
            addTag(TalentModel(tampData.size, name, "posisi ${tampData.size}"))
        }

        // done keyboard button is pressed
        binding.mainTagAutoCompleteTextView.setOnEditorActionListener { textView, actionId, keyEvent ->
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
        for (index in 0..10){
            dataTags.add(TalentModel(index, "talent $index", "posisi ke $index"))
        }
        dataTags.forEach {
            nameTalentData.add("${it.id} - ${it.name}")
        }
    }

    private fun addChipToGroup(data: TalentModel, tampData: MutableList<TalentModel> ){
        val chip = Chip(this)
        chip.text = data.name
        chip.isClickable = true
        chip.isCheckable = false
        chip.isCloseIconVisible = true
        chip.closeIconTint = resources.getColorStateList(R.color.white)
        chip.chipBackgroundColor = resources.getColorStateList(id.android.kmabsensi.R.color.cl_orange)
        binding.mainTagChipGroup.addView(chip)

        chip.setOnCloseIconClickListener {
            binding.mainTagChipGroup.removeView(chip)
            tampData.remove(data)
        }
    }
}