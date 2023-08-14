package com.example.archive_sfc.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.archive_sfc.CameraActivity
import com.example.archive_sfc.R
import com.example.archive_sfc.constante.ImageParcours
import com.example.archive_sfc.databinding.FragmentCameraBinding
import com.example.archive_sfc.models.FileState
import com.example.archive_sfc.utils.FileUtil
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalGetImage
class CameraFragment : Fragment() {
    private  lateinit var mBinding: FragmentCameraBinding
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private var imageCapture: ImageCapture? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val verify = resultLaunch()
            verify.launch(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
        }
        else{
            startCamera(cameraFacing)
        }
    }

    private fun startCamera(cameraFacing:Int){
        val aspectRatio = AspectRatio.RATIO_4_3
        val listenableFuture = ProcessCameraProvider.getInstance(requireContext())
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
                    toggleFlashEvent(camera)
                    startCaptureEvent()
                    preview.setSurfaceProvider(mBinding.preview.surfaceProvider)
                }
                catch (e:Exception){
                    Toast.makeText(requireContext(),"${e.message}", Toast.LENGTH_LONG).show()
                }
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }
    @SuppressLint("SuspiciousIndentation")
    private fun processImageProxy(scanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!,imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage).addOnSuccessListener {
            if(it.isNotEmpty())
                CameraActivity.onScan?.invoke(it)
            CameraActivity.onScan = null
        }.addOnFailureListener{
            it.printStackTrace()
        }.addOnCompleteListener {
            imageProxy.close()
        }
    }
    private  fun startCaptureEvent(){
        mBinding.cameraTake.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
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
                    requireContext(),
                    "Flash is not available currently",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

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
            .Builder(activity!!.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    GlobalScope.launch {
                        val uriSave: Uri? =  output.savedUri
                        val file = FileUtil.from(requireContext(),uriSave)
                        val msg = "Photo capture succeeded: ${file.name}"
                        val compressedImageFile = Compressor.compress(requireContext(), file, Dispatchers.Main)
                        val bitmap =  BitmapFactory.decodeFile(compressedImageFile?.path)
                        val fileData = FileState(file.name,bitmap,uriSave,file)
                        ImageParcours.stdList.add(fileData)
                        Log.d("", msg)
                        //findNavController().navigate(R.id.action_cameraFragment_to_archiveEditFragment)
                    }
                    Toast.makeText(requireContext(), "Photo capture succeeded", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun resultLaunch(): ActivityResultLauncher<Array<String>> {
        val registerForResult: ActivityResultLauncher<Array<String>> = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.forEach{
                if (!it.value) {
                    Toast.makeText(requireContext(),"Need permission for action", Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }
                Toast.makeText(requireContext(),"Permissions are Granted for action", Toast.LENGTH_LONG).show()
            }
        }
        return registerForResult
    }

}