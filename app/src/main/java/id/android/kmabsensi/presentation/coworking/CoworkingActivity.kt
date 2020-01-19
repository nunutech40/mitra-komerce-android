package id.android.kmabsensi.presentation.coworking

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.d
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.CoworkingSpace
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_coworking.*
import kotlinx.android.synthetic.main.activity_coworking.toolbar
import kotlinx.android.synthetic.main.item_row_coworking.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class CoworkingActivity : BaseActivity() {

    private val vm: CoworkingSpaceViewModel by viewModel()

    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null

//    lateinit var dateFrom: Date
    private var statusSelected = 1
    private var jumlahKursi = 1

    var latSelected: Double = 0.0
    var lngSelected: Double = 0.0

    private val MAP_BUTTON_REQUEST_CODE = 221

    private lateinit var myDialog : MyDialog

    private var coworking: CoworkingSpace? = null

    private var crudMode = 0 // tambah -> 0, edit -> 1, delete -> 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coworking)

        coworking = intent.getParcelableExtra(COWORKING_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        setToolbarTitle(if (coworking != null) "Kelola ${coworking?.cowork_name}" else "Tambahkan List")

        myDialog = MyDialog(this)

        viewListener()
        initLocation()

        initViews(coworking == null)

        vm.crudCoworkingSpaceResponse.observe(this, androidx.lifecycle.Observer {
            when(it){
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status){
                        val intent = Intent()
                        intent.putExtra("message", it.data.message)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Gagal", it.data.message)
                    }
                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    Timber.e(it.throwable)
                }
            }
        })

        coworking?.let {
            jumlahKursi = it.slot
            edtNamaCoworking.setText(it.cowork_name)
            edtLokasi.setText(it.address)
            txtJumlahKursi.text = it.slot.toString()
            edtInformasi.setText(it.description)

            edtLokasi.isFocusableInTouchMode = true
            edtLokasi.isFocusable = true

            latSelected = it.lat
            lngSelected = it.lng

            spinnerStatus.setSelection(it.status - 1)

            btnTambah.text = "Ubah"

        }

    }

    private fun initLocation(){
        locationManager = LocationManager(this)

        locationManager.getLastKnowPosition({
            lastLocation = it
        }, {})

        locationManager.listenLocationUpdate {
            lastLocation = it
        }
    }

    private fun initViews(enabled: Boolean) {
        edtNamaCoworking.isEnabled = enabled
        edtLokasi.isEnabled = enabled
        edtInformasi.isEnabled = enabled
        spinnerStatus.isEnabled = enabled
        btnTambah.isEnabled = enabled
        btnPlus.isEnabled = enabled
        btnMinus.isEnabled = enabled
    }

    private fun viewListener() {

        btnTambah.setOnClickListener {
            if (validation()){
                if (crudMode == 0){
                    vm.addCoworkingSpace(
                        edtNamaCoworking.text.toString(),
                        edtInformasi.text.toString(),
                        latSelected,
                        lngSelected,
                        edtLokasi.text.toString(),
                        statusSelected,
                        txtJumlahKursi.text.toString().toInt()
                    )
                } else if (crudMode == 1){
                    coworking?.let {
                        vm.editCoworkingSpace(
                            it.id,
                            edtNamaCoworking.text.toString(),
                            edtInformasi.text.toString(),
                            latSelected,
                            lngSelected,
                            edtLokasi.text.toString(),
                            statusSelected,
                            txtJumlahKursi.text.toString().toInt()
                        )
                    }
                }

            }
        }

        edtLokasi.setOnClickListener {
            if (edtLokasi.text.toString().isEmpty()){
                val locationPickerIntent = LocationPickerActivity.Builder()
                    .withGooglePlacesEnabled()
                    .withLocation(lastLocation?.latitude!!, lastLocation?.longitude!!)
                    .withSearchZone("in_ID")
                    .withDefaultLocaleSearchZone()
                    .withSatelliteViewHidden()
                    .withGoogleTimeZoneEnabled()
                    .withUnnamedRoadHidden()
                    .build(applicationContext)

                startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
            }
        }

        btnMapPicker.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                .withGooglePlacesEnabled()
                .withLocation(coworking!!.lat, coworking!!.lng)
                .withSearchZone("in_ID")
                .withDefaultLocaleSearchZone()
                .withSatelliteViewHidden()
                .withGoogleTimeZoneEnabled()
                .withUnnamedRoadHidden()
                .build(applicationContext)

            startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
        }

        btnPlus.setOnClickListener {
            jumlahKursi += 1
            txtJumlahKursi.text = "$jumlahKursi"
        }

        btnMinus.setOnClickListener {
            if (jumlahKursi > 1) jumlahKursi -= 1
            txtJumlahKursi.text = "$jumlahKursi"
        }

//        edtTanggal.setOnClickListener {
//            MaterialDialog(this).show {
//                datePicker { dialog, date ->
//
//                    // Use date (Calendar)
//
//                    dialog.dismiss()
//
//                    dateFrom = date.time
//
//                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//                    val dateSelected: String = dateFormat.format(date.time)
//                    setDateUi(dateSelected)
//                }
//            }
//        }

        // spinner status
        ArrayAdapter.createFromResource(
            this,
            R.array.status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapter

            spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    statusSelected = position + 1
                }

            }
        }
    }

//    private fun setDateUi(dateFrom: String) {
//        edtTanggal.setText(dateFrom)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == MAP_BUTTON_REQUEST_CODE) {
                latSelected = data.getDoubleExtra(LATITUDE, 0.0)
                lngSelected = data.getDoubleExtra(LONGITUDE, 0.0)
                val address = data.getStringExtra(LOCATION_ADDRESS)
                edtLokasi.setText(address)
                edtLokasi.isFocusableInTouchMode = true
                edtLokasi.isFocusable = true
                edtLokasi.setSelection(edtLokasi.text.toString().length)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                d { "Canceled" }
            }
            if (requestCode == locationManager.REQUEST_CHECK_SETTINGS) {
                locationManager.startLocationUpdate()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        coworking?.let {
            menuInflater.inflate(R.menu.menu_edit_delete, menu)
            val menuDelete = menu?.findItem(R.id.action_delete)
            val menuEdit = menu?.findItem(R.id.action_edit)
            if (crudMode == 1) {
                menuDelete?.isVisible = true
                menuEdit?.isVisible = false
            } else {
                menuEdit?.isVisible = true
                menuDelete?.isVisible = false
            }
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_edit) {
            initViews(true)
            btnMapPicker.visible()
            crudMode = 1
            invalidateOptionsMenu()
        } else if (item.itemId == R.id.action_delete) {
            alert("Apakah anda yakin ingin menghapus coworking space ini", "Hapus Co-Working") {
                yesButton {
                    crudMode = 2
                    coworking?.let { vm.deleteCoworkingSpace(it.id) }
                }
            }.show()
        }

        return super.onOptionsItemSelected(item)
    }

    fun validation(): Boolean{
        val coworkName = ValidationForm.validationInput(edtNamaCoworking, "Nama Co-Working tidak boleh kosong")
        val coworkLokasi = ValidationForm.validationInput(edtLokasi, "Lokasi tidak boleh kosong")
        val coworkinformasi = ValidationForm.validationInput(edtInformasi, "Informasi tidak boleh kosong")

        return coworkName && coworkLokasi && coworkinformasi
    }
}
