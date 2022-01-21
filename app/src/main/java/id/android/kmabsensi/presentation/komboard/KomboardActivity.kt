package id.android.kmabsensi.presentation.komboard

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.gson.Gson
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.response.komship.KomPartnerItem
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.databinding.ActivityKomboardBinding
import id.android.kmabsensi.presentation.base.BaseActivityRf
import id.android.kmabsensi.utils.OnSingleCLick
import id.android.kmabsensi.utils.showDialogAfterChangePartner
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class KomboardActivity : BaseActivityRf<ActivityKomboardBinding>(
    ActivityKomboardBinding::inflate
) {

    private val prefHelper: PreferencesHelper by inject()
    private var dataPartner: MutableList<KomPartnerItem> = ArrayList()
    private var dataPartnerLocal: MutableList<KomPartnerItem> = ArrayList()

    //    private var dataPartnerLocal = ArrayList<KomPartnerItem>()
    private var sklPartner: SkeletonScreen? = null
    private var idPartner = 0
    private var namePartner = ""
    private var positionPartner = "0"
    private var positionPartnerInt: Int? = null
    private lateinit var dataLocal: KomPartnerResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar("Komboard", isBackable = true)
        setupView()
//        setupObserver()
        setupListener()

    }

    private fun setupObserver() {

    }

    private fun setupView() {

        if (prefHelper.getString(PreferencesHelper.DATA_PARTNER) ==  null) {
            Log.d("test", "error or null")
        } else {
            dataLocal =
                Gson().fromJson<KomPartnerResponse>(
                    prefHelper.getString(PreferencesHelper.DATA_PARTNER),
                    KomPartnerResponse::class.java
                )
        }

        dataLocal.data?.let { dataPartner.addAll(it) }
        setupSpinnerPartner(dataLocal.data)

    }

    private fun setupSpinnerPartner(data: List<KomPartnerItem>?) {
        val partner = ArrayList<String>()
        data?.forEach {
            partner.add(it.partnerName ?: "-")
        }

        ArrayAdapter(applicationContext, R.layout.spinner_item, partner)
            .also { adapter ->
                binding.spPartnerKomboard.prompt = "Partner (Mengatur input leads)"
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                binding.spPartnerKomboard.adapter = adapter

                binding.spPartnerKomboard.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val partnerPos = parent?.selectedItemPosition
                            Log.d("posisi", "onItemSelected: $partnerPos")
                            idPartner = dataPartner[position].partnerId!!
                            namePartner = dataPartner[position].partnerName.toString()

                            //save Local
                            partnerPos?.let {
                                prefHelper.saveString(
                                    PreferencesHelper.POSITION_PARTNER,
                                    it.toString()
                                )
                            }
                            prefHelper.saveInt(PreferencesHelper.ID_PARTNER.toString(), idPartner)
                            prefHelper.saveString(PreferencesHelper.NAME_PARTNER, namePartner)

                            if (partnerPos != positionPartnerInt) {
                                showDialogAfterChangePartner(
                                    this@KomboardActivity,
                                    object : OnSingleCLick {
                                        override fun onCLick() {
                                            val intentSetKomboard = Intent()
                                            intentSetKomboard.component =
                                                ComponentName(
                                                    this@KomboardActivity,
                                                    "id.android.kmabsensi.presentation.komboard.KomboardSettingActivity"
                                                )
                                            intentSetKomboard.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intentSetKomboard)
                                        }
                                    })
                            }
                        }
                    }
            }
    }

    private fun setupListener() {
        binding.apply {
            rlKomboard.setOnClickListener { startActivity<KomboardSettingActivity>() }
            binding.spPartnerKomboard.setSelection(1)
        }

        if (dataLocal.data!!.size > 1) {
            positionPartner = prefHelper.getString(PreferencesHelper.POSITION_PARTNER)
            if (positionPartner.isNullOrEmpty()){
                positionPartner = "0"
                toInteger(positionPartner)
                Log.d("Log baru", "setupView: $positionPartnerInt")
                positionPartnerInt?.let { binding.spPartnerKomboard.setSelection(it) }
            } else{
                toInteger(positionPartner)
                Log.d("Log baru", "setupView: $positionPartnerInt")
                positionPartnerInt?.let { binding.spPartnerKomboard.setSelection(it) }
            }
        }
        else {
            //Jumlah partner 1
            toInteger(positionPartner)
            positionPartnerInt?.let { binding.spPartnerKomboard.setSelection(it) }
        }
    }


    fun toInteger(s: String) {
        try {
            positionPartnerInt = s.toInt()
        } catch (ex: NumberFormatException) {
            ex.printStackTrace()
        }
    }

    private fun showSkeleton() {
        if (sklPartner == null) {
            sklPartner = Skeleton.bind(binding.spPartnerKomboard)
                .load(R.layout.skeleton_item_big)
                .show()
        } else {
            sklPartner?.show()
        }
    }
}