package id.android.kmabsensi.presentation.checkin

import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.*
import kotlinx.android.synthetic.main.activity_cek_jangkauan.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class CekJangkauanActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private lateinit var data : OfficeAssigned

    //untuk keperluan checkout
    private var presenseId: Int = 0

    private var lastLocation: Location? = null

    var marker : Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_jangkauan)

        setupToolbar()

        data = intent.getParcelableExtra(DATA_OFFICE_KEY)
        presenseId = intent.getIntExtra(PRESENCE_ID_KEY, 0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = LocationManager(this)

        btnNext.setOnClickListener {
            startActivity<CheckinActivity>(
                DATA_OFFICE_KEY to data,
                PRESENCE_ID_KEY to presenseId)
        }

        val kendalaAbsen = SpannableString("Mengalami Kendala? Kirim Laporan")
        kendalaAbsen.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this,
                    R.color.color_kirim_laporan
                )
            ),
            19, 32,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        kendalaAbsen.setSpan(
            StyleSpan(Typeface.BOLD),
            19, 32,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        btnKirimLaporan.text = kendalaAbsen

        btnKirimLaporan.setOnClickListener {
            startActivity<ReportAbsensiActivity>()
        }

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
                .radius(350.0)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(ContextCompat.getColor(this, R.color.colorCircle))
        )


        locationManager.listenLocationUpdate {
            lastLocation = it
            var myLocation = LatLng(it.latitude, it.longitude)
            if (!layoutNext.isVisible) layoutNext.visible()
            marker?.let {
                it.remove()
            }
            marker = mMap.addMarker(MarkerOptions().position(myLocation))
            val officeLocation = Location("office").apply {
                latitude = office.latitude
                longitude = office.longitude
            }
            val distance = officeLocation.distanceTo(it)

            layoutNext.visible()
            val animation = AnimationUtils.loadAnimation(this, R.anim.downtoup)
            layoutNext.animation = animation
            if (distance > 350){
                layoutDiluarJangkauan.visible()
                txtJangkauan.gone()
                btnNext.isEnabled = false
            } else {
                txtJangkauan.visible()
                layoutDiluarJangkauan.gone()
                btnNext.isEnabled = true
            }
        }

    }

    fun setupToolbar(){
        txtTitle.text = "Cek Jangkauan"
        btnBack.setOnClickListener {
            onBackPressed()
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
