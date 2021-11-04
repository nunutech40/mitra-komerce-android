package id.android.kmabsensi.presentation.camera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.wildma.idcardcamera.camera.CameraPreview
import com.wildma.idcardcamera.camera.CameraUtils
import com.wildma.idcardcamera.camera.IDCardCamera
import com.wildma.idcardcamera.utils.FileUtils
import com.wildma.idcardcamera.utils.ImageUtils
import com.wildma.idcardcamera.utils.PermissionUtils
import dmax.dialog.SpotsDialog
import id.android.kmabsensi.R
import id.android.kmabsensi.databinding.ActivityCameraBinding
import id.android.kmabsensi.utils.DIR_ROOT
import id.android.kmabsensi.utils.IMG_DIRECTORY_NAME
import id.android.kmabsensi.utils.compressCustomerCaptureImage
import id.android.kmabsensi.utils.deleteCaptureFileFromPath
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.camera_confirmation.*
import org.joda.time.DateTime
import java.io.File

class CameraActivity : AppCompatActivity() {
    private var mCropBitmap: Bitmap? = null
    private var mType: Int = 0//Shooting type
    private var isToast =
        true//Whether to play the toast, in order to ensure that the for loop only plays once
    private var mCameraView: CameraPreview? = null
    private var capturedFilePath: String? = null
    private lateinit var photoFile : File
    val FLIP_VERTICAL = 1
    val FLIP_HORIZONTAL = 2

    private lateinit var waitingDialog : AlertDialog
    private var handler = Handler(mainLooper)
    private val dialog by lazy {
        MaterialDialog(this).apply {
            cornerRadius(16f)
            customView(
                R.layout.dialog_retake_foto,
                scrollable = false,
                horizontalPadding = true,
                noVerticalPadding = true
            )
        }
    }

    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        delayTakePhoto()

    }

    private fun delayTakePhoto(){
        handler.postDelayed({
            takePhoto()
        }, 5000)
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
            initCamera()
        } else {
            finish()
        }
    }

    private fun initCamera() {
        waitingDialog = SpotsDialog.Builder().setContext(this)
            .setMessage("Pemindaian wajah...")
            .setCancelable(false)
            .build()

        try {
            if (CameraUtils.hasCamera(this@CameraActivity)) {
//                mCameraView = findViewById(R.id.mCameraPreview)
                binding.mCameraPreview.setCameraId(1)
                mType = IDCardCamera.TYPE_IDCARD_BACK
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                Handler(mainLooper).postDelayed({
                    binding.cameraPreviewLayout.visibility = View.VISIBLE
                    binding.mCameraPreview.visibility = View.VISIBLE
                }, 200)
            } else {
                Toast.makeText(this, "Camera tidak tersedia", Toast.LENGTH_LONG).show()
            }
        } catch (ex: Exception) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }

    }

    private fun reCaptureImage() {
        try {
            binding.cameraPreviewLayout.visibility = View.VISIBLE
            binding.mCameraPreview.setCameraId(1)
            binding.mCameraPreview.isEnabled = true
            binding.mCameraPreview.addCallback()
            binding.mCameraPreview.startPreview()
            delayTakePhoto()
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
                var imagePath = ""

                val cw = ContextWrapper(applicationContext)
                val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
                val file = File(directory, DateTime.now().millis.toString() + ".jpg")

                imagePath = file.path

                if (ImageUtils.save(mCropBitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                    capturedFilePath =
                        compressCustomerCaptureImage(this, imagePath)
                    deleteCaptureFileFromPath(imagePath)
                    photoFile = File(capturedFilePath)
                    Log.d("TAGTAGTAG", "confirmImage: $photoFile, $mCropBitmap")
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
            waitingDialog.show()
            mCameraPreview!!.isEnabled = false
            CameraUtils.getCamera().setOneShotPreviewCallback { data, camera ->
                val size = camera!!.parameters.previewSize
                camera.stopPreview()
                Thread {
                    val w = size.width
                    val h = size.height
                    val bitmap = ImageUtils.getBitmapFromByte(data, w, h)
//                    cropImage(bitmap)
                    mCropBitmap = rotateImage(bitmap!!, 270f)
                    mCropBitmap = flipImage(mCropBitmap!!, FLIP_HORIZONTAL)
//                    customCropImage(mCropBitmap!!)
                    runFaceDetector(mCropBitmap)
                }.start()
            }
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

    override fun onStop() {
        super.onStop()
        if (mCameraView != null) {
            mCameraView!!.onStop()
        }
        handler.removeCallbacksAndMessages(null)
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

    private fun runFaceDetector(bitmap: Bitmap?) {
        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
        val option = FirebaseVisionFaceDetectorOptions.Builder()
            .build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(option)

        detector.detectInImage(image)
            .addOnSuccessListener{ result -> processFaceResult(result)}
            .addOnFailureListener{ e-> Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
    }

    private fun processFaceResult(result: List<FirebaseVisionFace>) {
        var count = 0
        for (face in result){
            count++
        }
        waitingDialog.dismiss()
        if (count>0){
            confirmImage(mCropBitmap!!)
        }else{
            showDialogReTakeFoto()
        }
    }

    fun showDialogReTakeFoto() {
        val customView = dialog.getCustomView()
        val btn_retake = customView.findViewById<Button>(R.id.btn_retake)
        btn_retake.setOnClickListener {
            dialog.dismiss()
            reCaptureImage()
        }
        dialog.cancelOnTouchOutside(false)
        dialog.show()
    }

}
