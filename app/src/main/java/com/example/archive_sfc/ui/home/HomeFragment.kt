package com.example.archive_sfc.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.archive_sfc.CameraActivity
import com.example.archive_sfc.LoginActivity
import com.example.archive_sfc.R
import com.example.archive_sfc.UserApplication
import com.example.archive_sfc.adaptater.AdaptaterImageStore
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.constante.Route
import com.example.archive_sfc.constante.UserData
import com.example.archive_sfc.databinding.FragmentHomeBinding
import com.example.archive_sfc.databinding.NavHeaderMainBinding
import com.example.archive_sfc.models.ApiInvoice
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
import com.example.archive_sfc.utils.url_fusio
import com.example.archive_sfc.volley.Singleton
import com.example.archive_sfc.volley.VolleyFileUploadRequest
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalGetImage
@RequiresApi(Build.VERSION_CODES.M)
class HomeFragment : Fragment() {
    private  var binding: FragmentHomeBinding? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var adapterImageStore: AdaptaterImageStore? = null
    private var recyclerView: RecyclerView? = null
    private var actionMode: ActionMode? = null
    private val selectedItems = mutableListOf<Invoice>()
    private lateinit var mDialog: MaterialDialog.Builder
   // private val Context.dataStore by preferencesDataStore("settings")
    private var userParcour = mutableListOf<User>()
    private val tag = "HomeFragment"
    private val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((activity?.application as UserApplication).repositoryPicture)
    }
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((activity?.application as UserApplication).repositoryDirectory)
    }
    private val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory((activity?.application as UserApplication).repositoryInvoice)
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as UserApplication).repository)
    }
    private var userOn:Int = 0
    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((activity?.application as UserApplication).repositoryInvoiceKey)
    }
//    private val urlViewModel: UrlViewModel by viewModels {
//        UrlViewModelFactory((activity?.application as UserApplication).repositoryUrl)
//    }
    private val statusViewModel: StatusViewModel by viewModels {
        StatusViewModelFactory((activity?.application as UserApplication).repositoryStatus)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.e(tag,"onCreateView")
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The usage of an interface lets you inject your own implementation
        Log.e(tag,"onViewCreated")
        val drawerLayout: DrawerLayout = binding!!.drawerLayout
        val navigationView: NavigationView = binding!!.navView
        val headerView = navigationView.getHeaderView(0)
        val navViewHeaderBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        navViewHeaderBinding.name.text = UserData.name
        navViewHeaderBinding.status.text = UserData.status
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        binding?.navView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_logout -> {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.menu_serve_url -> {

                    true
                }
                else -> {
                    true
                }
            }
        }
        initRecy()
        //prefsData = ViewModelProvider(this).get(PrefsDataStoreScreenViewModel::class.java)
        manageEvent()
        binding?.toolbar?.inflateMenu(R.menu.main)
        binding?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_save_all -> {
                    if(!checkingConnexion()) run {
                        mDialogConnexion("Connexion failed","Connection error, make sure you are connected to the internet",R.raw.animation_failed_connexion)
                        return@run
                    }
                    else{
                        sendAllInvoice()
                    }
                }
            }
            true
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecy() {
// 123456789//MA
        recyclerView = binding?.recyclerViewImage
        adapterImageStore = AdaptaterImageStore()
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView?.adapter = adapterImageStore
        allPicture()
        userViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("com", it.toString())
                Toast.makeText(
                    requireContext(),
                    it.username,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        statusViewModel.allStatus.observe(this){
            if(it.isNotEmpty()){
                userOn = it[0].idUser
                userViewModel.allWords.observe(this){i->
                    i.filterTo(userParcour){user ->
                        user.UserId == userOn
                    }
                }
                Toast.makeText(context,"Notre utilisateur $userOn",Toast.LENGTH_LONG).show()
            }
        }
    }

    private inner class SelectionCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflating the menu resource
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = "${selectedItems.size} tailles"
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.delete -> {
                    // Perform action on selected items (e.g., delete)
                    // deleteSelectedItems()
                    GlobalScope.launch(Dispatchers.Main) {
                        var i = 0
                        selectedItems.forEach {
                            i++
                            Toast.makeText(context,"delete ${it.InvoiceId}",Toast.LENGTH_LONG).show()
                            pictureViewModel.deleteAllFID(it.InvoiceId)
                            invoiceViewModel.deleteById(it.InvoiceId)
//                            if(selectedItems.size - 1 == i){
//                                initRecy()
//                            }
                        }
                        initRecy()
                    }
                    mode.finish() // Finish the action mode
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
        }
    }

    private fun startActionMode() {
        actionMode = activity?.startActionMode(SelectionCallback())
    }

    private fun allPicture() {
        try {
            val user = User(0, UserData.name, UserData.password)
            setUser(user)
            invoiceViewModel.allInvoice.observe(viewLifecycleOwner) {
                var iteration = 0
                Toast.makeText(context,"Hello world",Toast.LENGTH_LONG).show()
                it.forEach {invoice ->
                    Toast.makeText(context,"$invoice",Toast.LENGTH_LONG).show()
                    iteration++
                    pictureViewModel.getAllImageByInvoice(invoice.InvoiceId).observe(viewLifecycleOwner){listInvoicePicture ->
                        Log.e("ADAPTATER","->[${listInvoicePicture.size}]=${listInvoicePicture}")
                        Log.e("INVOICE","->[--${invoice}]--")
                        adapterImageStore?.addImageInContenaire(invoice,
                            listInvoicePicture as ArrayList<Picture>,it
                        )
                    }
//                        Log.d("Iteration $iteration -->", "$it")
//                        if (!listInvoicePicture.contains(listPicture)) {
//                            Log.e("ICI =>", "$listInvoicePicture")
//                            listInvoicePicture.addAll(arrayListOf(listPicture))
//                            // sdt.add(stockInvoicePicture)
//                        }
                            //adapterImageStore?.addImageInContenaire(stockInvoicePicture)
                    }

            }
        }
        catch(e:Exception){
            Toast.makeText(context,"${e.message}",Toast.LENGTH_LONG).show()
        }
    }
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun manageEvent() {
        val bundle = Bundle()
        adapterImageStore?.setOnClickItem {
            if (actionMode != null) {
                toggleSelection(it)
            } else {
                bundle.putInt("invoiceId", it.InvoiceId)
                findNavController().navigate(R.id.action_nav_home_to_galleryFragment, bundle)
            }
        }
        binding?.fab?.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            activity?.finish()
        }
        binding?.Sync?.setOnClickListener {
            if(!checkingConnexion()) run {
                mDialogConnexion("Connexion failed","Connection error, make sure you are connected to the internet",R.raw.animation_failed_connexion)
                return@run
            }
            else{
                GlobalScope.launch(Dispatchers.Main) {
                     syncDirectory()
                     syncInvoiceKey()
                        mDialogConnexion("Storage update","Mises à jour éffectué avec succès",R.raw.animation_success)
//                    }
//                    else{
//                        mDialogConnexion("Retry update","Mises à jour non éffectué",R.raw.animation_refresh)
//                    }
                }
            }
        }
        adapterImageStore?.onLongClickItem {
            startActionMode()
            toggleSelection(it)
        }
    }
    private fun toggleSelection(invoice: Invoice) {
        if (selectedItems.contains(invoice)) {
            selectedItems.remove(invoice)
            invoice.isSelect = false
            invoiceViewModel.update(invoice)
        } else {
            selectedItems.add(invoice)
            invoice.isSelect = true
            invoiceViewModel.update(invoice)
        }
        if (selectedItems.isEmpty()) {
            actionMode?.finish()
        } else {
            selectedItems.forEach {
                it.isSelect = false
                invoiceViewModel.update(it)
            }
            actionMode?.invalidate()
        }
    }
    private fun sendAllInvoice() {
     val msg = postInvoicePictusre()
        val mDialog =  MaterialDialog.Builder(requireActivity())
        mDialog.setAnimation(R.raw.animation_refresh)
            .setTitle(msg)
            .setPositiveButton("Okey"){dialog,_->
                Log.w(tag,"--->${adapterImageStore?.sdtListInvoice}")
                Log.i(tag,"--->${adapterImageStore?.stdListImage}")
                try {
                    var i = 0
                    var comptage = 0
                    adapterImageStore?.sdtListInvoice?.forEach {
                        i+=1
                        comptage++
                            val data = JSONObject()
                            data.put("InvoiceCode", it.invoiceCode)
                            data.put("InvoiceDesc", it.invoiceDesc)
                            data.put("InvoiceBarCode", it.invoiceBarCode)
                            data.put("UserFId", it.UserFId)
                            data.put("DirectoryFId", it.DirectoryFId)
                            data.put("BranchFId", it.BranchFId)
                            data.put("InvoiceDate", it.invoiceDate)
                            data.put("InvoicePath", it.invoicePath)
                            data.put("AndroidVersion", it.androidVersion)
                            data.put("InvoiceUniqueId", it.invoiceUniqueId)
                            data.put("ClientName", it.clientName)
                            data.put("ClientPhone", it.clientPhone)
                            data.put("ExpiredDate", it.expiredDate)
                            data.put("InvoicekeyFId", it.InvoicekeyFId)
                        CoroutineScope(Dispatchers.IO).launch {
                            val jsonObjectRequest = JsonObjectRequest(
                                Request.Method.POST,
                                url_fusio(Route.url, Route.postInvoice_endpoint),
                                data,
                                {response ->
                                    val responseObject: ApiInvoice = Gson().fromJson(response.toString(),object : TypeToken<ApiInvoice>() {}.type)
                                    Log.e("NOW","Oups->> $responseObject")
                                    var cpt =0
                                    var tour = 0
                                    if(responseObject.invoiceId != 0){
                                        adapterImageStore?.stdListImage?.forEach { pict->
                                            Log.e("where ->","->> ${adapterImageStore?.stdListImage}")
                                            Log.e("wherein ->","->> [--$pict--]")
                                            pict.forEach {im->
                                                        if(it.InvoiceId == im.InvoiceFId){
                                                            cpt++
                                                            val byteImg = fromBitmap(im.contentFile!!)
                                                            val queue = Volley.newRequestQueue(context)
                                                            val timeStamp = (SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                                                                .format(System.currentTimeMillis())).toString()
                                                            val uploadRequest = object: VolleyFileUploadRequest(Method.POST,url_fusio(Route.url, "${responseObject.invoiceId}/picture"),
                                                                {response->
                                                                    Toast.makeText(context,"To save $response",Toast.LENGTH_LONG).show()
                                                                    pictureViewModel.deleteGetById(im.PictureId)
                                                                    if(cpt == pict.size ){
                                                                        GlobalScope.launch {
                                                                            invoiceViewModel.deleteById(im.InvoiceFId)
                                                                        }
                                                                        Toast.makeText(context,"To $response",Toast.LENGTH_LONG).show()
                                                                    }
                                                                    if(adapterImageStore?.sdtListInvoice?.size ==  comptage ){
//                                                                        CoroutineScope(Dispatchers.main).launch {
//                                                                            invoiceViewModel.deleteById(im.InvoiceFId)
//                                                                            adapterImageStore?.deleteAll()
//                                                                            invoiceViewModel.deleteAll()
//                                                                            pictureViewModel.deleteAll()
//                                                                            initRecy()
//                                                                            Toast.makeText(context,"Collection send",Toast.LENGTH_LONG).show()
//                                                                        }
                                                                    }
                                                                },
                                                                {error->
                                                                    Toast.makeText(context,"bug $error",Toast.LENGTH_LONG).show()
                                                                }
                                                            ){
                                                                override fun getByteData(): MutableMap<String, DataPart> {
                                                                    val params = HashMap<String, DataPart>()
                                                                    params["image"] = DataPart("PNG_${timeStamp}_.png", byteImg!! , "image/*")
                                                                    return params
                                                                }
                                                                override fun getHeaders(): Map<String, String> {
                                                                    val params :MutableMap<String,String> = HashMap()
                                                                    params["Accept"] = "application/json"
                                                                    params["authorization"] = "Bear ${userParcour[0].smstoken}"
                                                                    return params
                                                                }
                                                            }
                                                            queue.add(uploadRequest).retryPolicy = DefaultRetryPolicy(0,0,0f)
                                                        }

                                                    tour++
                                            }
                                        }
                                    }
                                },
                                { error ->
                                    Log.e(tag, "$error")
                                })
                            Singleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
                        }
                    }
                }
                catch (e:Exception){
                    Toast.makeText(context,"${e.message}",Toast.LENGTH_LONG).show()
                    Log.e(tag,"${e.message}")
                }
               dialog.dismiss()
            }
        mDialog.build().show()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun setUser(_user: User){
        GlobalScope.launch {
            val test = userViewModel.getUser(_user)
            if (test != null){
                Data.user = test
                Log.d("Storage===>>>",test.email.toString())
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun syncDirectory():Boolean{
        var status = false
        CoroutineScope(Dispatchers.IO).launch {
            val stringRequest = StringRequest(
                Request.Method.GET, url_fusio(Route.url, Route.getAllDirectory_endpoint),
                { response ->
                    Log.e("Viva","######### -> $response")
                    val responseObject: List<Directory> = Gson().fromJson(response,object : TypeToken<java.util.ArrayList<Directory>>() {}.type)
                    responseObject.forEach {
                        directoryViewModel.insert(it)
                    }
                    status = true
                    Toast.makeText(requireContext(),"Mises à jours détecté for directory",Toast.LENGTH_LONG).show()
                    Log.e("Arrivederci","######### -> $responseObject")
                },
                { error ->
                    Toast.makeText(requireContext(),"Connexion failed retry",Toast.LENGTH_LONG).show()
                    Log.e("Viva","######### -> $error")
                }
            )
            Singleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
        }
    return status
    }
    @SuppressLint("SuspiciousIndentation")
    private fun syncInvoiceKey():Boolean{
        var status = false
        CoroutineScope(Dispatchers.IO).launch {
            val stringRequest = StringRequest(
                Request.Method.GET, url_fusio(Route.url, Route.getAllInvoiceKey_endpoint),
                { response ->
                    Log.e("Viva","######### -> $response")
                    // val obj =  Gson().fromJson(response.toString(), object : TypeToken<List<Directory>>() {}.type)
                    val responseObject: List<InvoiceKey> = Gson().fromJson(response,object : TypeToken<java.util.ArrayList<InvoiceKey>>() {}.type)
                    responseObject.forEach {
                        invoiceKeyViewModel.insert(it)
                    }
                    status = true
                    Toast.makeText(requireContext(),"Mises à jours détecté for invoiceKey",Toast.LENGTH_LONG).show()
                    Log.e("Arrivederci","######### -> $responseObject")
                },
                { error ->
                    Toast.makeText(requireContext(),"Connexion failed retry",Toast.LENGTH_LONG).show()
                    Log.e("Viva","######### -> $error")
                }
            )
            Singleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
        }
        return status
    }
    private fun postInvoicePictusre():String{
        var msg = "Nothing data to collection invoice"

        if( adapterImageStore?.getSize()!= 0){
            msg = "Data in collection invoice ${adapterImageStore?.getSize()} items"
        }
        return msg
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(tag,"onCreate")
       // allPicture()
    }

    override fun onPause() {
        super.onPause()
        Log.e(tag,"onPause")
    }
    override fun onStop() {
        super.onStop()
        Log.e(tag,"onStop")
    }
    override fun onStart() {
        super.onStart()
//        initRecy()
        Log.e(tag,"onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(tag,"onResume")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.e(tag,"onDestroy")
        binding = null
    }
    private fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkingConnexion():Boolean {
        val manager = activity?.application?.getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        var isAvailable = false
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true
            }
        }
        return isAvailable
    }
    private fun mDialogConnexion(title:String ="",message: String = "",animation :Int = 0 ){
        mDialog =  MaterialDialog.Builder(requireActivity())
        if(animation != 0)
            mDialog.setAnimation(animation)
        mDialog.setTitle(title)
        mDialog.setMessage(message)
        mDialog.setPositiveButton("Ok"){ dialog, _ ->
            dialog.dismiss()
        }
        mDialog.build().show()
    }
}