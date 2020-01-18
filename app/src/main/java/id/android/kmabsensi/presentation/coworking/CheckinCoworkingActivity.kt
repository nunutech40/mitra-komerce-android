package id.android.kmabsensi.presentation.coworking

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import id.android.kmabsensi.R
import id.android.kmabsensi.data.remote.response.CoworkingSpace
import id.android.kmabsensi.data.remote.response.UserCoworkingSpace
import id.android.kmabsensi.presentation.base.BaseActivity
import id.android.kmabsensi.presentation.home.HomeViewModel
import id.android.kmabsensi.utils.*
import id.android.kmabsensi.utils.ui.MyDialog
import kotlinx.android.synthetic.main.activity_checkin_coworking.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckinCoworkingActivity : BaseActivity(), OnMapReadyCallback {

    private val vm: HomeViewModel by viewModel()

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    private lateinit var coworking : UserCoworkingSpace

    private var lastLocation: Location? = null

    var marker : Marker? = null

    lateinit var myDialog : MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin_coworking)

        setToolbarTitle("Cek Jangkauan")

        coworking = intent.getParcelableExtra("coworking")
        myDialog = MyDialog(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = LocationManager(this)

        btnNext.setOnClickListener {
            vm.checkInCoworkingSpace(coworking.id)
        }

        vm.checkInCoworkingSpace.observe(this, Observer {
            when (it) {
                is UiState.Loading -> {
                    myDialog.show()
                }
                is UiState.Success -> {
                    myDialog.dismiss()
                    if (it.data.status) {
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        createAlertError(this, "Failed", it.data.message)
                    }

                }
                is UiState.Error -> {
                    myDialog.dismiss()
                    Timber.e(it.throwable)
                }
            }
        })

    }

    override fun onMapReady(p0: GoogleMap?) {
        p0?.let {
            mMap = it
        }

        // Add a marker in Sydney and move the camera
        val office = LatLng(coworking.lat.toDouble(), coworking.lng.toDouble())
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
            val officeLocation = Location("coworking").apply {
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

    override fun onResume() {
        super.onResume()
        locationManager.startLocationUpdate()
    }

    override fun onStop() {
        super.onStop()
        locationManager.stopLocationUpdate()
    }
}
