package com.example.archive_sfc.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
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
import com.example.archive_sfc.R
import com.example.archive_sfc.databinding.FragmentCameraBinding
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs

class CameraFragment : Fragment() {
    //private lateinit var viewModel: CameraViewModel
    private  lateinit var mBinding: FragmentCameraBinding
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
// viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
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

        super.onViewCreated(view, savedInstanceState)
    }
    private fun startCamera(cameraFacing:Int){
        val aspectRatio = aspectRatio(mBinding.preview.width,mBinding.preview.height)
        val listenableFuture = ProcessCameraProvider.getInstance(requireContext())
        listenableFuture.addListener(
            {
                try {
                    val cameraProvider: ProcessCameraProvider = listenableFuture.get() as ProcessCameraProvider
                    val preview = Preview.Builder().setTargetAspectRatio(aspectRatio).build()
                    val imageCapture: ImageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetRotation(
                        Surface.ROTATION_0).build()
                    val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraFacing).build()
                    cameraProvider.unbindAll()
                    val camera: Camera = cameraProvider.bindToLifecycle(requireActivity(),cameraSelector,preview,imageCapture)
                    toggleCameraEvent()
                    toggleFlashEvent(camera)
                    startCaptureEvent(imageCapture)
                    preview.setSurfaceProvider(mBinding.preview.surfaceProvider)
                }
                catch (e:Exception){
                    Toast.makeText(requireContext(),"${e.message}",Toast.LENGTH_LONG).show()
                }
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private  fun startCaptureEvent(imageCapture: ImageCapture){
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
                takePicture(imageCapture)
            }

        }
    }

    private fun toggleFlashEvent(camera: Camera ){
        mBinding.flash.setOnClickListener {
            setFlashIcon(camera)
        }
    }

    private fun setFlashIcon(camera: Camera ) {
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

    private fun takePicture(imageCapture: ImageCapture) {
        val file = File(Environment.getExternalStorageDirectory(),"${System.currentTimeMillis()}.jpg")
        val outputfileOption = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(/* outputFileOptions = */ outputfileOption, /* executor = */
            Executors.newCachedThreadPool(),
            /* imageSavedCallback = */
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException)
                {
                    // insert your code here.
                    Thread {
                        Toast.makeText(requireContext(), "Failed ", Toast.LENGTH_LONG).show()
                    }
                    startCamera(cameraFacing)
                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // insert your code here.
                    Thread {
                        Toast.makeText(
                            requireContext(),
                            "Save image ${file.path}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    startCamera(cameraFacing)
                }
            })
    }
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = (width.coerceAtLeast(height) / width.coerceAtMost(height)).toFloat()
        return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0))
            AspectRatio.RATIO_4_3
        else
            AspectRatio.RATIO_16_9
    }
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
                    Toast.makeText(requireContext(),"Need permission for action",Toast.LENGTH_LONG).show()
                return@registerForActivityResult
                }
                Toast.makeText(requireContext(),"Permissions are Granted for action",Toast.LENGTH_LONG).show()
            }

        }
    return registerForResult
    }

}