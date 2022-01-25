package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.kusu.loadingbutton.LoadingButton
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PrefData
import id.android.kmabsensi.data.pref.PrefData.Companion.KEYBOARD_TYPE
import id.android.kmabsensi.data.remote.ApiClientOngkir
import id.android.kmabsensi.data.remote.body.komboard.CostOngkirParams
import id.android.kmabsensi.data.remote.response.komboard.*
import id.android.kmabsensi.data.remote.service.ApiServiceKomboard
import id.android.kmabsensi.services.komboard.KomboardService
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Call
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class LayoutCekongkir : Service() {
    companion object{
    var daftarHarga = ""
    val hashHarga: HashMap<String, String> = HashMap<String, String>()
    }

    private lateinit var customView: ViewGroup
    private var LAYOUT_TYPE = 0
    private lateinit var floatWindowLayoutParam: WindowManager.LayoutParams
    private lateinit var windowManager: WindowManager
    private lateinit var inflater: LayoutInflater
    private lateinit var dataPref: PrefData

    //field cek Ongkir
    private lateinit var ed_Berat: EditText
    private lateinit var btn_pickAsal: LinearLayout
    private lateinit var btn_pickTujuan: LinearLayout
    private lateinit var tv_alamatAsal: TextView
    private lateinit var tv_alamatTujuan: TextView
    private lateinit var tv_topAsal: TextView
    private lateinit var tv_topTujuan: TextView
    private lateinit var btn_cekOngkir: LoadingButton
    private lateinit var layout_cekOngkir: LinearLayout

    //layout pick ekspedisi
    private lateinit var layout_pickEkspedisi: LinearLayout
    private lateinit var tvAddresCosting: TextView
    private lateinit var tvWeight: TextView
    private lateinit var rbGroup: RadioGroup
    private lateinit var rbJNE: RadioButton
    private lateinit var rbJNT: RadioButton
    private lateinit var rbSICEPAT: RadioButton
    private lateinit var layout_btnPickExpedisi: LinearLayout
    private lateinit var layout_button_cekOngkir: LinearLayout
    private lateinit var im_donePickExpedisi: ImageView
    private lateinit var btn_pickExpedisi: LoadingButton
    private lateinit var tv_pickExpedisi: TextView

    //layout copy ongkir
    private lateinit var layout_copyOngkir: LinearLayout
    private lateinit var btn_copyOngkir: LoadingButton
    private lateinit var im_doneCopy_ongkir: ImageView

    //field pick destination
    private lateinit var layout_pickAlamat: LinearLayout
    private lateinit var ed_provinsi: EditText
    private lateinit var ed_kabupaten: EditText
    private lateinit var ed_kecamatan: EditText
    private lateinit var btn_simpanAlamat: Button
    private lateinit var layout_button_pickAlamat: LinearLayout
    private lateinit var im_back: ImageView
    private lateinit var im_done: ImageView
    private var id_Prov: Int = 0
    private var id_Kab: Int = 0
    private var id_Kec: Int = 0

    //layout harga
    private lateinit var layout_harga: LinearLayout
    private lateinit var tv_harga: TextView

    //progressbar
    private lateinit var prog_prov: SpinKitView
    private lateinit var prog_kab: SpinKitView
    private lateinit var prog_kec: SpinKitView
//    private lateinit var prog_ongkir: SpinKitView

    //view addres
    private lateinit var recAddress: RecyclerView
    private var adapterAddress: AddresAdapter? = null
    private var adapterCity: CityAdapter? = null
    private var adapterDistrict: SubdistrictAdapter? = null
    private var adapterHargaOngkir: AdapterHargaOngkir? = null
    private var arrayAddress: MutableList<ProvinceResults> = ArrayList()
    private var arrayCity: MutableList<CityResults> = ArrayList()
    private var arrayDistrict: MutableList<SubDistrictResults> = ArrayList()

    //params
    private var apiService: ApiServiceKomboard?= null
    private var pickingAddres: Int? = 0

    //params cost ongkir
    private var originID: Int? = 0
    private var originType: String? = ""
    private var destinationID: Int? = 0
    private var destinationType: String? = ""
    private var originDistrict: String? = ""
    private var destinationDistrict: String? = ""
    private var weight: Int? = 0
    private var courier: String? = "jne"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customView = inflater.inflate(R.layout.layout_input_ongkir, null) as ViewGroup
        dataPref = PrefData(applicationContext)
        initializationItem(customView)
        checkingSDK()
        setupWindow()
        apiService = ApiClientOngkir.client?.create(ApiServiceKomboard::class.java)
        windowManager.addView(customView, floatWindowLayoutParam)
    }


    @SuppressLint("ClickableViewAccessibility", "LogNotTimber")
    fun initializationItem(view: ViewGroup){
        btn_pickAsal = view.findViewById(R.id.layout_pickAlamatAsal)
        btn_pickTujuan = view.findViewById(R.id.layout_pickAlamatTujuan)
        btn_pickExpedisi = view.findViewById(R.id.btn_doneLanjut)
        ed_Berat = view.findViewById(R.id.ed_beratBarang)
        tv_alamatAsal = view.findViewById(R.id.tv_bottomAlamat_Asal)
        tv_alamatTujuan = view.findViewById(R.id.tv_bottomAlamat_tujuan)
        tv_topAsal= view.findViewById(R.id.tv_topAlamat_Asal)
        tv_topTujuan= view.findViewById(R.id.tv_topAlamat_tujuan)
        tv_pickExpedisi= view.findViewById(R.id.tv_pick_expedisi)
        tvAddresCosting = view.findViewById(R.id.tvADDRESS)
        rbGroup = view.findViewById(R.id.rbGroupExpedisi)
        rbJNE = view.findViewById(R.id.rbJNE)
        rbJNT = view.findViewById(R.id.rbJNT)
        rbSICEPAT = view.findViewById(R.id.rbSICEPAT)
        tvWeight = view.findViewById(R.id.tvWEIGHT)
        layout_button_cekOngkir = view.findViewById(R.id.lay_btn_cekOngkir)
        layout_button_pickAlamat = view.findViewById(R.id.lay_btn_pick)
        layout_btnPickExpedisi = view.findViewById(R.id.lay_btn_lanjut)
        layout_pickAlamat = view.findViewById(R.id.layout_formDestination)
        layout_cekOngkir = view.findViewById(R.id.layout_formCekongkir)
        layout_pickEkspedisi = view.findViewById(R.id.layout_pickExpedisi)
        layout_copyOngkir = view.findViewById(R.id.lay_btn_copyOngkir)
        ed_provinsi = view.findViewById(R.id.ed_pickprofinsi)
        ed_kabupaten = view.findViewById(R.id.ed_pickkabupaten)
        ed_kecamatan = view.findViewById(R.id.ed_pickkecamatan)
        btn_simpanAlamat = view.findViewById(R.id.btn_donePick)
        btn_cekOngkir = view.findViewById(R.id.btn_cekOngkir)
        btn_copyOngkir = view.findViewById(R.id.btn_copyOngkir)
        im_doneCopy_ongkir = view.findViewById(R.id.im_done_copyongkir)
        im_back = view.findViewById(R.id.im_donePick)
        im_done = view.findViewById(R.id.im_done)
        im_donePickExpedisi = view.findViewById(R.id.im_doneLanjut)
        recAddress = view.findViewById(R.id.rec_Address)
        prog_prov = view.findViewById(R.id.prog_provinsi)
        prog_kab = view.findViewById(R.id.prog_kabupaten)
        prog_kec = view.findViewById(R.id.prog_kecamatan)
        initLoading()
        btn_pickAsal.setOnClickListener {
            pickingAddres = 0
            Log.d("onPickingAddress", "$pickingAddres")
            layout_cekOngkir.visibility = View.GONE
            layout_pickAlamat.visibility = View.VISIBLE
            ed_provinsi.visibility = View.VISIBLE
            ed_provinsi.setText("")
            ed_provinsi.setError(null)
            ed_kabupaten.visibility = View.VISIBLE
            ed_kabupaten.setText("")
            ed_kabupaten.setError(null)
            ed_kecamatan.visibility = View.VISIBLE
            ed_kecamatan.setText("")
            ed_kecamatan.setError(null)
            layout_btnPickExpedisi.visibility= View.GONE
            layout_button_pickAlamat.visibility= View.VISIBLE
        }
        btn_pickTujuan.setOnClickListener {
            pickingAddres = 1
            Log.d("onPickingAddress", "$pickingAddres")
            getDataProvince()
            setupAdapter()
            layout_cekOngkir.visibility = View.GONE
            layout_pickAlamat.visibility = View.VISIBLE
            ed_provinsi.visibility = View.VISIBLE
            ed_provinsi.setText("")
            ed_provinsi.setError(null)
            ed_kabupaten.visibility = View.VISIBLE
            ed_kabupaten.setText("")
            ed_kabupaten.setError(null)
            ed_kecamatan.visibility = View.VISIBLE
            ed_kecamatan.setText("")
            ed_kecamatan.setError(null)
            layout_btnPickExpedisi.visibility= View.GONE
            layout_button_pickAlamat.visibility= View.VISIBLE
        }
        btn_pickExpedisi.setOnClickListener {
            tvAddresCosting.setText("$originDistrict - $destinationDistrict")
            tvWeight.setText("Berat : ${ed_Berat.text.toString()}(g)")
            layout_pickEkspedisi.visibility = View.VISIBLE
            layout_btnPickExpedisi.visibility = View.GONE
            layout_button_cekOngkir.visibility = View.VISIBLE
            layout_cekOngkir.visibility = View.GONE
        }
        btn_simpanAlamat.setOnClickListener {
            if (ed_provinsi.text.toString().equals("")){
                ed_provinsi.setError("Provinsi harus diisi!")
            }else if(ed_kabupaten.text.toString().equals("")){
                ed_kabupaten.setError("Kabupaten harus diisi!")
            }else if(ed_kecamatan.text.toString().equals("")){
                ed_kecamatan.setError("Kecamatan harus diisi!")
            }else{
                if (pickingAddres == 0){
                    val alamat = "Prov. ${ed_provinsi.text}, Kab. ${ed_kabupaten.text}, Kec. ${ed_kecamatan.text}"
                    originDistrict = ed_kecamatan.text.toString()
                    tv_alamatAsal.setText(alamat)
                    layout_pickAlamat.visibility = View.GONE
                    layout_cekOngkir.visibility = View.VISIBLE
                    layout_btnPickExpedisi.visibility= View.VISIBLE
                    layout_button_pickAlamat.visibility = View.GONE
                }else if(pickingAddres == 1){
                    val alamat = "Prov. ${ed_provinsi.text}, Kab. ${ed_kabupaten.text}, Kec. ${ed_kecamatan.text}"
                    destinationDistrict = ed_kecamatan.text.toString()
                    tv_alamatTujuan.setText(alamat)
                    layout_pickAlamat.visibility = View.GONE
                    layout_cekOngkir.visibility = View.VISIBLE
                    layout_btnPickExpedisi.visibility= View.VISIBLE
                    layout_button_pickAlamat.visibility = View.GONE
                }
            }
        }
        btn_cekOngkir.setOnClickListener {
            daftarHarga = ""
            btn_cekOngkir.showLoading()
            if (rbJNE.isChecked){
                tv_pickExpedisi.setText("Ekspedisi JNE")
            }
            if(rbJNT.isChecked){
                tv_pickExpedisi.setText("Ekspedisi J&T")
            }
            if (rbSICEPAT.isChecked){
                tv_pickExpedisi.setText("Ekspedisi SICEPAT")
            }
            postCost()
            setupAdapterHarga()
            btn_copyOngkir.text = "copy"
            rbGroup.visibility = View.GONE
        }
        btn_copyOngkir.setOnClickListener {
        var tracking = "Pengiriman ${tvAddresCosting.text} ${tv_pickExpedisi.text} dengan ${tvWeight.text}" +
                "\n\n--------------------\n"
        btn_copyOngkir.showLoading()
        daftarHarga = ""
        for (key in hashHarga.keys){
            daftarHarga += "${hashHarga[key]}\n\n"
        }
        Log.d("onPickingHarga", "onDaftarHarga: $daftarHarga")
        val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("ongkir", "${tracking}"+"${daftarHarga}")
            clipboard.setPrimaryClip(clip)
        btn_copyOngkir.hideLoading()
        btn_copyOngkir.text = "copied"
        }
        ed_provinsi.setOnTouchListener { v, event ->
            ed_provinsi.isCursorVisible = true
            reqFocusWindow()
            getDataProvince()
            setupAdapter()
            ed_kabupaten.visibility = View.GONE
            ed_kecamatan.visibility = View.GONE
            recAddress.visibility = View.VISIBLE
            return@setOnTouchListener false
        }
        ed_kabupaten.setOnTouchListener { v, event ->
            if (ed_provinsi.text.equals("")){
                ed_provinsi.setError("harap cari provinsi terlebih dahulu")
                ed_provinsi.requestFocus()
            }else{
//                prog_kab.visibility = View.VISIBLE
                ed_kabupaten.isCursorVisible = true
                reqFocusWindow()
                getDataCity(id_Prov)
                setupAdapterCity()
                ed_provinsi.visibility = View.GONE
                ed_kecamatan.visibility = View.GONE
                recAddress.visibility = View.VISIBLE
            }
            return@setOnTouchListener false
        }
        ed_kecamatan.setOnTouchListener { v, event ->
            ed_kecamatan.isCursorVisible = true
            reqFocusWindow()
//            prog_kec.visibility = View.VISIBLE
            getDataSubdistrict(id_Prov, id_Kab)
            setupAdapterDistrict()
            ed_provinsi.visibility = View.GONE
            ed_kabupaten.visibility = View.GONE
            recAddress.visibility = View.VISIBLE
            return@setOnTouchListener false
        }
        ed_provinsi.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                prog_prov.visibility = View.VISIBLE
                filter(s.toString())
            }

        })
        ed_kabupaten.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                prog_kab.visibility = View.VISIBLE
                filterCity(s.toString())
            }

        })
        ed_kecamatan.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                prog_kec.visibility = View.VISIBLE
                filterSubdistrict(s.toString())
            }

        })

        ed_Berat.setOnTouchListener { v, event ->

            ed_Berat.isCursorVisible = true
            reqFocusWindow()
            if (originID != 0 && destinationID != 0){
                btn_pickExpedisi.isEnabled = true
            }

            return@setOnTouchListener false
        }

        ed_Berat.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    btn_pickExpedisi.isEnabled = false
                }
            }
        })

        im_done.setOnClickListener {
            layout_cekOngkir.visibility = View.VISIBLE
            layout_button_cekOngkir.visibility = View.GONE
            layout_pickEkspedisi.visibility = View.GONE
            layout_btnPickExpedisi.visibility = View.VISIBLE
        }
        im_doneCopy_ongkir.setOnClickListener {
            KomboardService.layout_menu.visibility = View.VISIBLE
            showKeyboard()
            dataPref.saveBoolean(KEYBOARD_TYPE, true)
            close()
        }
        im_back.setOnClickListener {
            layout_cekOngkir.visibility = View.VISIBLE
            layout_pickAlamat.visibility = View.GONE
            layout_btnPickExpedisi.visibility= View.VISIBLE
            layout_button_pickAlamat.visibility= View.GONE
            recAddress.visibility= View.GONE
            adapterAddress?.setData(emptyList())
            ed_provinsi.setText("")
            ed_kabupaten.setText("")
            ed_kecamatan.setText("")
        }
        im_donePickExpedisi.setOnClickListener {
            KomboardService.layout_menu.visibility = View.VISIBLE
            showKeyboard()
            dataPref.saveBoolean(KEYBOARD_TYPE, true)
            close()
        }

    }
    fun initLoading(){
        val doubleBounce: Sprite = Circle()
        prog_prov.setIndeterminateDrawable(doubleBounce)
        prog_kab.setIndeterminateDrawable(doubleBounce)
        prog_kec.setIndeterminateDrawable(doubleBounce)
    }
    fun getDataProvince(){
        prog_prov.visibility = View.VISIBLE
        val getProvince: Call<ProvinceRespons>? =
            apiService?.getProvince(
                "68d4b8a1858b8c7fe6822c90fb6d6667")
        getProvince?.enqueue(object: Callback<ProvinceRespons>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<ProvinceRespons>,
                response: Response<ProvinceRespons>
            ) {
                adapterAddress?.setData(response.body()?.rajaongkir?.results!!)
                arrayAddress.addAll(response.body()?.rajaongkir?.results!!)
                Log.d("onDataProvince", "${response.body()?.rajaongkir?.results}")
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<ProvinceRespons>, t: Throwable) {
                Log.d("onFailureProvince", "$t")
            }
        })
    }
    @SuppressLint("LogNotTimber")
    fun getDataCity(provinceID: Int){
        Log.d("onGetCity", "getDataCity: $provinceID")
        val getCity: Call<CityRespons>? =
            apiService?.getCity(
                "68d4b8a1858b8c7fe6822c90fb6d6667",
                provinceID
            )
        getCity?.enqueue(object : Callback<CityRespons>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(call: Call<CityRespons>, response: Response<CityRespons>) {
                adapterCity?.setData(response.body()?.rajaongkirCity?.resultsCity!!)
                arrayCity.addAll(response.body()?.rajaongkirCity?.resultsCity!!)
                prog_kab.visibility = View.GONE
                Log.d("onDataCity", "${response.body()?.rajaongkirCity?.resultsCity}")
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<CityRespons>, t: Throwable) {
                Log.d("onFailureCity", "$t")
            }
        })
    }

    @SuppressLint("LogNotTimber")
    fun getDataSubdistrict(prov: Int, kec: Int){
        Log.d("onGetDistrict", "getDataDistrict: $prov $kec")
        val getDistrict: Call<SubDistrictRespons>? =
            apiService?.getSubdistrict(
                "68d4b8a1858b8c7fe6822c90fb6d6667",
                prov,
                kec
            )
        getDistrict?.enqueue(object: Callback<SubDistrictRespons>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<SubDistrictRespons>,
                response: Response<SubDistrictRespons>
            ) {
                adapterDistrict?.setData(response.body()?.rajaongkirSubdistrict?.resultsSubdistrict!!)
                arrayDistrict.addAll(response.body()?.rajaongkirSubdistrict?.resultsSubdistrict!!)
                prog_kec.visibility = View.VISIBLE
                Log.d("onDataDistrict", "${response.body()?.rajaongkirSubdistrict?.resultsSubdistrict}")
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<SubDistrictRespons>, t: Throwable) {
                Log.d("onErrorDistrict", "$t")
            }

        })
    }
    fun postCost(){
        val body = mapOf(
            "origin" to originID,
            "origin_type" to originType,
            "destination" to destinationID,
            "destination_type" to destinationType,
            "weight" to ed_Berat.text,
            "courier" to courier
        )
        val hasMapBody: HashMap<String, Any?> = HashMap<String, Any?>()
        hasMapBody.put("origin", originID)
        hasMapBody.put("origin_type", originType)
        hasMapBody.put("destination", destinationID)
        hasMapBody.put("destination_type", destinationType)
        hasMapBody.put("weight", ed_Berat.text)
        hasMapBody.put("courier", courier)
        val bodyObject : CostOngkirParams = CostOngkirParams(
            originID!!,
            originType!!,
            destinationID!!,
            destinationType!!,
            ed_Berat.text.toString().toInt(),
            courier!!
        )
        val postCost: Call<OngkirRespons>? =
        apiService?.costOngkir(
            "68d4b8a1858b8c7fe6822c90fb6d6667",
            bodyObject)
        postCost?.enqueue(object : Callback<OngkirRespons>{
            @SuppressLint("LogNotTimber", "SetTextI18n")
            override fun onResponse(call: Call<OngkirRespons>, response: Response<OngkirRespons>) {
                adapterHargaOngkir?.setData(response.body()?.rajaongkirOngkir?.resultsOngkir!![0].costsData!!)
                layout_button_cekOngkir.visibility = View.GONE
                layout_copyOngkir.visibility = View.VISIBLE
                Log.d("onCostOngkir", "${response.body()?.rajaongkirOngkir?.resultsOngkir}")
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<OngkirRespons>, t: Throwable) {
                Log.d("onCostFailure", "$t")
                btn_cekOngkir.hideLoading()
            }

        })
    }
    fun setupAdapter(){
        prog_prov.visibility = View.GONE
        adapterAddress = AddresAdapter(object: AddresAdapter.onAddressAdapterLintener{
            @SuppressLint("LogNotTimber")
            override fun onClick(dataAddress: ProvinceResults) {
                ed_provinsi.setText("${dataAddress.province_name}")
                recAddress.visibility = View.GONE
                ed_kabupaten.visibility = View.VISIBLE
                ed_kecamatan.visibility = View.VISIBLE
                dataAddress.province_id?.let {
                    id_Prov = it
                }
                ed_kabupaten.isEnabled = true
                Log.d("onClickListener", "${dataAddress.province_name}")
            }
        })
        recAddress.apply {
            adapter = adapterAddress
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }
    }
    fun setupAdapterCity(){
        prog_kab.visibility = View.GONE
        adapterCity = CityAdapter(object : CityAdapter.onCityAdapterListener{
            override fun onClick(dataCity: CityResults) {
                ed_kabupaten.setText("${dataCity.city_name}")
                recAddress.visibility = View.GONE
                ed_kecamatan.visibility = View.VISIBLE
                ed_provinsi.visibility = View.VISIBLE
                dataCity.city_id?.let {
                    id_Kab = it.toInt()
                }
                ed_kecamatan.isEnabled = true
            }
        })
        recAddress.apply {
            adapter = adapterCity
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }
    }
    fun setupAdapterDistrict(){
        prog_kec.visibility = View.GONE
        adapterDistrict = SubdistrictAdapter(object : SubdistrictAdapter.onDistrictListener{
            @SuppressLint("LogNotTimber")
            override fun onClick(dataDistrict: SubDistrictResults) {
                if (pickingAddres == 0){
                    originID = dataDistrict.subdistrict_id
                    originType = "subdistrict"
                }else if(pickingAddres == 1){
                    destinationID = dataDistrict.subdistrict_id
                    destinationType = "subdistrict"
                }
                ed_kecamatan.setText("${dataDistrict.subdistrict_name}")
                ed_provinsi.visibility = View.VISIBLE
                ed_kabupaten.visibility = View.VISIBLE
                recAddress.visibility = View.GONE
                Log.d("onDistrictPicked", "${dataDistrict.subdistrict_name}")
                if (originID != 0 && destinationID != 0){
                    btn_pickExpedisi.isEnabled = true
                }
            }

        })
        recAddress.apply {
            adapter = adapterDistrict
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }
    }
    fun setupAdapterHarga(){
        recAddress.visibility = View.VISIBLE
        adapterHargaOngkir = AdapterHargaOngkir(object: AdapterHargaOngkir.onHargaOngkirListener{
            @SuppressLint("LogNotTimber")
            override fun onClick(costData: CostResults) {
                Log.d("ondataHargaPick", "onClick: picking ${costData.cost!![0].valueCost}")
            }

        })
        recAddress.apply {
            adapter = adapterHargaOngkir
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }
    }
    fun reqFocusWindow(){
        val floatWindowLayoutParamUpdateFlag = floatWindowLayoutParam
        floatWindowLayoutParamUpdateFlag.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        windowManager.updateViewLayout(customView, floatWindowLayoutParamUpdateFlag)
    }
    fun showKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            customView.applicationWindowToken,
            InputMethodManager.SHOW_FORCED, 0
        )
    }
    fun hideKeyboard(){
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            customView.applicationWindowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY, 0
        )
    }

    private fun setupWindow() {
        floatWindowLayoutParam = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            LAYOUT_TYPE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        floatWindowLayoutParam.gravity = Gravity.BOTTOM or Gravity.BOTTOM
    }

    private fun checkingSDK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST
        }
    }

    private fun close(){
        stopSelf()
        windowManager.removeView(customView)
    }
    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        windowManager.removeView(customView)
    }

    fun filter(text: String) {

        val filteredAddress =  mutableSetOf<ProvinceResults>()
        val filteredAddressArr: ArrayList<ProvinceResults> = ArrayList()
        val filterData = arrayAddress.filter { it.province_name!!.toLowerCase().contains(text.toLowerCase()) }

        for (item in filterData) {
            filteredAddress.add(item)
        }

        for (filtered in filteredAddress) {
            filteredAddressArr.add(filtered)
        }

        adapterAddress?.filterList(filteredAddressArr)

        prog_prov.visibility = View.GONE
    }
    fun filterCity(text: String) {

        val filteredAddress = mutableSetOf<CityResults>()
        val filteredAddressArr: ArrayList<CityResults> = ArrayList()
        val filterData = arrayCity.filter { it.city_name!!.toLowerCase().contains(text.toLowerCase()) }

        for (item in filterData) {
            filteredAddress.add(item)
        }

        for (filtered in filteredAddress) {
            filteredAddressArr.add(filtered)
        }

        adapterCity?.filterList(filteredAddressArr)
        prog_kab.visibility = View.GONE
    }
    fun filterSubdistrict(text: String) {
        val filteredAddress = mutableSetOf<SubDistrictResults>()
        val filteredAddressArr: ArrayList<SubDistrictResults> = ArrayList()
        val filterData = arrayDistrict.filter { it.subdistrict_name!!.toLowerCase().contains(text.toLowerCase()) }

        for (item in filterData) {
            filteredAddress.add(item)
        }

        for (filtered in filteredAddress) {
            filteredAddressArr.add(filtered)
        }

        adapterDistrict?.filterList(filteredAddressArr)
        prog_kec.visibility = View.GONE
    }
}