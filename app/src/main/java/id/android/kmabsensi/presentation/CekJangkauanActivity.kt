package id.android.kmabsensi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.ajalt.timberkt.d
import com.google.android.gms.maps.GoogleMap
import id.android.kmabsensi.R
import org.jetbrains.anko.toast

class CekJangkauanActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_jangkauan)

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        toast("asd")
        d { "oncreate dipanggil" }
    }

//    override fun onMapReady(p0: GoogleMap?) {
//
//        toast("test")
//
//        p0?.let {
//            mMap = it
//        }
//
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//
//
//    }

//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        airLocation?.onActivityResult(requestCode, resultCode, data)
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
}
