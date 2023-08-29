package com.example.archive_sfc.ui.archive

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.CameraActivity
import com.example.archive_sfc.R
import com.example.archive_sfc.UserApplication
import com.example.archive_sfc.adaptater.AdaptaterImageContenaire
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.constante.ImageParcours
import com.example.archive_sfc.databinding.FragmentArchiveEditBinding
import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.models.room.InvoiceKey
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModel
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModelFactory
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModel
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModelFactory
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModel
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModelFactory
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModel
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModelFactory
import com.example.archive_sfc.utils.chiffrement_de_cesar.chiffreDeCesarCryptageEtDecryptage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import java.text.SimpleDateFormat
import java.util.*


class ArchiveEditFragment : Fragment() {
    private lateinit var mBinding: FragmentArchiveEditBinding
    private lateinit var  recyclerView: RecyclerView
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((activity?.application as UserApplication).repositoryDirectory)
    }
    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((activity?.application as UserApplication).repositoryInvoiceKey)
    }
    private val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory((activity?.application as UserApplication).repositoryInvoice)
    }
    private val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((activity?.application as UserApplication).repositoryPicture)
    }
    private var folderId :Int = 0
    private var keyId :Int = 0
    private var invoiceFId = 0
    private var itemsInvoiceKey = mutableListOf<String>()
    private var itemsFolder = mutableListOf<String>()
    private var itemSubFolder = mutableListOf<String>()

    private var adapterImageContenaire : AdaptaterImageContenaire? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArchiveEditBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = mBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,true)
        adapterImageContenaire = AdaptaterImageContenaire()
        recyclerView.adapter = adapterImageContenaire
        Toast.makeText(requireContext(),"Stock en encours ->${ImageParcours.stdList.size}", Toast.LENGTH_LONG).show()
        adapterImageContenaire?.addImageInContenaire(ImageParcours.stdList)
        val listPicture = pictureViewModel.getAllImages()
        listPicture.observe(this){listOfPicture->
            Toast.makeText(requireContext(),"${listOfPicture.size}",Toast.LENGTH_LONG).show()
        }
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
        verifyInvoiceKey()
        calendarWithTextInput()
        storageLocalData()
        launch()
    }
    private fun optionSelect(){
        val selectItemsFolder = ArrayAdapter(requireContext(),R.layout.itemfolder,itemsFolder)
        mBinding.folderIdSelect.setAdapter(selectItemsFolder)
    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun launch(){
        mBinding.takePicture.setOnClickListener{
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }
    }

    private fun scannerStart(){
//        mBinding!!.InvoiceBareCode.setOnClickListener {
//            CameraActivity.startScanner(requireContext()){ its ->
//                its.forEach{
//                    when(it.valueType){
//                        Barcode.TYPE_URL ->{
//                            mBinding!!.InvoiceBareCode.setText(it.url.toString())
//                        }
//                        Barcode.TYPE_CONTACT_INFO ->{
//                            mBinding!!.InvoiceBareCode.setText(it.contactInfo.toString())
//                        }
//                        else->{
//                            mBinding!!.InvoiceBareCode.setText(it.rawValue.toString())
//                        }
//                    }
//                }
//            }
//        }

    }

    @SuppressLint("SetTextI18n")
    private fun calendarWithTextInput() {
        val c = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar.getInstance()
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        mBinding.InvoiceDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                mBinding.InvoiceDate.setText("$dayOfMonth/${month + 1}/$year")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        mBinding.InvoiceExpireDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                mBinding.InvoiceExpireDate.setText("$dayOfMonth/${month + 1}/$year")
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
                val selectItems = ArrayAdapter(requireContext(),R.layout.itemsub,itemSubFolder)
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
            val selectKey = ArrayAdapter(requireContext(),R.layout.itemkey,itemsInvoiceKey)
            mBinding.selectKey.setAdapter(selectKey)
        }
    }
    private fun verifyInvoiceKey(){
        mBinding.selectKey.addTextChangedListener {ed->
            invoiceKeyViewModel.allInvoiceKey.observe(this){listKey->
                listKey.forEach {
                    if(it.Invoicekey == ed.toString()){
                        keyId = it.InvoicekeyId
                    }
                }
            }
        }
    }

    private fun dialog(title:String="",message:String=""){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                // Respond to positive button press
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun storageLocalData(){

        mBinding.saveData.setOnClickListener {
            Toast.makeText(requireContext(),"${Data.user!!.UserId}",Toast.LENGTH_LONG).show()
            val invoiceCode = mBinding.InvoiceCode.text.toString()
            val invoiceBarCode= mBinding.InvoiceBareCode.text.toString()
            val invoiceDesc = mBinding.InvoiceDesc.text.toString()
            val invoiceDate = mBinding.InvoiceDate.text.toString()
            val selectFolder = mBinding.folderIdSelect.toString()
            val selectKey = mBinding.selectKey.text.toString()
            val clientName = mBinding.clientName.text.toString()
            val clientPhone = mBinding.clientPhone.text.toString()
            val expireDate = mBinding.InvoiceExpireDate.text.toString()
            val versionAndroid = Build.VERSION.RELEASE
            val branchFId = Data.user!!.BranchFId as Int
            val userFId: Int = Data.user!!.UserId
            val dateMillisSecond = (SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis())).toString()
            var invoiceUniqueId = Data.user?.let { it1 -> chiffreDeCesarCryptageEtDecryptage(key = 8,mot = it1.username) }
            invoiceUniqueId += dateMillisSecond
            if(invoiceCode.isNotEmpty() && selectFolder.isNotEmpty() && selectKey.isNotEmpty()){
                //

                if(adapterImageContenaire?.getPicture()?.size!= 0){
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
                        invoicePath = "",
                        androidVersion = versionAndroid,
                        invoiceUniqueId = invoiceUniqueId!!,
                        clientName = clientName,
                        clientPhone = clientPhone,
                        expiredDate = expireDate
                    )
                    invoiceViewModel.insert(invoice)
                    savePicture(invoiceUniqueId)
                }
                else{
                    Toast.makeText(requireContext(),"Foreach save invoice, add picture",Toast.LENGTH_LONG).show()
                }
            }
            else{
                dialog("Warning","Some fields cannot be empty try again entering values in the input boxes *")
            }
            Toast.makeText(requireContext(),"${Data.user}",Toast.LENGTH_LONG).show()
        }
    }
    private fun savePicture(invoiceUniqueId: String) {
//        invoiceViewModel.getAllInvoice()?.forEach{invoice->
//
//                if (invoice.invoiceUniqueId == invoiceUniqueId){
//                    invoiceFId = invoice.InvoiceId
//                    //Toast.makeText(this,"AWA $invoiceFId",Toast.LENGTH_LONG).show()
//                    adapterImageContenaire?.getPicture()?.forEach {img->
//                        val bitmap = img.bitmap
//                        val picture = Picture(
//                            PictureId = 0,
//                            InvoiceFId = invoiceFId,
//                            pictureName = img.fileName,
//                            picturePath = img.fileContent!!.path,
//                            PublicUrl = "",
//                            contentFile = bitmap
//                        )
//                        pictureViewModel.insert(picture)
//                    }
//                    ImageParcours.stdList.clear()
//                   // findNavController().navigate(R.id.action_archiveEditFragment_to_nav_home)
////                    val intent = Intent(this@ArchiveActivity, MainActivity::class.java)
////                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
////                    startActivity(intent)
////                    finish()
//                }
//            }
        }


}