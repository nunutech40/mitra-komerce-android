package id.android.kmabsensi.services.windowService

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kusu.loadingbutton.LoadingButton
import id.android.kmabsensi.R
import id.android.kmabsensi.data.pref.PrefData
import id.android.kmabsensi.data.pref.PreferencesHelper
import id.android.kmabsensi.data.remote.ApiClientKomboard
import id.android.kmabsensi.data.remote.body.komboard.BulkResiParams
import id.android.kmabsensi.data.remote.body.komboard.ResiParams
import id.android.kmabsensi.data.remote.response.komboard.BulkResiRespons
import id.android.kmabsensi.data.remote.response.komboard.CekResiRespons
import id.android.kmabsensi.data.remote.response.komboard.DataBulkResi
import id.android.kmabsensi.data.remote.response.komboard.DataResultResi
import id.android.kmabsensi.data.remote.service.ApiServiceKomboard
import id.android.kmabsensi.services.komboard.KomboardService
import org.jetbrains.anko.find
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LayoutResi: Service(){
    private lateinit var customView: ViewGroup
    private var LAYOUT_TYPE = 0
    private lateinit var floatWindowLayoutParam: WindowManager.LayoutParams
    private lateinit var windowManager: WindowManager
    private lateinit var inflater: LayoutInflater
    private lateinit var dataPref: PrefData
    private val prefHelper: PreferencesHelper by inject()
    private var adapterResi: ResiAdapter? = null
    private var adapterRiwayatResi: AdapterRiwayatResi? = null
    //field
    private lateinit var btn_Cek: LoadingButton
    private lateinit var ed_Search: EditText
    private lateinit var im_done: ImageView
    private lateinit var recResi: RecyclerView
    private lateinit var recRiwayatresi: RecyclerView
    private lateinit var tv_error: TextView
    //params
    private lateinit var paramsResi: ResiParams
    private var apiService: ApiServiceKomboard ?= null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customView = inflater.inflate(R.layout.layout_input_cekresi, null) as ViewGroup
        ed_Search = customView.findViewById(R.id.ed_searchResi)
        im_done = customView.findViewById(R.id.im_donecekResi)
        apiService = ApiClientKomboard.client?.create(ApiServiceKomboard::class.java)
        btn_Cek = customView.findViewById(R.id.btn_cekResi)
        recResi = customView.findViewById(R.id.rec_resultSearchResi)
        recRiwayatresi = customView.findViewById(R.id.rec_riwayat_resi)
        tv_error = customView.findViewById(R.id.tv_error_resi)
        dataPref = PrefData(applicationContext)
        checkingSDK()
        setupWindow()
        setupAdapter()
        windowManager.addView(customView, floatWindowLayoutParam)
        ed_Search.setOnTouchListener { v, event ->
            ed_Search.isCursorVisible = true
            reqFocusWindow()
            return@setOnTouchListener false
        }
        im_done.setOnClickListener {
            KomboardService.layout_menu.visibility= View.VISIBLE
            close()
            showKeyboard()
        }
        btn_Cek.setOnClickListener {
            btn_Cek.showLoading()
            paramsResi = ResiParams(ed_Search.text.toString())
            getDataResi(paramsResi)
            Log.d("onToken","${prefHelper.getString(PreferencesHelper.ACCESS_TOKEN_KEY)}")
        }
    }
    fun getDataResi(params: ResiParams){
        val getResi: Call<CekResiRespons>? =
            apiService?.getResi(
                "Bearer "+prefHelper.getString(PreferencesHelper.ACCESS_TOKEN_KEY),
                params.search
            )
        getResi?.enqueue(object: Callback<CekResiRespons>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<CekResiRespons>,
                response: Response<CekResiRespons>
            ) {
                if (response.body()?.data == null || response.body()?.data!!.isEmpty()){
                    tv_error.visibility = View.VISIBLE
                    recResi.visibility = View.GONE
                    btn_Cek.hideLoading()
                    tv_error.setText("data tidak ditemukan")
                    Log.d("onResultResi", "${response.body()?.data}")
                    Toast.makeText(applicationContext, "data tidak ada", Toast.LENGTH_SHORT).show()
                }else{
                    recResi.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    adapterResi?.setData(response.body()?.data!!)
                    btn_Cek.hideLoading()
                    Log.d("onResultResi", "${response.body()?.data}")
                }
            }

            override fun onFailure(call: Call<CekResiRespons>, t: Throwable) {
                Log.d("onErrorResi", "error : $t")
            }

        })
    }
    @SuppressLint("LogNotTimber")
    fun getDataBulk(data: String){
        val getBulk: Call<BulkResiRespons>? =
            apiService?.getBulkResi(
                "Bearer "+prefHelper.getString(PreferencesHelper.ACCESS_TOKEN_KEY),
                data
            )
        getBulk?.enqueue(object: Callback<BulkResiRespons>{
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<BulkResiRespons>,
                response: Response<BulkResiRespons>
            ) {
                if (response.body()?.data == null){
                    tv_error.setText("Data resi tidak valid")
                    tv_error.visibility = View.VISIBLE
                    Toast.makeText(applicationContext, "data tidak ada", Toast.LENGTH_SHORT).show()
                    Log.d("onDataBulkResi", "${response.body()?.data}")
                }else{
                    adapterRiwayatResi?.setData(response.body()?.data!!)
                    Log.d("onDataBulkResi", "${response.body()?.data}")
                }
            }

            @SuppressLint("LogNotTimber")
            override fun onFailure(call: Call<BulkResiRespons>, t: Throwable) {
                tv_error.setText("Data resi tidak valid")
                tv_error.visibility = View.VISIBLE
                Log.d("onErrorBulk", "$t")
            }

        })
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

    private fun setupAdapter(){
        adapterResi = ResiAdapter(object : ResiAdapter.onAdapterListener{
            @SuppressLint("LogNotTimber")
            override fun onClick(dataResi: DataResultResi) {
                getDataBulk("${dataResi.airwayBil}")
                setupAdapterRiwayat()
                ed_Search.setText("${dataResi.customerName}")
                recResi.visibility = View.GONE
                recRiwayatresi.visibility= View.VISIBLE
                Log.d("onResultPicking", "${dataResi.airwayBil}")
            }
        })
        recResi.apply {
            adapter = adapterResi
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }
    }
    private fun setupAdapterRiwayat(){
        adapterRiwayatResi = AdapterRiwayatResi(object : AdapterRiwayatResi.onBulkResiListener{
            @SuppressLint("LogNotTimber")
            override fun onClick(dataBulkResi: DataBulkResi) {
                Log.d("onDataBulkResiPicked"," data picked : ${dataBulkResi.customer}")
            }

        })
        recRiwayatresi.apply {
            adapter = adapterRiwayatResi
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }
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
}