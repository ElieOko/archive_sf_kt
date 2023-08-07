package com.example.archive_sfc

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.ExperimentalGetImage
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.adaptater.AdaptaterImageContenaire
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.constante.ImageParcours
import com.example.archive_sfc.databinding.ActivityArchiveBinding
import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.models.room.InvoiceKey
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModel
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModelFactory
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModel
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModelFactory
import com.example.archive_sfc.utils.chiffrement_de_cesar.chiffreDeCesarCryptageEtDecryptage
import com.google.mlkit.vision.barcode.common.Barcode

@ExperimentalGetImage class ArchiveActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityArchiveBinding
    private lateinit var  recyclerView: RecyclerView
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((application as UserApplication).repositoryDirectory)
    }
    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((application as UserApplication).repositoryInvoiceKey)
    }
    var itemsInvoiceKey = mutableListOf<String>()
    var itemsFolder = mutableListOf<String>()
    var itemSubFolder = mutableListOf<String>()

    private var adapterImageContenaire : AdaptaterImageContenaire? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        recyclerView = mBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true)
        adapterImageContenaire = AdaptaterImageContenaire()
        recyclerView.adapter = adapterImageContenaire
        adapterImageContenaire?.addImageInContenaire(ImageParcours.stdList)
        invoiceKeyViewModel.allInvoiceKey.observe(this){
            it.forEach {i->
               if(!itemsInvoiceKey.contains(i.Invoicekey)){
                   itemsInvoiceKey.add(i.Invoicekey)
               }

            }
        }
        directoryViewModel.allDirectory.observe(this){
            it.forEach { i->
                if(!itemsFolder.contains(i.DirectoryName)){
                    itemsFolder.add(i.DirectoryName)
                }
            }
        }
        optionSelect()
        verifySubFolder()
        calendarWithTextInput()
        storageLocalData()
        launch()
        scannerStart()
    }
    private fun optionSelect(){
        val selectItemsFolder = ArrayAdapter(this,R.layout.itemfolder,itemsFolder)
        mBinding.folderIdSelect.setAdapter(selectItemsFolder)
    }
    private fun launch(){
        mBinding.takePicture.setOnClickListener{
            val intent = Intent(this@ArchiveActivity, CameraActivity::class.java)
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

    @SuppressLint("SetTextI18n")
    private fun calendarWithTextInput() {
        val c = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance()
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        mBinding.InvoiceDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                mBinding.InvoiceDate.setText("$dayOfMonth/${month + 1}/$year")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        mBinding.InvoiceExpireDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                mBinding.InvoiceExpireDate.setText("$dayOfMonth/${month + 1}/$year")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun verifySubFolder(){
        var itemSub = mutableListOf<Directory>()
        var itemKey = mutableListOf<InvoiceKey>()
        mBinding.folderIdSelect.addTextChangedListener {ed->
                //mBinding.subfolderIdSeflect.visibility = View.VISIBLE
                var dynamique :Any? = null
                var key :Int? = null
                mBinding.subfolderIdSeflect.setText("")
                directoryViewModel.allDirectory.observe(this){
                    it.forEach {directory ->
                        if(ed.toString() == directory.DirectoryName){
                            key = directory.DirectoryId
                            if(directory.parentId != null)
                            {
                                dynamique =  directory.parentId
//                                Toast.makeText(this,"$dynamique run",Toast.LENGTH_LONG).show()
                            }
                            else{
                                mBinding.subfolderIdSeflect.setText("")
//                                Toast.makeText(this,"no run",Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    if(dynamique != null){
                        itemSubFolder.clear()
                        itemSub.clear()
                        it.filterTo(itemSub){i->
                            i.DirectoryId == dynamique.toString().toInt()
                        }
                            itemSub.forEach{item->
                                itemSubFolder.add(item.DirectoryName)
                            }
                        mBinding.subfolderIdSeflect.setText(itemSub[0].DirectoryName)
                        mBinding.subfolder.visibility = View.VISIBLE
                    }
                    else{
                        mBinding.subfolder.visibility = View.GONE
                        itemSub.clear()
                        itemSubFolder.clear()
                    }
                }
                if (key != null){
                    itemKey.clear()
                    invoiceKeyViewModel.allInvoiceKey.observe(this){keys->
                        keys.filterTo(itemKey){outInvoice->
                            outInvoice.DirectoryFId == key
                        }
                    }
                    itemKey.forEach {invoiceKey->
                        itemsInvoiceKey.add(invoiceKey.Invoicekey)
                    }
                }
                val selectItems = ArrayAdapter(applicationContext,R.layout.itemsub,itemSubFolder)
                mBinding.subfolderIdSeflect.setAdapter(selectItems)
                val selectKey = ArrayAdapter(this,R.layout.itemkey,itemKey)
                mBinding.selectKey.setAdapter(selectKey)
            }
    }



    private fun storageLocalData(){
        mBinding.saveData.setOnClickListener {
//            val invoiceCode = mBinding.InvoiceCode.text.toString()
//            val bareCodeInvoice = mBinding.InvoiceBareCode.text.toString()
              val invoiceDesc = mBinding.InvoiceDesc.text.toString()
//            val dateInvoice = mBinding.InvoiceDate.text.toString()
//            var selectFolder = mBinding.folderIdSelect.toString()
//            var selectKey = mBinding.selectKey.text.toString()
//            val clientName = mBinding.clientName.text.toString()
//            val clientPhone = mBinding.clientPhone.text.toString()
//            val expireDate = mBinding.InvoiceExpireDate.text.toString()
//            val versionAndroid = Build.VERSION.RELEASE
//            val branchFId = Data.user?.BranchFId
//            val userFId = Data.user?.id
              val invoiceUniqueId =
                  Data.user?.let { it1 -> chiffreDeCesarCryptageEtDecryptage(key = 8,mot = it1.username) }
            //Picture

            /*


    @ColumnInfo(name = "invoiceFId") val invoiceFId: Int,
    @ColumnInfo(name = "pictureName") val pictureName: String,
    @ColumnInfo(name = "picturePath") val picturePath: String,
    @ColumnInfo(name = "publicUrl", defaultValue = "") val PublicUrl: String,
    @ColumnInfo(name = "pictureOriginalName") var pictureOriginalName: Bitmap? = null,




    @ColumnInfo(name = "invoiceCode") val invoiceCode: String,
    @ColumnInfo(name = "invoiceDesc") val invoiceDesc: String,
    @ColumnInfo(name = "invoiceBarCode") val invoiceBarCode: String,
    @ColumnInfo(name = "userFId") val userFId: Int,
    @ColumnInfo(name = "directoryFId") val directoryFId: Int,
    @ColumnInfo(name = "branchFId") val branchFId: Int,
    @ColumnInfo(name = "invoiceDate") val invoiceDate: String,
    @ColumnInfo(name = "invoicekeyFId") val invoicekeyFId: Int,
    @ColumnInfo(name = "invoicePath") val invoicePath: String,
    @ColumnInfo(name = "androidVersion") val androidVersion: String,
    @ColumnInfo(name = "invoiceUniqueId") val invoiceUniqueId: String,
    @ColumnInfo(name = "clientName", defaultValue = "null") val clientName: String? = "" ,
    @ColumnInfo(name = "clientPhone", defaultValue = "null") val clientPhone: String? = "" ,
    @ColumnInfo(name = "expiredDate", defaultValue = "null") val expiredDate: String? = "" ,
             */
            Toast.makeText(this,"${Data.user}",Toast.LENGTH_LONG).show()
        }

    }
}