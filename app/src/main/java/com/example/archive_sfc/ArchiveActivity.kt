package com.example.archive_sfc

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.adaptater.AdaptaterImageContenaire
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.constante.ImageParcours
import com.example.archive_sfc.databinding.ActivityArchiveBinding
import com.example.archive_sfc.models.FileState
import com.example.archive_sfc.models.room.*
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModel
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModelFactory
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModel
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModelFactory
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModel
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModelFactory
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModel
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModelFactory
import com.example.archive_sfc.room.status.viewModel.StatusViewModel
import com.example.archive_sfc.room.status.viewModel.StatusViewModelFactory
import com.example.archive_sfc.room.user.viewmodel.UserViewModel
import com.example.archive_sfc.room.user.viewmodel.UserViewModelFactory
import com.example.archive_sfc.utils.FileUtil
import com.example.archive_sfc.utils.chiffrement_de_cesar.chiffreDeCesarCryptageEtDecryptage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalGetImage
@RequiresApi(Build.VERSION_CODES.O)
class ArchiveActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityArchiveBinding
    private lateinit var  recyclerView: RecyclerView
    private lateinit var mDialog: MaterialDialog.Builder
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((application as UserApplication).repositoryDirectory)
    }
    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((application as UserApplication).repositoryInvoiceKey)
    }
    private val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory((application as UserApplication).repositoryInvoice)
    }
    private val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((application as UserApplication).repositoryPicture)
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as UserApplication).repository)
    }
    private val statusViewModel: StatusViewModel by viewModels {
        StatusViewModelFactory((application as UserApplication).repositoryStatus)
    }
    private var userParcour = mutableListOf<User>()
    private var folderId :Int = 0
    private var keyId :Int = 0
    private var invoiceFId = 0
    private var userOn:Int = 0
    private var itemsInvoiceKey = mutableListOf<String>()
    private var itemsFolder = mutableListOf<String>()
    private var itemSubFolder = mutableListOf<String>()

    private var adapterImageContenaire : AdaptaterImageContenaire? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
            recyclerView = mBinding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,true)
            adapterImageContenaire = AdaptaterImageContenaire()
            recyclerView.adapter = adapterImageContenaire
            Toast.makeText(this,"Stock en encours ->${ImageParcours.stdList.size}",Toast.LENGTH_LONG).show()
            adapterImageContenaire?.addImageInContenaire(ImageParcours.stdList)
        val listPicture = pictureViewModel.getAllImages()
        listPicture.observe(this){listOfPicture->
            Toast.makeText(this,"${listOfPicture.size}",Toast.LENGTH_LONG).show()
        }
        invoiceKeyViewModel.allInvoiceKey.observe(this){
            it.forEach {i->
               if(!itemsInvoiceKey.contains(i.Invoicekey)){
                   itemsInvoiceKey.add(i.Invoicekey)
               }
            }
        }
        statusViewModel.allStatus.observe(this){
            userOn = it[0].idUser
            userViewModel.allWords.observe(this){i->
                i.filterTo(userParcour){u->
                    u.UserId == userOn
                }
            }
        }
        directoryViewModel.allDirectory.observe(this){
            it.forEach { i->
                if(!itemsFolder.contains(i.DirectoryName)){
                    itemsFolder.add(i.DirectoryName)
                }
            }
            Log.e("TAG => ","${it.size}")
        }
        invoiceViewModel.allInvoice.observe(this){
            Log.e("TAG invoice => ","${it.size}")
        }
        optionSelect()
        verifySubFolder()
        verifyInvoiceKey()
        calendarWithTextInput()
        storageLocalData()
        launch()
        scannerStart()
        gallery()

    }
    private fun gallery() {
        mBinding.selectGalery.setOnClickListener {
            selectPicture()
        }
    }
    private fun optionSelect(){
        val selectItemsFolder = ArrayAdapter(this,R.layout.itemfolder,itemsFolder)
        mBinding.folderIdSelect.setAdapter(selectItemsFolder)
    }
    private fun launch(){
        mBinding.takePicture.setOnClickListener{
            val intent = Intent(this@ArchiveActivity, CameraActivity::class.java)
            val cr : ArrayList<Picture> = ArrayList()
            intent.putExtra("coding",cr)
            //  cleMax = intent.getIntExtra( "MAX",0)
            //intent.hasExtra("MAX" )
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
    private fun scannerStart(){
        mBinding.InvoiceBareCode.setOnClickListener {
            CameraActivity.startScanner(this){ its ->
                its.forEach{
                    when(it.valueType){
                        Barcode.TYPE_URL ->{
                            mBinding.InvoiceBareCode.setText(it.url.toString())
                        }
                        Barcode.TYPE_CONTACT_INFO ->{
                            mBinding.InvoiceBareCode.setText(it.contactInfo.toString())
                        }
                        else->{
                            mBinding.InvoiceBareCode.setText(it.rawValue.toString())
                        }
                    }
                }
            }
        }
    }
    @Suppress("DEPRECATION")
    private  fun selectPicture(){
        try{
        val takePictureDevise = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        takePictureDevise.type = "image/*"
        takePictureDevise.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        takePictureDevise.action =  Intent.ACTION_GET_CONTENT
        startActivityForResult(takePictureDevise,1)
    }
    catch (e:Exception){
        Log.d("Exception -",e.message.toString())
    }
    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data!!.clipData != null) {
                val count = data.clipData!!.itemCount
                for(i in 0 until count){
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    val file = FileUtil.from(applicationContext,imageUri)
                    GlobalScope.launch {
                        val compressedImageFile = Compressor.compress(applicationContext, file, Dispatchers.Main)
                        val bitmap =  BitmapFactory.decodeFile(compressedImageFile?.path)
                        val fileData = FileState(file.name,bitmap,imageUri,file)
                        ImageParcours.stdList.add(fileData)
                        adapterImageContenaire?.addImageInContenaire(ImageParcours.stdList)
                    }
                }
            }
            else{
                GlobalScope.launch {
                    val imageUri: Uri? = data.data
                    val file = FileUtil.from(applicationContext,imageUri)
                    val compressedImageFile = Compressor.compress(applicationContext, file, Dispatchers.Main)
                    val bitmap =  BitmapFactory.decodeFile(compressedImageFile?.path)
                    val fileData = FileState(file.name,bitmap,imageUri,file)
                    ImageParcours.stdList.add(fileData)
                    adapterImageContenaire?.addImageInContenaire(ImageParcours.stdList)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun pickImagesIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
    }
    @SuppressLint("SetTextI18n")
    private fun calendarWithTextInput() {
        val c = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance()
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val currentDate = LocalDate.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = dateFormat.format(currentDate)
        mBinding.InvoiceExpireDate.setText(formattedDate)
        mBinding.InvoiceDate.setText(formattedDate)
        mBinding.InvoiceDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                mBinding.InvoiceDate.setText("$year-${month + 1}-$dayOfMonth")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        mBinding.InvoiceExpireDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                mBinding.InvoiceExpireDate.setText("$year-${month + 1}-$dayOfMonth")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun verifySubFolder(){
        val itemSub = mutableListOf<Directory>()
        val itemKey = mutableListOf<InvoiceKey>()
        mBinding.folderIdSelect.addTextChangedListener { ed->
            var dynamique :Any? = null
            directoryViewModel.allDirectory.observe(this){
                it.forEach {directory ->
                    if(ed.toString() == directory.DirectoryName){
                        folderId = directory.DirectoryId
                        if(directory.parentId != null){
                            dynamique = directory.parentId
                        }
                    }
                }
                if (dynamique != null){
                    itemSubFolder.clear()
                    itemSub.clear()
                    it.filterTo(itemSub){i->
                        i.DirectoryId == dynamique
                    }
                    itemSub.forEach{item->
                        itemSubFolder.add(item.DirectoryName)
                    }
                    mBinding.subfolder.visibility = View.VISIBLE
                }
                else{
                    itemSub.clear()
                    itemSubFolder.clear()
                    mBinding.subfolderIdSeflect.setText("")
                    mBinding.subfolder.visibility = View.GONE
                }
                val selectItems = ArrayAdapter(applicationContext,R.layout.itemsub,itemSubFolder)
                mBinding.subfolderIdSeflect.setAdapter(selectItems)
            }
            if (folderId != 0){
                itemKey.clear()
                itemsInvoiceKey.clear()
                invoiceKeyViewModel.allInvoiceKey.observe(this){keys->
                    keys.filterTo(itemKey){outInvoice->
                        outInvoice.DirectoryFId == folderId
                    }
                }
                itemKey.forEach {invoiceKey->
                    itemsInvoiceKey.add(invoiceKey.Invoicekey)
                }
            }
            val selectKey = ArrayAdapter(this,R.layout.itemkey,itemsInvoiceKey)
            mBinding.selectKey.setAdapter(selectKey)
        }
    }
    private fun verifyInvoiceKey(){
        mBinding.selectKey.addTextChangedListener {ed->
            invoiceKeyViewModel.allInvoiceKey.observe(this){listKey->
                keyId = 0
                listKey.forEach {
                    if(it.Invoicekey == ed.toString()){
                        keyId = it.InvoicekeyId
                    }
                }
            }
        }
    }

    private fun dialog(title:String="",message:String=""){
       MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun storageLocalData(){
        mBinding.saveData.setOnClickListener {
            try {
                Toast.makeText(this,"${userParcour[0].UserId}",Toast.LENGTH_LONG).show()
                val invoiceCode = mBinding.InvoiceCode.text.toString()
                val invoiceBarCode= mBinding.InvoiceBareCode.text.toString()
                val invoiceDesc = mBinding.InvoiceDesc.text.toString()
//                val invoiceKey = mBinding.selectKey.text.toString()
                val invoiceDate = mBinding.InvoiceDate.text.toString()
                val selectFolder = mBinding.folderIdSelect.toString()
                val selectKey = mBinding.selectKey.text.toString()
                val clientName = mBinding.clientName.text.toString()
                val clientPhone = mBinding.clientPhone.text.toString()
                val expireDate = mBinding.InvoiceExpireDate.text.toString()
                val versionAndroid = Build.VERSION.RELEASE
                val branchFId = userParcour[0].BranchFId as Int
                val userFId: Int = userParcour[0].UserId
                val dateMillisSecond = (SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())).toString()
                var invoiceUniqueId = chiffreDeCesarCryptageEtDecryptage(key = 8,mot = userParcour[0].username )
                invoiceUniqueId += dateMillisSecond
                if(invoiceCode.isNotEmpty() && selectFolder.isNotEmpty() && selectKey.isNotEmpty()){
                    //
                    if(keyId != 0){
                        if(ImageParcours.stdList.size != 0){
                            val invoice = Invoice(
                                InvoiceId = 0,
                                invoiceCode = invoiceCode,
                                invoiceDesc = invoiceDesc,
                                invoiceBarCode = invoiceBarCode,
                                UserFId = userFId,
                                DirectoryFId = folderId,
                                BranchFId = branchFId,
                                invoiceDate = invoiceDate,
                                InvoicekeyFId = keyId,
                                invoicePath = "__",
                                androidVersion = versionAndroid,
                                invoiceUniqueId = invoiceUniqueId,
                                clientName = clientName,
                                clientPhone = clientPhone,
                                expiredDate = expireDate
                            )
                            invoiceViewModel.insert(invoice)

                                savePicture(invoiceUniqueId)

                        }
                    }
                    else{
                        dialog("Error","Keys invalidate *")
                    }
                }
                else{
                    dialog("Warning","Some fields cannot be empty try again entering values in the input boxes *")
                }
            }
            catch (e:Exception){
                dialog("Exception detected","${e.message} *")
            }
        }
    }
    private fun savePicture(invoiceUniqueId: String) : Boolean{
        var status = false
        var i = 0
        invoiceViewModel.allInvoice.observe(this){
        it.forEach{invoice ->
            i++
                if (invoice.invoiceUniqueId == invoiceUniqueId){
                    invoiceFId = invoice.InvoiceId
                    ImageParcours.stdList.forEach {img->
                        val bitmap = img.bitmap
                        val picture = Picture(
                            PictureId = 0,
                            InvoiceFId = invoiceFId,
                            pictureName = img.fileName,
                            picturePath = img.fileContent!!.path,
                            PublicUrl = "",
                            contentFile = bitmap
                        )
                        pictureViewModel.insert(picture)
                    }
                    ImageParcours.stdList.clear()
                    val intent = Intent(this@ArchiveActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                if(it.size -1 == i){
                    status = true
                }
             }
        }
        return status

    }
//    private fun fileToByteArray(file: File): ByteArray {
//        val inputStream = FileInputStream(file)
//        val byteArray = inputStream.readBytes()
//        inputStream.close()
//        return byteArray
//    }

    private fun mDialogConnexion(title:String ="",message: String = "",animation :Int = 0 ){
        mDialog =  MaterialDialog.Builder(this)
        if(animation != 0)
            mDialog.setAnimation(animation)
        mDialog.setTitle(title)
        mDialog.setMessage(message)
        mDialog.setPositiveButton("Ok"){ dialog, _ ->
            val intent = Intent(this@ArchiveActivity, MainActivity::class.java)
//           intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        mDialog.build().show()
    }
}