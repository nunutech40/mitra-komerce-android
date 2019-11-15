package id.android.kmabsensi.presentation.kantor.cabang

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_tambah_cabang.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.koin.android.ext.android.inject

class TambahCabangActivity : BaseActivity(), AdapterView.OnItemSelectedListener {


    private val vm: TambahCabangViewModel by inject()

    private val MAP_BUTTON_REQUEST_CODE = 221

    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null

    val pjNames = mutableListOf<String>()
    var pjData = mutableListOf<User>()

    var latSelected: String = ""
    var lngSelected: String = ""
    var pjSelected: Int = 0

    private var office: Office? = null

    private var crudMode = 0 // tambah -> 0, edit -> 1, delete -> 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_cabang)

        disableAutofill()

        office = intent.getParcelableExtra(OFFICE_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
////        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setToolbarTitle(if (office != null) "Kelola ${office?.office_name}" else "Tambah Cabang Baru")

        viewListener()
        observeData()

        vm.getUserManagement()

        locationManager = LocationManager(this)

        locationManager.getLastKnowPosition({
            lastLocation = it
        }, {})

        locationManager.listenLocationUpdate {

            lastLocation = it

        }

        if (office != null) initViews(false) else initViews(true)


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        pjSelected = if (position == 0) 0 else pjData[position - 1].id

    }

    fun viewListener() {

        office?.let {
            edtNamaCabang.setText(it.office_name)
            edtAlamatCabang.setText(it.address)

            edtAlamatCabang.isFocusableInTouchMode = true
            edtAlamatCabang.isFocusable = true

            latSelected = it.lat.toString()
            lngSelected = it.lng.toString()


        }


        edtAlamatCabang.setOnClickListener {
            if (edtAlamatCabang.text.isEmpty()) {
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

        btnSimpan.setOnClickListener {
            if (crudMode == 0) {
                if (validation()) vm.addOffice(
                    edtNamaCabang.text.toString(),
                    latSelected,
                    lngSelected,
                    edtAlamatCabang.text.toString(),
                    pjSelected
                )
            } else if (crudMode == 1) {
                office?.let {
                    if (validation()) vm.editOffice(
                        it.id,
                        edtNamaCabang.text.toString(),
                        latSelected,
                        lngSelected,
                        edtAlamatCabang.text.toString(),
                        pjSelected
                    )
                }

            }

        }

        btnMapPicker.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                .withGooglePlacesEnabled()
                .withLocation(office!!.lat, office!!.lng)
                .withSearchZone("in_ID")
                .withDefaultLocaleSearchZone()
                .withSatelliteViewHidden()
                .withGoogleTimeZoneEnabled()
                .withUnnamedRoadHidden()
                .build(applicationContext)

            startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
        }

    }

    private fun observeData() {
        vm.penanggungJawabState.observe(this, Observer {
            when (it) {
                is UiState.Success -> {
                    pjData.addAll(it.data.data)
                    pjNames.add("Pilih Manajemen (Opsional)")
                    it.data.data.forEach {
                        pjNames.add(it.full_name)
                    }
                    ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pjNames).also {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner_pj.adapter = it
                    }
                    spinner_pj.onItemSelectedListener = this

                    office?.let { kantor ->
                        spinner_pj.setSelection(pjData.indexOfFirst { it.id == kantor.pj_user_id } + 1)
                    }
                }
            }
        })

        vm.crudCabangState.observe(this, Observer {
            when (it) {
                is UiState.Success -> {
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
                    e(it.throwable)
                }
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == MAP_BUTTON_REQUEST_CODE) {
                latSelected = data.getDoubleExtra(LATITUDE, 0.0).toString()
                lngSelected = data.getDoubleExtra(LONGITUDE, 0.0).toString()
                val address = data.getStringExtra(LOCATION_ADDRESS)
                edtAlamatCabang.setText(address)
                edtAlamatCabang.isFocusableInTouchMode = true
                edtAlamatCabang.isFocusable = true
                edtAlamatCabang.setSelection(edtAlamatCabang.text.toString().length)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                d { "Canceled" }
            }
            if (requestCode == locationManager.REQUEST_CHECK_SETTINGS) {
                locationManager.startLocationUpdate()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        locationManager.stopLocationUpdate()
    }

    fun initViews(enabled: Boolean) {
        edtNamaCabang.isEnabled = enabled
        edtAlamatCabang.isEnabled = enabled
        spinner_pj.isEnabled = enabled
        btnSimpan.isEnabled = enabled
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        office?.let {
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
            alert("Apakah anda yakin ingin menghapus kantor cabang ini", "Hapus Kantor") {
                yesButton {
                    crudMode = 2
                    office?.let { vm.deleteOffice(it.id) }
                }
            }.show()
        }

        return super.onOptionsItemSelected(item)
    }

    fun validation(): Boolean {
        val namaCabang = ValidationForm.validationInput(edtNamaCabang, "Nama cabang tidak boleh kosong")
        val alamatCabang =
            ValidationForm.validationInput(edtAlamatCabang, "Alamat cabang tidak boleh kosong")

        return namaCabang && alamatCabang
    }


}
