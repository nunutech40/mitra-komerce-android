package id.android.kmabsensi.presentation

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import id.android.kmabsensi.data.remote.response.OfficeAssigned
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.utils.LocationManager
import kotlinx.android.synthetic.main.activity_detail_karyawan.*
import id.android.kmabsensi.R
import kotlinx.android.synthetic.main.activity_cek_jangkauan.*
import kotlinx.android.synthetic.main.activity_detail_karyawan.toolbar
import org.jetbrains.anko.toast


class CekJangkauanActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private lateinit var data : OfficeAssigned

    private var lastLocation: Location? = null

    var marker : Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_jangkauan)


        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cek Jangkauan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = intent.getParcelableExtra("data")

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
        val office = LatLng(data.lat.toDouble(), data.lng.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(office, 15.0F))

        mMap.addCircle(
            CircleOptions()
                .center(LatLng(office.latitude, office.longitude))
                .radius(200.0)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(ContextCompat.getColor(this, R.color.colorCircle))
        )


        locationManager.listenLocationUpdate {
            lastLocation = it
            var myLocation = LatLng(it.latitude, it.longitude)
            marker?.let {
                it.remove()
            }
            marker = mMap.addMarker(MarkerOptions().position(myLocation))
            val officeLocation = Location("office").apply {
                latitude = office.latitude
                longitude = office.longitude
            }
            val distance = officeLocation.distanceTo(it)

            if (distance > 200){
                imgJangkauan.setImageResource(R.drawable.ic_circle_x)
                txtJangkauan.text = "Anda diluar jangkauan, silahkan menuju jangkauan"
            } else {
                imgJangkauan.setImageResource(R.drawable.ic_circle_checked)
                txtJangkauan.text = "Anda berada dalam jangkauan"
            }
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
