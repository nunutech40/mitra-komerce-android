package id.android.kmabsensi.presentation.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wildma.idcardcamera.camera.CameraPreview
import com.wildma.idcardcamera.camera.CameraUtils
import com.wildma.idcardcamera.camera.IDCardCamera
import com.wildma.idcardcamera.utils.FileUtils
import com.wildma.idcardcamera.utils.ImageUtils
import com.wildma.idcardcamera.utils.PermissionUtils
import id.android.kmabsensi.R
import id.android.kmabsensi.utils.DIR_ROOT
import id.android.kmabsensi.utils.IMG_DIRECTORY_NAME
import id.android.kmabsensi.utils.compressCustomerCaptureImage
import id.android.kmabsensi.utils.deleteCaptureFileFromPath
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.camera_confirmation.*
import org.joda.time.DateTime
import java.io.File

class CameraActivity : AppCompatActivity() {

//    private val permissions =
//        arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        )


    private var mCropBitmap: Bitmap? = null
    private var mType: Int = 0//Shooting type
    private var isToast =
        true//Whether to play the toast, in order to ensure that the for loop only plays once
    private var mCameraView: CameraPreview? = null
    private var capturedFilePath: String? = null
    private lateinit var photoFile : File
    val FLIP_VERTICAL = 1
    val FLIP_HORIZONTAL = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val checkPermissionFirst = PermissionUtils.checkPermissionFirst(
            this, IDCardCamera.PERMISSION_CODE_FIRST,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )

        if (checkPermissionFirst) {
            initCamera()
        }
        btn_take_picture.setOnClickListener {
            takePhoto()
        }

        btn_confirmCrop.setOnClickListener {
            disabledConfirmButton()
            confirmImage(mCropBitmap!!)
        }

        btn_cancelCrop.setOnClickListener {
            reCaptureImage()
        }


//        val cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//        val writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//
//        if (cameraPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED
//        ) supportFragmentManager.beginTransaction().replace(
//            R.id.container,
//            CameraView()
//        ).commit() else ActivityCompat.requestPermissions(this, permissions, 1)

    }

    private fun disabledConfirmButton() {
        btn_confirmCrop.isEnabled = false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isPermissions = true
        for (i in 0 until permissions.size - 1) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    if (isToast) {
                        Toast.makeText(
                            this,
                            "Please manually open the permissions required for the app",
                            Toast.LENGTH_SHORT
                        ).show()
                        isToast = false
                    }
                }
            }
        }
        isToast = true
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "Allow all permissions")
            initCamera()
        } else {
            Log.d(
                "onRequestPermission",
                "onRequestPermissionsResult: " + "Have permission not allowed"
            )
            finish()
        }
    }

    private fun initCamera() {
        try {
            if (CameraUtils.hasCamera(this@CameraActivity)) {
                mIvCameraCrop.visibility = View.GONE
                mCameraView = findViewById(R.id.mCameraPreview)
                mCameraView!!.setCameraId(1)
                mType = IDCardCamera.TYPE_IDCARD_BACK
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                Handler().postDelayed({
                    camera_preview_layout.visibility = View.VISIBLE
                    mCameraView!!.visibility = View.VISIBLE
                }, 200)
            } else {
                btn_take_picture.visibility = View.GONE
                Toast.makeText(this, "Camera tidak tersedia", Toast.LENGTH_LONG).show()
            }
        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }

    private fun reCaptureImage() {
        try {
            show_image_layout.visibility = View.GONE
            camera_preview_layout.visibility = View.VISIBLE
            mCameraPreview.setCameraId(1)
            mCameraPreview.isEnabled = true
            mCameraPreview.addCallback()
            mCameraPreview.startPreview()
        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }

    private fun confirmImage(mCropBitmap: Bitmap) {
        try {
            if (mCropBitmap == null) {
                Toast.makeText(
                    applicationContext,
                    "Pemotongan gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (FileUtils.createOrExistsDir(DIR_ROOT)) {
                val buffer = StringBuffer()
                var imagePath = ""
                if (mType == IDCardCamera.TYPE_IDCARD_BACK) {
                    imagePath = buffer.append(DIR_ROOT).append(IMG_DIRECTORY_NAME).append("/")
                        .append(DateTime.now().toString() + "jpeg").toString()
                }

                if (ImageUtils.save(mCropBitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                    capturedFilePath =
                        compressCustomerCaptureImage(this, imagePath)
                    deleteCaptureFileFromPath(imagePath)

                    photoFile = File(capturedFilePath)
                    val intent = Intent()
                    intent.putExtra("image", photoFile)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }


        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }

    @Suppress("DEPRECATION")
    private fun takePhoto() {
        try {
            mCameraPreview!!.isEnabled = false
            CameraUtils.getCamera().setOneShotPreviewCallback { data, camera ->
                val size = camera!!.parameters.previewSize
                camera.stopPreview()
                Thread {
                    val w = size.width
                    val h = size.height
                    val bitmap = ImageUtils.getBitmapFromByte(data, w, h)
                    //cropImage(bitmap)
                    mCropBitmap = rotateImage(bitmap!!, 270f)
                    mCropBitmap = flipImage(mCropBitmap!!, FLIP_HORIZONTAL)
                    customCropImage(mCropBitmap!!)
                }.start()
            }
        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }


    private fun customCropImage(mCropBitmap: Bitmap) {
        try {
            runOnUiThread {
                setCropLayout()
                captured_image.setImageBitmap(mCropBitmap)
            }
        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }

    private fun setCropLayout() {
        try {
            show_image_layout.visibility = View.VISIBLE
            camera_preview_layout.visibility = View.GONE
        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }

    override fun onStart() {
        super.onStart()
        if (mCameraView != null) {
            mCameraView!!.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (mCameraView != null) {
            mCameraView!!.onStop()
        }
    }

    fun flipImage(source: Bitmap, type: Int): Bitmap {

        // create new matrix for transformation
        val matrix = Matrix()
        // if vertical
        if (type == FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f)
        }
        // if horizonal
        if (type == FLIP_HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f)
            // unknown type
        }
        return Bitmap.createBitmap(
            source,
            0,
            0,
            source.getWidth(),
            source.getHeight(),
            matrix,
            true
        )
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun enabledConfirmButton() {
        btn_confirmCrop.isEnabled = true
    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>, grantResults: IntArray
//    ) {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) === PackageManager.PERMISSION_GRANTED
//        ) supportFragmentManager.beginTransaction().replace(
//            R.id.container,
//            CameraFragment()
//        ).commit() else ActivityCompat.requestPermissions(this, permissions, 1)
//    }

}
