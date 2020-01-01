package id.android.kmabsensi.presentation.camera


import androidx.fragment.app.Fragment


/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment() {

//    private lateinit var photoFile : File
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_camera, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupCamera()
//
//        ivCaptureButton.setOnClickListener {
//            camera.capture()
//        }
//
//        btnCancel.setOnClickListener {
//            hidePreview()
//            camera.visible()
//            camera.start()
//            ivCaptureButton.visible()
//        }
//
//        btnDone.setOnClickListener {
//            val intent = Intent()
//            intent.putExtra("image", photoFile)
//            activity?.setResult(Activity.RESULT_OK, intent)
//            activity?.finish()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CAMERA
//            ) === PackageManager.PERMISSION_GRANTED
//        ) {
//            camera.start()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        camera.stop()
//    }
//
//    private fun setupCamera() {
//
//
//        camera.addCameraOpenedListener {
//            Timber.i("Camera opened.")
//        }
//        camera.addPictureTakenListener { image: Image ->
//            savePhotoAndPreview(image)
//        }
//
//        camera.addCameraErrorListener { t: Throwable?, errorLevel: ErrorLevel? ->
//            if (errorLevel is Warning) Timber.w(t) else if (errorLevel is ErrorLevel.Error) Timber.e(
//                t
//            )
//        }
//
//        camera.addCameraClosedListener {
//            Timber.i("Camera closed.")
//        }
//    }
//
//    private fun savePhotoAndPreview(image: Image) {
//        photoFile = File(
//            Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES
//            ).absolutePath, "checkinphoto.jpg"
//        )
//        if (photoFile.exists()) {
//            photoFile.delete()
//        }
//        try {
//            val fos = FileOutputStream(photoFile.path)
//            fos.write(image.data)
//            fos.close()
//            camera.stop()
//            camera.gone()
//            showPreview()
//            Glide.with(context!!)
//                .load(photoFile)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(imgPreview)
//
//        } catch (e: IOException) {
//            Log.e("PictureDemo", "Exception in photoCallback", e)
//        }
//
//    }
//
//    private fun showPreview(){
//        imgPreview.visible()
//        btnDone.visible()
//        btnCancel.visible()
//
//        ivCaptureButton.gone()
//
//    }
//
//    private fun hidePreview(){
//        imgPreview.gone()
//        btnDone.gone()
//        btnCancel.gone()
//    }


}
