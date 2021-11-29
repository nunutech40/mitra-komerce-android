/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.android.kmabsensi.utils.komboard

import android.annotation.SuppressLint
import android.content.*
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.IBinder
import android.text.InputType
import android.text.method.MetaKeyKeyListener
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

import kotlinx.android.synthetic.main.cek_ongkir.view.*
import kotlinx.android.synthetic.main.cek_resi.view.*
import kotlinx.android.synthetic.main.item_detail_resi.view.*
import android.text.Editable

import android.text.TextWatcher
import kotlinx.android.synthetic.main.item_ekspedisi.view.*
import android.view.*
import com.nex3z.notificationbadge.NotificationBadge
import java.text.SimpleDateFormat
import java.util.*
import android.widget.EditText

import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import android.app.Activity
import android.text.Layout
import android.util.DisplayMetrics
import android.util.Log
import kotlinx.android.synthetic.main.item_detail_resi.*
import android.view.Gravity

import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.google.gson.Gson
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.ApiClient
import id.android.kmabsensi.data.remote.body.komship.LeadsParams
import id.android.kmabsensi.data.remote.response.komship.KomCreateLeadsResponse
import id.android.kmabsensi.data.remote.response.komship.KomPartnerResponse
import id.android.kmabsensi.data.remote.response.komship.LeadsCountResponse
import id.android.kmabsensi.data.remote.service.ApiService
import id.android.kmabsensi.utils.gone
import id.android.kmabsensi.utils.komboard.keyboard.CandidateView
import id.android.kmabsensi.utils.komboard.keyboard.LatinKeyboard
import id.android.kmabsensi.utils.komboard.keyboard.LatinKeyboardView
import id.android.kmabsensi.utils.visible
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
class CustomKeyboard : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    // ListAdapter.ItemAdapterCallback, ListEkspedisiAdapter.ItemAdapterCallback, QuantityListener

    private var mInputMethodManager: InputMethodManager? = null

    private val prefHelper: PreferencesHelper by inject()

    private var idPartner = 0
    private var userId: Int? = null

    private var mInputView: LatinKeyboardView? = null
    private var mCandidateView: CandidateView? = null
    private var mCompletions: Array<CompletionInfo>? = null

    private val mComposing = StringBuilder()
    private var mPredictionOn: Boolean = false
    private var mCompletionOn: Boolean = false
    private var mLastDisplayWidth: Int = 0
    private var mCapsLock: Boolean = false
    private var mLastShiftTime: Long = 0
    private var mMetaState: Long = 0

    private var mSymbolsKeyboard: LatinKeyboard? = null
    private var mSymbolsShiftedKeyboard: LatinKeyboard? = null
    private var mQwertyKeyboard: LatinKeyboard? = null
    private var mNumberKeyboard: LatinKeyboard? = null
    private var mCalculator: LatinKeyboard? = null

    private var mCurKeyboard: LatinKeyboard? = null

    private var wordSeparators: String? = null
    lateinit var bsBehavior: BottomSheetBehavior<ScrollView>


    //RESI
    private lateinit var layoutResultCalculator: LinearLayout
    private lateinit var layoutCekResi: ConstraintLayout
    private lateinit var bottomSheet: ScrollView
    private lateinit var layoutDetailResi: ConstraintLayout
    private lateinit var keyboard: LatinKeyboardView
    private lateinit var rvCekResi: RecyclerView

    //    private val list: ArrayList<Model> = arrayListOf()
    private lateinit var listAdapter: ListAdapter
    private lateinit var btnCekResi: Button
    private lateinit var btnCekResiDetail: Button
    private lateinit var btnBackResi: AppCompatImageView
    private lateinit var btnBackResiDetail: ImageView
    private lateinit var edtNameOrResi: EditText
    private lateinit var ivKembali: AppCompatImageView
    private lateinit var hostActivity: Activity

    //ONGKIR
    private lateinit var edtAddress: EditText
    private lateinit var edtKecamatan: EditText
    private lateinit var edtBerat: EditText
    private lateinit var btnCekOngkir: Button
    private lateinit var btnSalinOngkir: Button
    private lateinit var rvCekOngkir: RecyclerView
    private lateinit var tvPilihSemua: Button

    //    private var listEkspedisi: ArrayList<Ekspedisi> = arrayListOf()
//    private lateinit var ekspedisiAdapter: ListEkspedisiAdapter
    private lateinit var layoutCekOngkir: ScrollView
    private lateinit var dataMultiCheckBox: String
    private lateinit var ibCheckDoneOngkir: ImageButton

    private lateinit var btnTes: Button

    //Leads
    private lateinit var notifLeads: NotificationBadge
    private var apiService: ApiService? = null
    private lateinit var dataLocal: KomPartnerResponse

    //Emoticon
//    private var popupWindow: EmojiconsPopup? = null

    //Calculator
    private lateinit var btnCopyResultCalculator: ImageButton
    private lateinit var edtCalculator: EditText
    private var inputConnection: InputConnection? = null


    private val token: IBinder?
        get() {
            val dialog = window ?: return null
            val window = dialog.window ?: return null
            return window.attributes.token
        }

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    override fun onCreate() {
        //Jalan saat setelah memilih Komboard
        super.onCreate()
        mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        wordSeparators = resources.getString(R.string.word_separators)
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    override fun onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            val displayWidth = maxWidth
            if (displayWidth == mLastDisplayWidth) return
            mLastDisplayWidth = displayWidth
        }
        mQwertyKeyboard = LatinKeyboard(this, R.xml.qwerty)
        mSymbolsKeyboard = LatinKeyboard(this, R.xml.symbols)
        mSymbolsShiftedKeyboard = LatinKeyboard(this, R.xml.symbols_shift)
        mNumberKeyboard = LatinKeyboard(this, R.xml.numbers)
        mCalculator = LatinKeyboard(this, R.xml.calculator)
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    override fun onCreateInputView(): View {
        //Jalan saat pertama kali showSoftInput

        val root: View = layoutInflater.inflate(R.layout.input, null)
        keyboard = root.findViewById(R.id.keyboard)
        notifLeads = root.findViewById(R.id.notif_badge)
        apiService = ApiClient.client?.create(ApiService::class.java)
//        val keyboard2 = root.findViewById<LatinKeyboardView>(R.id.keyboard2)
        layoutCekResi = root.findViewById(R.id.layout_cek_resi)
        layoutDetailResi = layoutCekResi.findViewById(R.id.layout_detail_resi)
        layoutCekOngkir = root.findViewById<ScrollView>(R.id.layout_cek_ongkir)

        userId = prefHelper.getInt(PreferencesHelper.ID_USER.toString())

        layoutResultCalculator = root.findViewById(R.id.ll_result_calculator)
        edtCalculator = root.findViewById(R.id.edt_calculator)
        btnCopyResultCalculator = root.findViewById(R.id.ib_salin_calculator)
        btnCopyResultCalculator.setOnClickListener { customToast(applicationContext, "copied") }

        // get count leads
        dataLocal =
            Gson().fromJson<KomPartnerResponse>(
                prefHelper.getString(PreferencesHelper.DATA_PARTNER),
                KomPartnerResponse::class.java
            )
        idPartner = if (dataLocal.data!!.size > 1) {
            prefHelper.getInt(PreferencesHelper.ID_PARTNER.toString())
        } else {
            dataLocal.data!![0].partnerId!!
        }

        //getTotalLeads
        getTotalLeads()

        //toTopMenu
        val llCekResi = root.findViewById<LinearLayout>(R.id.ll_cek_resi)
        val llOrderku = root.findViewById<LinearLayout>(R.id.ll_orderku)
        val llCekOngkir = root.findViewById<LinearLayout>(R.id.ll_cek_ongkir)
        val llLeads = root.findViewById<LinearLayout>(R.id.ll_leads)

        llCekResi.setOnClickListener {
//            layoutCekResi.visible()
//            keyboard.gone()
//            layoutDetailResi.gone()
//            ivKembali.gone()
//            edtNameOrResi.requestFocus()
            customToast(applicationContext, "Coming Soon.")
            val motionEvent = MotionEvent.ACTION_DOWN
//            dispatchTouchEvent()

//            registerEditText(R.id.edt_name_or_resi)
        }

        // ORDERKU
        llOrderku.setOnClickListener {
//            send intent to BackIntentService for launch application from letter
            Intent(this, BackIntentService::class.java).also {
                startService(it)
            }
        }

        //Cek Ongkir
        llCekOngkir.setOnClickListener {
//            keyboard.visibility = View.VISIBLE
//            layoutCekResi.visibility = View.GONE
//            layoutCekOngkir.visibility = View.VISIBLE
//            btnCekOngkir.visibility = View.VISIBLE
//            btnSalinOngkir.visibility = View.GONE
//            tvPilihSemua.visibility = View.GONE
            customToast(applicationContext, "Coming Soon.")
        }

        //LEADS
        llLeads.setOnClickListener {
//            Toast.makeText(applicationContext, userId.toString(), Toast.LENGTH_SHORT).show()
            val formatDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateTime = formatDateTime.format(Date())
            val leads = LeadsParams(
                idUser = userId!!,
                idPartner = idPartner,
                dateLeads = dateTime
            )
            if (idPartner == 0) {
                customToast(applicationContext, "Silahkan pilih partner di Komboard")
            } else {
                createLeadsOrder(leads)
            }
        }

        //RESI  //Initial
//        listAdapter = ListAdapter(list, this)
        rvCekResi = layoutCekResi.findViewById(R.id.rv_cek_resi)
        btnBackResi = layoutCekResi.findViewById(R.id.iv_back)
        btnBackResiDetail = layoutCekResi.findViewById<ImageView>(R.id.iv_back_detail)
        btnCekResi = layoutCekResi.findViewById(R.id.btn_cek_resi)
        btnCekResiDetail = layoutCekResi.findViewById(R.id.btn_cek_resi_detail)
        edtNameOrResi = layoutCekResi.findViewById(R.id.edt_name_or_resi)
        ivKembali = layoutCekResi.findViewById(R.id.iv_kembali)

        edtNameOrResi.setOnTouchListener { v, event -> // your code here....
            keyboard.visibility = View.VISIBLE
            ivKembali.visibility = View.VISIBLE
            btnCekResi.visible()

            return@setOnTouchListener false
        }

        edtNameOrResi.addTextChangedListener(cekResiTextWatcher)

        ivKembali.setOnClickListener {
            keyboard.visibility = View.GONE
            ivKembali.visibility = View.GONE
        }

        btnBackResi.setOnClickListener {
            layoutCekResi.visibility = View.GONE
            rvCekResi.visibility = View.VISIBLE
            edtNameOrResi.setText("")
            btnCekResi.background = getDrawable(R.drawable.bg_button_disable_komboard)
            edtNameOrResi.addTextChangedListener(cekResiTextWatcher)
            keyboard.visibility = View.VISIBLE
            layoutDetailResi.visibility = View.GONE
            focus

//            listAdapter.cleanData()
            rvCekResi.background = null
          }

//        btnBackResiDetail.setOnClickListener {
//            layoutCekResi.visibility = View.GONE
//            keyboard.visibility = View.VISIBLE
//            layoutDetailResi.visibility = View.GONE
//            bottomSheet.visibility = View.GONE
////            Toast.makeText(it.context, "ok", Toast.LENGTH_SHORT).show()
//        }

//        btnCekResi.setOnClickListener {
//            getData()
////            btnCekResi.isEnabled = false
//        }

        //ONGKIR
//        ekspedisiAdapter = ListEkspedisiAdapter(listEkspedisi, this, this)
        val btnBackOngkir = layoutCekOngkir.findViewById<AppCompatImageView>(R.id.iv_back_ongkir)
        edtAddress = layoutCekOngkir.findViewById<EditText>(R.id.edt_alamat_asal)
        edtKecamatan = layoutCekOngkir.findViewById<EditText>(R.id.edt_kecamatan)
        edtBerat = layoutCekOngkir.findViewById<EditText>(R.id.edt_berat)
        btnCekOngkir = layoutCekOngkir.findViewById<Button>(R.id.btn_cek_ongkir)
        btnSalinOngkir = layoutCekOngkir.findViewById<Button>(R.id.btn_salin_ongkir)
        tvPilihSemua = layoutCekOngkir.findViewById(R.id.tv_pilih_semua)
        rvCekOngkir = layoutCekOngkir.findViewById(R.id.rv_list_ekspedisi)
        ibCheckDoneOngkir = layoutCekOngkir.findViewById(R.id.ib_check_done_address)

        btnTes = layoutCekOngkir.findViewById(R.id.btn_tes)

//        edtAddress.addTextChangedListener(cekOngkirTextWatcher)
//        edtKecamatan.addTextChangedListener(cekOngkirTextWatcher)
//        edtBerat.addTextChangedListener(cekOngkirTextWatcher)
        edtAddress.setOnTouchListener { _, _ -> // your code here....
            keyboard.visibility = View.VISIBLE
            ibCheckDoneOngkir.visibility = View.VISIBLE
            return@setOnTouchListener false
        }
        edtKecamatan.setOnTouchListener { _, _ -> // your code here....
            keyboard.visibility = View.VISIBLE
            ibCheckDoneOngkir.visibility = View.VISIBLE
            return@setOnTouchListener false
        }
        edtBerat.setOnTouchListener { _, _ -> // your code here....
            keyboard.visibility = View.VISIBLE
            ibCheckDoneOngkir.visibility = View.VISIBLE
            return@setOnTouchListener false
        }
//        val btnSalinOngkir = bottomSheet.findViewById<Button>(R.id.btn_cek_ongkir)
        btnCekOngkir.setOnClickListener {
//            getEkspedisi()
            btnCekOngkir.visibility = View.GONE
            btnSalinOngkir.visibility = View.VISIBLE
        }
        btnBackOngkir.setOnClickListener {
//            bsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            layoutCekOngkir.visibility = View.GONE
            keyboard.visibility = View.VISIBLE
//            ekspedisiAdapter.cleanData()
            btnCekOngkir.isEnabled = true
//            btnCekOngkir.background = getDrawable(R.drawable.bg_button_enable)
            btnCekOngkir.text = "Cek Ongkir"
//            tvPilihSemua.isChecked = false
        }
        ibCheckDoneOngkir.setOnClickListener {
            keyboard.visibility = View.GONE
            ibCheckDoneOngkir.visibility = View.GONE
        }
        edtAddress.isFocusableInTouchMode = true
        keyboard.visibility = View.VISIBLE

        //Input
//        mInputView = layoutInflater.inflate(R.layout.input, null) as LatinKeyboardView
        mInputView = root.findViewById(R.id.keyboard) as LatinKeyboardView
        mInputView!!.setOnKeyboardActionListener(this)
        setLatinKeyboard(mQwertyKeyboard)
        return root
//        return mInputView as LatinKeyboardView
    }

    var focus: View.OnFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            v.isFocusableInTouchMode = false
        }
    }

    private fun createLeadsOrder(leads: LeadsParams) {
        val body = mapOf(
            "user_id" to leads.idUser,
            "partner_id" to leads.idPartner,
            "date_leads" to leads.dateLeads
        )
        val createLeads: Call<KomCreateLeadsResponse>? =
            apiService?.createLeads(
                "Bearer " + prefHelper.getString(PreferencesHelper.ACCESS_TOKEN_KEY),
                body
            )
        createLeads?.enqueue(object : Callback<KomCreateLeadsResponse> {
            override fun onResponse(
                call: Call<KomCreateLeadsResponse>,
                response: Response<KomCreateLeadsResponse>
            ) {
                val result = response.body()?.data?.total_leads
                result?.let { notifLeads.setNumber(it) }
                Log.d("Create api", "onResponse: $result")
            }

            override fun onFailure(call: Call<KomCreateLeadsResponse>, t: Throwable) {
                Log.e("Create api", "onFailure: ${t.message}")
            }

        })
    }

    private fun getTotalLeads() {
        val totals: Call<LeadsCountResponse>? =
            userId?.let {
                apiService?.getCountLeads(
                    "Bearer " + prefHelper.getString(PreferencesHelper.ACCESS_TOKEN_KEY),
                    it, idPartner
                )
            }
        totals?.enqueue(object : Callback<LeadsCountResponse> {
            override fun onResponse(
                call: Call<LeadsCountResponse>,
                response: Response<LeadsCountResponse>
            ) {
                val count = response.body()?.data?.total_leads!!
                notifLeads.setNumber(count)
                Log.d("Leads API", "onResponse: $count")
            }

            override fun onFailure(call: Call<LeadsCountResponse>, t: Throwable) {

            }
        })
    }

    fun isCustomKeyboardVisible(): Boolean {
        return keyboard.visibility == View.VISIBLE
    }

    fun showCustomKeyboard(v: View?) {
        keyboard.visibility = View.VISIBLE
        keyboard.isEnabled = true
        if (v != null) (applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun hideCustomKeyboard() {
        keyboard.visibility = View.GONE
        keyboard.isEnabled = false
    }

    @SuppressLint("ClickableViewAccessibility")
    fun registerEditText(resid: Int) {
        val edittext: EditText = layoutCekResi.findViewById(resid)
        edittext.setOnFocusChangeListener { view, b ->
            if (b) {
                showCustomKeyboard(view)
            } else {
                hideCustomKeyboard()
            }
        }

        edittext.setOnClickListener {
            showCustomKeyboard(it)
        }

        edittext.setOnTouchListener { view, motionEvent ->
            if (!(isCustomKeyboardVisible())) {
                showCustomKeyboard(view)
            }

            val edittext = view as EditText
            val inType = edittext.inputType
            edittext.inputType = InputType.TYPE_NULL
            edittext.onTouchEvent(motionEvent)
            edittext.inputType = inType
            return@setOnTouchListener true
        }

        edittext.inputType = edittext.inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        val otl = View.OnTouchListener()
        { view: View, motionEvent: MotionEvent ->
            if (!(isCustomKeyboardVisible())) {
                showCustomKeyboard(view)
            }
            val layout: Layout
            val x: Float
            val offset: Int
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    layout = (view as EditText).layout
                    x = motionEvent.x + edittext.scrollX
                    offset = layout.getOffsetForHorizontal(0, x)
                    if (offset > 0) {
                        if (x > layout.getLineMax(0)) {
                            edittext.setSelection(offset)
                        } else {
                            edittext.setSelection(offset - 1)
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    layout = (view as EditText).layout
                    x = motionEvent.x + edittext.scrollX
                    offset = layout.getOffsetForHorizontal(0, x)
                    if (offset > 0) {
                        if (x > layout.getLineMax(0)) {
                            edittext.setSelection(offset)
                        } else {
                            edittext.setSelection(offset - 1)
                        }
                    }
                }
            }
            return@OnTouchListener true
        }

        edittext.setOnTouchListener(otl)
    }

//    private fun getEkspedisi() {
//        rvCekOngkir.setHasFixedSize(true)
////        listEkspedisi.addAll(DataDummy.listEkspedisi)
//        rvCekOngkir.layoutManager = LinearLayoutManager(this)
////        rvCekOngkir.adapter = ekspedisiAdapter
//        tvPilihSemua.visibility = View.VISIBLE
//        btnSalinOngkir.isEnabled = true
//        btnSalinOngkir.background = getDrawable(R.drawable.bg_button_disable)
//        btnSalinOngkir.setOnClickListener { customToast(it.context, "Silahkan pilih ekspedisi") }
//        tvPilihSemua.setOnClickListener {
////            ekspedisiAdapter.selectAll()
////            if(!ekspedisiAdapter.selectAll().equals(true)){
////                layoutCekOngkir.btn_salin_ongkir.background = getDrawable(R.drawable.bg_button_enable)
////            }
//            layoutCekOngkir.btn_salin_ongkir.background = getDrawable(R.drawable.bg_button_enable)
//            layoutCekOngkir.btn_salin_ongkir.setOnClickListener {
//                val addressFrom = edtAddress.text.toString().trim()
//                val addressTo = edtKecamatan.text.toString().trim()
//                val berat = edtBerat.text.toString().trim()
//                val ekspedisi = listEkspedisi.toString()
//                val copyText =
//                    "Alamat asal:\n${addressFrom}\n\nAlamat tujuan:\n${addressTo}\n\nBerat:${berat}kg\nOngkir: $ekspedisi"
//                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clip = ClipData.newPlainText("text", copyText)
//                clipboard.setPrimaryClip(clip)
//                customToast(applicationContext, "Ongkir berhasil disalin")
//            }
//        }
//    }

    private val cekResiTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val nameOrResi: String = edtNameOrResi.text.toString().trim()
            if (nameOrResi.isNotEmpty()){
                btnBackResi.isEnabled = true
                btnCekResi.background = getDrawable(R.drawable.background_orange_10dp)
            } else {
                btnBackResi.isEnabled = false
                btnCekResi.background = getDrawable(R.drawable.bg_button_disable_komboard)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val cekResiEdittext: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            edtNameOrResi.setText(s.toString())
        }

        override fun afterTextChanged(s: Editable) {}
    }

//    private val cekOngkirTextWatcher: TextWatcher = object : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            val alamat: String = edtAddress.text.toString().trim()
//            val kecamatan: String = edtKecamatan.text.toString().trim()
//            val berat: String = edtBerat.text.toString().trim()
//            if (alamat.isNotEmpty() && kecamatan.isNotEmpty() && berat.isNotEmpty()) {
//                btnCekOngkir.isEnabled = true
//                btnCekOngkir.background = getDrawable(R.drawable.bg_button_enable)
//            }
//        }
//
//        override fun afterTextChanged(s: Editable) {}
//    }

//    override fun onQuantityChange(v: View, arrayList: ArrayList<String>) {
//        val addressFrom = edtAddress.text.toString().trim()
//        val addressTo = edtKecamatan.text.toString().trim()
//        val berat = edtBerat.text.toString().trim()
//        dataMultiCheckBox = arrayList.toString()
////        val ekspedisi = "${data.nameEkspedisi}: ${data.biaya}"
//        var ekspedisi = arrayList.toString()
//        ekspedisi = ekspedisi.replace("\\[".toRegex(), "")
//        ekspedisi = ekspedisi.replace("]".toRegex(), "")
//        val copyText =
//            "Alamat asal:\n${addressFrom}\n\nAlamat tujuan:\n${addressTo}\n\nBerat:${berat}kg\nOngkir: $ekspedisi"
//        val clip = ClipData.newPlainText("text", copyText)
//        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//
//        if (v.check_box_ekspedisi.isChecked) {
//            layoutCekOngkir.btn_salin_ongkir.background = getDrawable(R.drawable.bg_button_enable)
//            layoutCekOngkir.btn_salin_ongkir.setOnClickListener {
//                clipboard.setPrimaryClip(clip)
//                customToast(applicationContext, "Ongkir berhasil disalin")
//            }
//        }
//        else if (!v.check_box_ekspedisi.isChecked) {
//            if (ekspedisi.isNotEmpty()){
//                layoutCekOngkir.btn_salin_ongkir.background = getDrawable(R.drawable.bg_button_enable)
//                layoutCekOngkir.btn_salin_ongkir.setOnClickListener {
//                    clipboard.setPrimaryClip(clip)
//                    customToast(applicationContext, "Ongkir berhasil disalin")
//                }
//            }else {
//                layoutCekOngkir.btn_salin_ongkir.background = getDrawable(R.drawable.bg_button_disable)
//                tvPilihSemua.isClickable = true
//                layoutCekOngkir.btn_salin_ongkir.setOnClickListener {
//                    val clip0 = ClipData.newPlainText("text", "")
//                    clipboard.setPrimaryClip(clip0)
//                    customToast(v.context, "Silahkan pilih ekspedisi")
//                }
//            }
//        }
//    }

    //Click Pilih Data Resi
//    override fun onListClick(v: View, data: Model) {
////        Toast.makeText(v.context, data.date, Toast.LENGTH_SHORT).show()
//        rvCekResi.visibility = View.GONE
//        layoutDetailResi.visibility = View.VISIBLE
////        btnBackResi.visibility = View.GONE
////        btnCekResi.visibility = View.GONE
//
//        //get and setData
//        val resi = "${data.resi} (${data.name})"
//        val date = data.date
//        val status = data.status
//        layoutCekResi.edt_name_or_resi.setText("${data.name}, $date")
//        layoutDetailResi.tv_resi.text = resi
//        layoutDetailResi.tv_day_resi.text = date
//        layoutDetailResi.tv_status_resi.text = status
//
//        //btn in Detail Resi
////        btnBackResiDetail.visibility = View.VISIBLE
////        btnCekResiDetail.visibility = View.VISIBLE
////        btnCekResiDetail.background = getDrawable(R.drawable.bg_button_enable)
//
//        //CopyText
//        val copyDetailResi = "No resi: $resi\n\n$date,\n$status"
//        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        layoutDetailResi.ib_salin_resi.setOnClickListener {
//            val clip = ClipData.newPlainText("text", copyDetailResi)
//            clipboard.setPrimaryClip(clip)
//            customToast(v.context, "Berhasil disalin")
//        }
//    }

//    private fun getData() {
//        rvCekResi.background = getDrawable(R.drawable.bg_button_keyboard)
//        rvCekResi.setHasFixedSize(true)
//
//        list.addAll(DataDummy.listData)
//        rvCekResi.layoutManager = LinearLayoutManager(this)
//        rvCekResi.adapter = listAdapter
//    }

    private fun cekResiActions() {
        val view = layoutInflater.inflate(R.layout.cek_resi, null)
        val dialog = BottomSheetDialog(view.context)
        dialog.setContentView(view)
        try {
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }

//        CustomDialog().apply { this.fragmentManager?.let { show(it, CustomDialog.TAG) } }
//        Toast.makeText(applicationContext, "Resi", Toast.LENGTH_SHORT).show()
    }

    private fun customToast(context: Context, s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    private fun setLatinKeyboard(nextKeyboard: LatinKeyboard?) {
        val shouldSupportLanguageSwitchKey =
            mInputMethodManager!!.shouldOfferSwitchingToNextInputMethod(token)
//        nextKeyboard!!.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey)
        mInputView!!.keyboard = nextKeyboard
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like [.onCreateInputView].
     */
    override fun onCreateCandidatesView(): View {
        mCandidateView = CandidateView(this)
        mCandidateView!!.setService(this)
        return mCandidateView as CandidateView
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    override fun onStartInput(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInput(attribute, restarting)

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0)
//        updateCandidates()

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0
        }

        mPredictionOn = false
        mCompletionOn = false
        mCompletions = null

        // We are now going to initialize our state based on the type of
        // text being edited.
        when (attribute.inputType and InputType.TYPE_MASK_CLASS) {
            InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_DATETIME ->
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard

            InputType.TYPE_CLASS_PHONE ->
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mNumberKeyboard

            InputType.TYPE_CLASS_TEXT -> {
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard
                mPredictionOn = true

                // We now look for a few special variations of text that will
                // modify our behavior.
                val variation = attribute.inputType and InputType.TYPE_MASK_VARIATION
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    || variation == InputType.TYPE_TEXT_VARIATION_URI
                    || variation == InputType.TYPE_TEXT_VARIATION_FILTER
                ) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false
                }

                if (attribute.inputType and InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false
                    mCompletionOn = isFullscreenMode
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute)
            }

            else -> {
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard
                updateShiftKeyState(attribute)
            }
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard!!.setImeOptions(resources, attribute.imeOptions)
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    override fun onFinishInput() {
        super.onFinishInput()

        // Clear current composing text and candidates.
        mComposing.setLength(0)
//        updateCandidates()

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false)

        mCurKeyboard = mQwertyKeyboard
        if (mInputView != null) {
            mInputView!!.closing()
        }
    }

    override fun onStartInputView(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        // Apply the selected keyboard to the input view.
        getTotalLeads()
        setLatinKeyboard(mCurKeyboard)
//        closeEmoticons()
        mInputView!!.closing()
        val subtype = mInputMethodManager!!.currentInputMethodSubtype
        mInputView!!.setSubtypeOnSpaceKey(subtype)
    }

    public override fun onCurrentInputMethodSubtypeChanged(subtype: InputMethodSubtype) {
        mInputView!!.setSubtypeOnSpaceKey(subtype)
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart, oldSelEnd, newSelStart, newSelEnd,
            candidatesStart, candidatesEnd
        )

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0)
//            updateCandidates()
            val ic = currentInputConnection
            ic?.finishComposingText()
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
//    override fun onDisplayCompletions(completions: Array<CompletionInfo>?) {
//        if (mCompletionOn) {
//            mCompletions = completions
//            if (completions == null) {
////                setSuggestions(null, false, false)
//                return
//            }
//
//            val stringList = ArrayList<String>()
//            for (i in completions.indices) {
//                val ci = completions[i]
//                if (ci != null) stringList.add(ci.text.toString())
//            }
////            setSuggestions(stringList, true, true)
//        }
//    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private fun translateKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        mMetaState = MetaKeyKeyListener.handleKeyDown(
            mMetaState,
            keyCode, event
        )
        var c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState))
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState)
        val ic = currentInputConnection
        if (c == 0 || ic == null) {
            return false
        }

        var dead = false

        if (c and KeyCharacterMap.COMBINING_ACCENT != 0) {
            dead = true
            c = c and KeyCharacterMap.COMBINING_ACCENT_MASK
        }

        if (mComposing.length > 0) {
            val accent = mComposing[mComposing.length - 1]
            val composed = KeyEvent.getDeadChar(accent.toInt(), c)

            if (composed != 0) {
                c = composed
                mComposing.setLength(mComposing.length - 1)
            }
        }

        onKey(c, null)

        return true
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK ->
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.repeatCount == 0 && mInputView != null) {
                    if (mInputView!!.handleBack()) {
                        return true
                    }
                }

            KeyEvent.KEYCODE_DEL ->
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null)
                    return true
                }

            KeyEvent.KEYCODE_ENTER ->
                // Let the underlying text editor always handle these.
                return false

            else ->
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE && event.metaState and KeyEvent.META_ALT_ON != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        val ic = currentInputConnection
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON)
                            keyDownUp(KeyEvent.KEYCODE_A)
                            keyDownUp(KeyEvent.KEYCODE_N)
                            keyDownUp(KeyEvent.KEYCODE_D)
                            keyDownUp(KeyEvent.KEYCODE_R)
                            keyDownUp(KeyEvent.KEYCODE_O)
                            keyDownUp(KeyEvent.KEYCODE_I)
                            keyDownUp(KeyEvent.KEYCODE_D)
                            // And we consume this event.
                            return true
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true
                    }
                }
        }

        return super.onKeyDown(keyCode, event)
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(
                    mMetaState,
                    keyCode, event
                )
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private fun commitTyped(inputConnection: InputConnection) {
        if (mComposing.length > 0) {
            inputConnection.commitText(mComposing, mComposing.length)
            mComposing.setLength(0)
//            updateCandidates()
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private fun updateShiftKeyState(attr: EditorInfo?) {
        if (attr != null
            && mInputView != null && mQwertyKeyboard === mInputView!!.keyboard
        ) {
            var caps = 0
            val ei = currentInputEditorInfo
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = currentInputConnection.getCursorCapsMode(attr.inputType)
            }
            mInputView!!.isShifted = mCapsLock || caps != 0

            // Change Shift key icon - 2
            updateShiftIcon()
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.md
     */
    private fun isAlphabet(code: Int): Boolean {
        return Character.isLetter(code)
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private fun keyDownUp(keyEventCode: Int) {
        currentInputConnection.sendKeyEvent(
            KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode)
        )
        currentInputConnection.sendKeyEvent(
            KeyEvent(KeyEvent.ACTION_UP, keyEventCode)
        )
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private fun sendKey(keyCode: Int) {
        when (keyCode) {
            '\n'.toInt() -> keyDownUp(KeyEvent.KEYCODE_ENTER)
            else -> if (keyCode >= '0'.toInt() && keyCode <= '9'.toInt()) {
                keyDownUp(keyCode - '0'.toInt() + KeyEvent.KEYCODE_0)
//                keyDownUp(keyCode)
            } else {
                currentInputConnection.commitText(keyCode.toChar().toString(), 1)
            }
        }
    }

    // Implementation of KeyboardViewListener

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {

        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (mComposing.length > 0) {
                commitTyped(currentInputConnection)
            }
            sendKey(primaryCode)
            updateShiftKeyState(currentInputEditorInfo)
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace()
        } else if (primaryCode == LatinKeyboardView.KEYCODE_CALCULATOR) {
            handleCalculator()
        } else if (primaryCode == LatinKeyboardView.KEYCODE_BUTTON_CEK_RESI) {
            handleButtonCekResi()
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift()
            return
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose()
            return
        } else if (primaryCode == LatinKeyboardView.KEYCODE_EMOTICON) {
//            handleEmoticon()
            return
        } else if (primaryCode == LatinKeyboardView.KEYCODE_CHECKLIST) {
            handleClose()
            return
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch()
            return
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or something
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mInputView != null) {
            val current = mInputView!!.keyboard
            if (current === mSymbolsKeyboard || current === mSymbolsShiftedKeyboard) {
                setLatinKeyboard(mQwertyKeyboard)
                layoutResultCalculator.visibility = View.GONE
                updateShiftIcon()
            } else {
                setLatinKeyboard(mSymbolsKeyboard)
                mSymbolsKeyboard!!.isShifted = false
                layoutResultCalculator.visibility = View.GONE
            }
        } else {
            handleCharacter(primaryCode, keyCodes)
        }
    }

    private fun handleButtonCekResi() {
        Toast.makeText(applicationContext, "Test", Toast.LENGTH_SHORT).show()
    }

    private fun updateShiftIcon() {
        val now = System.currentTimeMillis()
        val keys = mQwertyKeyboard!!.keys
        var currentKey: Keyboard.Key
        for (i in 0 until keys.size - 1) {
            currentKey = keys[i]
            mInputView!!.invalidateAllKeys()
            if (currentKey.codes[0] == -1) {
                currentKey.label = null
                when {

                    //capital tetap
                    mCapsLock-> {
                        currentKey.icon = resources.getDrawable(R.drawable.caps_lock)
                    }

                    //state capital if change mode to symbol
                    mInputView!!.isShifted -> {
                        currentKey.icon = resources.getDrawable(R.drawable.icon_shifts_filll)
                    }

                    //state is not shift
                    !mInputView!!.isShifted -> {
                        currentKey.icon = resources.getDrawable(R.drawable.icon_shifts)
                    }

                    //capital 1x
                    mLastShiftTime == now -> {
                        currentKey.icon = resources.getDrawable(R.drawable.icon_shifts_filll)
                    }

                   // capital 1x saat pertama pilih komboard
                    mLastShiftTime == now -1 -> {
                        currentKey.icon = resources.getDrawable(R.drawable.icon_shifts_filll)
                    }

                    else -> {
                        currentKey.icon = resources.getDrawable(R.drawable.icon_shifts)
                    }
                }
                break
            }
        }
    }


    override fun onText(text: CharSequence) {
        val ic = currentInputConnection ?: return
        ic.beginBatchEdit()
        if (mComposing.length > 0) {
            commitTyped(ic)
        }
        ic.commitText(text, 0)
        ic.endBatchEdit()
        updateShiftKeyState(currentInputEditorInfo)
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
//    private fun updateCandidates() {
//        if (!mCompletionOn) {
//            if (mComposing.length > 0) {
//                val list = ArrayList<String>()
//                list.add(mComposing.toString())
////                setSuggestions(list, true, true)
//            } else {
////                setSuggestions(null, false, false)
//            }
//        }
//    }

    fun setSuggestions(
        suggestions: List<String>?, completions: Boolean,
        typedWordValid: Boolean
    ) {
        if (suggestions != null && suggestions.size > 0) {
            setCandidatesViewShown(true)
        } else if (isExtractViewShown) {
            setCandidatesViewShown(true)
        }
        if (mCandidateView != null) {
            mCandidateView!!.setSuggestions(suggestions, completions, typedWordValid)
        }
    }

    private fun handleBackspace() {
        val length = mComposing.length
        when {
            length > 1 -> {
                mComposing.delete(length - 1, length)
                currentInputConnection.setComposingText(mComposing, 1)
                //            updateCandidates()
            }
            length > 0 -> {
                mComposing.setLength(0)
                currentInputConnection.commitText("", 0)
                //            updateCandidates()
            }
            else -> {
                keyDownUp(KeyEvent.KEYCODE_DEL)
            }
        }
        updateShiftKeyState(currentInputEditorInfo)
    }

//    fun handleEmoticon() {
//        val layoutInflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        if (layoutInflater != null) {
//            val popupView: View = layoutInflater.inflate(R.layout.emoji_listview_layout, null)
//            popupWindow = EmojiconsPopup(popupView, this)
//            popupWindow!!.setSizeForSoftKeyboard()
////            // get device dimensions
//            val displayMetrics = DisplayMetrics()
//            val windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//            windowManager.defaultDisplay?.getMetrics(displayMetrics)
//            val height = displayMetrics.heightPixels
//            when {
//                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R -> {
//                    popupWindow!!.setSize(MATCH_PARENT, (height/3.2).toInt())
//                }
//                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q -> {
//                    popupWindow!!.setSize(MATCH_PARENT,height/4)
//                }
//                else -> {
//                    popupWindow!!.setSize(MATCH_PARENT, (height/3.2).toInt())
//                }
//            }
//
//            popupWindow!!.showAtLocation(mInputView!!.rootView, Gravity.BOTTOM, 0, 0)
//            // If the text keyboard closes, also dismiss the emoji popup
//            popupWindow!!.setOnSoftKeyboardOpenCloseListener(object :
//                EmojiconsPopup.OnSoftKeyboardOpenCloseListener {
//                override fun onKeyboardOpen(keyBoardHeight: Int) {}
//                override fun onKeyboardClose() {
//                    if (popupWindow!!.isShowing) popupWindow!!.dismiss()
//                }
//            })
//            popupWindow!!.setOnEmojiconClickedListener { emojicon ->
//                mComposing.append(emojicon.emoji)
//                commitTyped(currentInputConnection)
//            }
//            popupWindow!!.setOnEmojiconBackspaceClickedListener {
//                val event = KeyEvent(
//                    0,
//                    0,
//                    0,
//                    KeyEvent.KEYCODE_DEL,
//                    0,
//                    0,
//                    0,
//                    0,
//                    KeyEvent.KEYCODE_ENDCALL
//                )
//                handleBackspace()
//            }
//        }
//    }

    private fun handleCalculator() {
        customToast(applicationContext, "Coming Soon")
//        if (mInputView == null) {
//            return
//        }
//
//        val currentKeyboard = mInputView!!.keyboard
//        when {
//            mQwertyKeyboard === currentKeyboard -> {
//                setLatinKeyboard(mCalculator)
//                layoutResultCalculator.visibility = View.VISIBLE
//            }
//            currentKeyboard === mSymbolsKeyboard -> {
//                mSymbolsKeyboard!!.isShifted = true
//                setLatinKeyboard(mCalculator)
//                layoutResultCalculator.visibility = View.VISIBLE
//                mInputView!!.isShifted = mCapsLock || !mInputView!!.isShifted
//            }
//            currentKeyboard === mSymbolsShiftedKeyboard -> {
//                mSymbolsShiftedKeyboard!!.isShifted = false
//                setLatinKeyboard(mCalculator)
//                layoutResultCalculator.visibility = View.VISIBLE
//                mInputView!!.isShifted = mCapsLock || !mInputView!!.isShifted
//            }
//        }
    }

    private fun handleShift() {
        if (mInputView == null) {
            return
        }

        val currentKeyboard = mInputView!!.keyboard
        when {
            mQwertyKeyboard === currentKeyboard -> {
                // Alphabet keyboard
                checkToggleCapsLock()
                layoutResultCalculator.visibility = View.GONE
                mInputView!!.isShifted = mCapsLock || !mInputView!!.isShifted
            }
            currentKeyboard === mSymbolsKeyboard -> {
                mSymbolsKeyboard!!.isShifted = true
                setLatinKeyboard(mSymbolsShiftedKeyboard)
                layoutResultCalculator.visibility = View.GONE
                mSymbolsShiftedKeyboard!!.isShifted = true
            }
            currentKeyboard === mSymbolsShiftedKeyboard -> {
                mSymbolsShiftedKeyboard!!.isShifted = false
                setLatinKeyboard(mSymbolsKeyboard)
                layoutResultCalculator.visibility = View.GONE
                mSymbolsKeyboard!!.isShifted = false
            }
        }

        updateShiftIcon()
    }

    private fun handleCharacter(primaryCode: Int, keyCodes: IntArray?) {
        var primaryCode = primaryCode
        if (isInputViewShown) {
            if (mInputView!!.isShifted) {
                primaryCode = Character.toUpperCase(primaryCode)
            }
        }
        if (isAlphabet(primaryCode) && mPredictionOn) {
            mComposing.append(primaryCode.toChar())
            currentInputConnection.setComposingText(mComposing, 1)
            updateShiftKeyState(currentInputEditorInfo)
//            updateCandidates()
        } else {
            mComposing.append(primaryCode.toChar())
            currentInputConnection.setComposingText(mComposing, 1)
        }

        edtCalculator.setText(mComposing)
    }

    private fun handleClose() {
        commitTyped(currentInputConnection)
        requestHideSelf(0)
        mInputView!!.closing()
    }

    private fun handleLanguageSwitch() {
        mInputMethodManager!!.switchToNextInputMethod(token, false /* onlyCurrentIme */)
    }

    private fun checkToggleCapsLock() {
        val now = System.currentTimeMillis()
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock
            mLastShiftTime = 0
        } else {
            mLastShiftTime = now
        }
    }

    fun isWordSeparator(code: Int): Boolean {
        val separators = wordSeparators
        return separators!!.contains(code.toChar().toString())
    }

    fun pickDefaultCandidate() {
        pickSuggestionManually(0)
    }

    fun pickSuggestionManually(index: Int) {
        if (mCompletionOn && mCompletions != null && index >= 0
            && index < mCompletions!!.size
        ) {
            val ci = mCompletions!![index]
            currentInputConnection.commitCompletion(ci)
            if (mCandidateView != null) {
                mCandidateView!!.clear()
            }
            updateShiftKeyState(currentInputEditorInfo)
        } else if (mComposing.length > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            commitTyped(currentInputConnection)
        }
    }

    override fun swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate()
        }
    }

    override fun swipeLeft() {
        handleBackspace()
    }

    override fun swipeDown() {
        handleClose()
    }

    override fun swipeUp() {}

    override fun onPress(primaryCode: Int) {}

    override fun onRelease(primaryCode: Int) {

//        edtNameOrResi.setText(edtNameOrResi.text.toString() + primaryCode.toChar().toString())
        edtNameOrResi.setText(mComposing)
        edtAddress.setText(edtAddress.text.toString() + primaryCode.toChar().toString())
        edtKecamatan.setText(edtKecamatan.text.toString() + primaryCode.toChar().toString())
        edtBerat.setText(edtBerat.text.toString() + primaryCode.toChar().toString())
    }

    companion object {
        internal val DEBUG = false

        /**
         * This boolean indicates the optional example code for performing
         * processing of hard keys in addition to regular text generation
         * from on-screen interaction.  It would be used for input methods that
         * perform language translations (such as converting text entered on
         * a QWERTY keyboard to Chinese), but may not be used for input methods
         * that are primarily intended to be used for on-screen text entry.
         */
        internal val PROCESS_HARD_KEYS = true
    }

//    fun closeEmoticons() {
//        if (popupWindow != null) popupWindow!!.dismiss()
//    }
}

