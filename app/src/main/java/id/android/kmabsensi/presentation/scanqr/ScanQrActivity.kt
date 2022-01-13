package id.android.kmabsensi.presentation.scanqr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.zxing.Result
import id.android.kmabsensi.R
import id.android.kmabsensi.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scan_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanQrActivity : BaseActivity(), ZXingScannerView.ResultHandler, View.OnClickListener {

    private lateinit var mScannerView: ZXingScannerView

    companion object {
        const val RESULT_CODE = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
        setupToolbar("Scan QR")
        initScannerView()
    }

    override fun onStart() {
        mScannerView.startCamera()
        doRequestPermission()
        super.onStart()
    }

    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }

    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                initScannerView()
            }
            else -> {
                /* nothing to do in here */
            }
        }
    }


    override fun handleResult(rawResult: Result?) {
        try {
            val result = rawResult?.text

            result.let {
                if (it?.toLowerCase()?.contains("kampungmarketer")!!){
                    val parsedData = it?.split("/")
                    val poin = parsedData?.last()?.toInt()
                    val intent = Intent()
                    intent.putExtra(getString(R.string.qrdata), poin)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this, "QrCode Tidak valid", Toast.LENGTH_LONG).show()
                    mScannerView.resumeCameraPreview(this)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "QrCode Tidak valid", Toast.LENGTH_LONG).show()
            mScannerView.resumeCameraPreview(this)
        }
    }


    private fun initScannerView() {
        mScannerView = ZXingScannerView(this)
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        frame_layout_camera.addView(mScannerView)
    }

    override fun onClick(v: View?) {

    }

}