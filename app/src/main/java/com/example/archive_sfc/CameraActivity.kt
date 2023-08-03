package com.example.archive_sfc

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.archive_sfc.constante.ImageParcours
import com.example.archive_sfc.databinding.ActivityCameraBinding
import com.example.archive_sfc.models.FileState
import com.example.archive_sfc.utils.FileUtil
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
@ExperimentalGetImage
class CameraActivity : AppCompatActivity() {
    private  lateinit var mBinding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var imageAnalysis : ImageAnalysis
    private lateinit var processCameraProvider :ProcessCameraProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val verify = resultLaunch()
            verify.launch(arrayOf(
                Manifest.permission.CAMERA
            ))
        }
        else{
            startCamera(cameraFacing)
        }
    }

    private fun startCamera(cameraFacing:Int){
        val aspectRatio = AspectRatio.RATIO_4_3
        val listenableFuture = ProcessCameraProvider.getInstance(this)
        listenableFuture.addListener(
            {
                try {
                    val cameraProvider: ProcessCameraProvider = listenableFuture.get() as ProcessCameraProvider
                    val preview = Preview.Builder().setTargetAspectRatio(aspectRatio).build()
                    imageCapture = ImageCapture.Builder().setCaptureMode(
                        ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetRotation(
                        Surface.ROTATION_0).build()
                    val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraFacing).build()
                    cameraProvider.unbindAll()
                    val camera: Camera = cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)

                    bindImageAnalysis(cameraSelector)
                    toggleCameraEvent()
                    toggleFlashEvent(camera)
                    startCaptureEvent()
                    preview.setSurfaceProvider(mBinding.preview.surfaceProvider)
                }
                catch (e:Exception){
                    Toast.makeText(this,"${e.message}", Toast.LENGTH_LONG).show()
                }
            }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindImageAnalysis(cameraSelector: CameraSelector) {
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        )
        imageAnalysis = ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor){
            processImageProxy(scanner,it)
        }
      processCameraProvider.bindToLifecycle(this,cameraSelector,imageAnalysis)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun processImageProxy(scanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!,imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage).addOnSuccessListener {
            if(it.isNotEmpty())
                onScan?.invoke(it)
                onScan = null
                finish()
        }.addOnFailureListener{
                it.printStackTrace()
        }.addOnCompleteListener {
            imageProxy.close()
        }
    }
companion object{
    private var onScan :((barcode:List<Barcode>)->Unit)?= null
    fun startScanner(context: Context, onScan:(barcode:List<Barcode>)->Unit) {
        this.onScan = onScan
        Intent(context,CameraActivity::class.java).also {
            context.startActivity(it)
        }
    }
}
    private  fun startCaptureEvent(){
        mBinding.cameraTake.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val verify = resultLaunch()
                verify.launch(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
            else{
                takePhoto()
            }
        }
    }

    private fun toggleFlashEvent(camera: Camera){
        mBinding.flash.setOnClickListener {
            setFlashIcon(camera)
        }
    }

    private fun setFlashIcon(camera: Camera) {
        if(camera.cameraInfo.hasFlashUnit()){
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                mBinding.flash.setImageResource(R.drawable.round_flash_off_24)
            }
            else {
                camera.cameraControl.enableTorch(false)
                mBinding.flash.setImageResource(R.drawable.round_flash_on_24)

            }

        }
        else{
            Thread {
                Toast.makeText(
                    applicationContext,
                    "Flash is not available currently",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

//    private fun takePicture(imageCapture: ImageCapture) {
//        val file = File(Environment.getExternalStorageDirectory(),"${System.currentTimeMillis()}.jpg")
//        val outputfileOption = ImageCapture.OutputFileOptions.Builder(file).build()
//        imageCapture.takePicture(/* outputFileOptions = */ outputfileOption, /* executor = */
//            Executors.newCachedThreadPool(),
//            /* imageSavedCallback = */
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(error: ImageCaptureException)
//                {
//                    // insert your code here.
//                    Thread {
//                        Toast.makeText(applicationContext, "Failed ", Toast.LENGTH_LONG).show()
//                        Log.d("TEST DATA ERROR SAVE = ", "${error.message}")
//                    }
//                    Toast.makeText(applicationContext, "Failed ", Toast.LENGTH_LONG).show()
//                    startCamera(cameraFacing)
//                }
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    // insert your code here.
//                    Thread {
//                        Toast.makeText(
//                            applicationContext,
//                            "Save image ${file.path}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                    Toast.makeText(
//                        applicationContext,
//                        "Save image ${file.path}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Log.d("TEST DATA = ","${file.path}")
//                    startCamera(cameraFacing)
//                }
//            })
//    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val uriSave: Uri? =  output.savedUri
                    val file = FileUtil.from(applicationContext,uriSave)
                    val msg = "Photo capture succeeded: ${file.name}"
                    val fileData = FileState(file.name,file,uriSave)
                    ImageParcours.stdList.add(fileData)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CameraActivity, ArchiveActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    Log.d("", msg)
                }
            }
        )
    }
//    private fun aspectRatio(width: Int, height: Int): Int {
//        val previewRatio = (width.coerceAtLeast(height) + 1 / width.coerceAtMost(height) + 1).toFloat()
//        return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0))
//            AspectRatio.RATIO_4_3
//        else
//            AspectRatio.RATIO_16_9
//    }
    private fun toggleCameraEvent(){
        mBinding.cameraReturn.setOnClickListener {
            cameraFacing = if(cameraFacing == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK
        }
    }
    private fun resultLaunch(): ActivityResultLauncher<Array<String>> {
        val registerForResult: ActivityResultLauncher<Array<String>> = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.forEach{

                if (!it.value) {
                    Toast.makeText(applicationContext,"Need permission for action", Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }
                Toast.makeText(applicationContext,"Permissions are Granted for action", Toast.LENGTH_LONG).show()
            }

        }
        return registerForResult
    }
}