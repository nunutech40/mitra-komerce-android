package id.android.kmabsensi.presentation.kantor.cabang

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.d
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.Office
import id.android.kmabsensi.data.remote.response.User
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.LocationManager
import id.android.kmabsensi.utils.OFFICE_KEY
import id.android.kmabsensi.utils.UiState
import id.android.kmabsensi.utils.ValidationForm
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

    private var office : Office? = null

    private var crudMode = 0 // tambah -> 0, edit -> 1, delete -> 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_cabang)

        office = intent.getParcelableExtra(OFFICE_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.title = if (office != null) "Kelola ${office?.office_name}" else "Tambah Cabang Baru"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
            edtEmail.setText(it.office_name)
            edtPasword.setText(it.address)

            edtPasword.isFocusableInTouchMode = true
            edtPasword.isFocusable = true

            latSelected = it.lat.toString()
            lngSelected = it.lng.toString()

        }


        edtPasword.setOnClickListener {
            if (edtPasword.text.isEmpty()) {
                val locationPickerIntent = LocationPickerActivity.Builder()
                    .withGooglePlacesEnabled()
                    .withGeolocApiKey("AIzaSyDyzQyjNW2HOJIYV7FrPah1sZkPN4uuE_w")
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
            if (crudMode == 0){
                if (validation()) vm.addOffice(edtEmail.text.toString(), latSelected, lngSelected, edtPasword.text.toString(), pjSelected)
            } else if(crudMode == 1) {
                office?.let {
                    if (validation()) vm.editOffice(it.id, edtEmail.text.toString(), latSelected, lngSelected, edtPasword.text.toString(), pjSelected)
                }

            }

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
                    Intent().apply {
                        putExtra("message", it.data.message)
                        setResult(Activity.RESULT_OK)
                    }
                    finish()
                }
                is UiState.Error -> {
                    toast(it.throwable.message.toString())
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
                edtPasword.setText(address)
                edtPasword.isFocusableInTouchMode = true
                edtPasword.isFocusable = true
                edtPasword.setSelection(edtPasword.text.toString().length)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                d { "Canceled" }
            }
            if (requestCode == locationManager.REQUEST_CHECK_SETTINGS){
                locationManager.startLocationUpdate()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        locationManager.stopLocationUpdate()
    }

    fun initViews(enabled: Boolean){
        edtEmail.isEnabled = enabled
        edtPasword.isEnabled = enabled
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

        if (item.itemId == R.id.action_edit){
            initViews(true)
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
        val namaCabang = ValidationForm.validationInput(edtEmail, "Nama cabang tidak boleh kosong")
        val alamatCabang = ValidationForm.validationInput(edtPasword, "Alamat cabang tidak boleh kosong")

        return namaCabang && alamatCabang
    }


}
