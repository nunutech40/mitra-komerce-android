package id.android.kmabsensi.presentation.checkin

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.OfficeAssigned
import id.android.kmabsensi.databinding.ActivityCekJangkauanBinding
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.DATA_OFFICE_KEY
import id.android.kmabsensi.utils.LocationManager
import id.android.kmabsensi.utils.PRESENCE_ID_KEY
import org.jetbrains.anko.startActivity


class CekJangkauanActivity : BaseActivity(), OnMapReadyCallback {

    private var countOutArea = 0

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private var data: OfficeAssigned? = null

    //untuk keperluan checkout
    private var presenseId: Int = 0

    private var lastLocation: Location? = null

    var marker: Marker? = null

    private val dialog by lazy {
        MaterialDialog(this).apply {
            cornerRadius(16f)
            customView(
                R.layout.dialog_diluar_jangkauan,
                scrollable = false,
                horizontalPadding = true,
                noVerticalPadding = true
            )
        }
    }

    private val binding by lazy { ActivityCekJangkauanBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setupToolbar()

        data = intent.getParcelableExtra(DATA_OFFICE_KEY)
        presenseId = intent.getIntExtra(PRESENCE_ID_KEY, 0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = LocationManager(this)

    }

    override fun onMapReady(p0: GoogleMap?) {

        p0?.let {
            mMap = it
        }

        // Add a marker in Sydney and move the camera
        var office: LatLng? = null
        data?.let { data -> office = LatLng(data.lat.toDouble(), data.lng.toDouble()) }
        office?.let { office ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(office, 15.0F))

            mMap.addCircle(
                CircleOptions()
                    .center(LatLng(office.latitude, office.longitude))
                    .radius(350.0)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(ContextCompat.getColor(this, R.color.colorCircle))
            )


            locationManager.listenLocationUpdate {
                lastLocation = it

                val officeLocation = Location("office").apply {
                    latitude = office.latitude
                    longitude = office.longitude
                }
                val distance = officeLocation.distanceTo(it)
                if (distance > 3500) {
                    countOutArea++
                } else {
                    Handler(mainLooper).postDelayed({
                        //doSomethingHere()
                        startActivity<CheckinActivity>(
                            DATA_OFFICE_KEY to data,
                            PRESENCE_ID_KEY to presenseId
                        )
                    }, 700)
                }

                if (countOutArea >= 2) {
                    showDialogDiluarJangkauan()
                }
            }
        }

    }

    fun showDialogDiluarJangkauan() {
        if (!dialog.isShowing) {
            val customView = dialog.getCustomView()
            val btnOke = customView.findViewById<Button>(R.id.btn_oke)
            btnOke.setOnClickListener {
                dialog.dismiss()
                onBackPressed()
            }
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        locationManager.startLocationUpdate()
    }

    override fun onStop() {
        super.onStop()
        locationManager.stopLocationUpdate()
    }

}
