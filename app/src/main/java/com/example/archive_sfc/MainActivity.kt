package com.example.archive_sfc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.constante.Route
import com.example.archive_sfc.constante.UserData
import com.example.archive_sfc.databinding.ActivityMainBinding
import com.example.archive_sfc.databinding.NavHeaderMainBinding
import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.models.room.InvoiceKey
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModel
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModelFactory
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModel
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModelFactory
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModel
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModelFactory
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModel
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModelFactory
import com.example.archive_sfc.room.user.viewmodel.UserViewModel
import com.example.archive_sfc.room.user.viewmodel.UserViewModelFactory
import com.example.archive_sfc.utils.url_fusio
import com.example.archive_sfc.volley.Singleton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


@ExperimentalGetImage
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as UserApplication).repository)
    }
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((application as UserApplication).repositoryDirectory)
    }
    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((application as UserApplication).repositoryInvoiceKey)
    }
    private val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((application as UserApplication).repositoryPicture)
    }
    private val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory((application  as UserApplication).repositoryInvoice)
    }
    private val listInvoicePicture: ArrayList<List<Picture>> = ArrayList<List<Picture>>()
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel.user.observe(this){
            if(it != null){
                Log.d("com",it.toString())
                Toast.makeText(
                    applicationContext,
                    it.username,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val user = User(0,UserData.name,UserData.password)
        setUser(user)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navigationView  : NavigationView = binding.navView
        val headerView = navigationView.getHeaderView(0)

        Toast.makeText(this,getVersion(),Toast.LENGTH_LONG).show()
        binding.appBarMain.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        binding.appBarMain.Sync.setOnClickListener {
            syncDirectory()
            syncInvoiceKey()
        }
        val navViewHeaderBinding : NavHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        navViewHeaderBinding.name.text = UserData.name
        navViewHeaderBinding.status.text = UserData.status

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        getAllInvoicePicture()
        binding.appBarMain.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_save_all -> {
                    sendAllInvoice()
                    true
                }
               else ->{
                   true
               }
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val mDialog =  MaterialDialog.Builder(this)
        mDialog.setAnimation(R.raw.animation_success)
            .setTitle("Congratulation")
            .setPositiveButton("Okey"){dialog,_->
                dialog.dismiss()
            }
        mDialog.build().show()
    }

    private fun sendAllInvoice() {
       val msg =  postInvoicePictusre()
        val mDialog =  MaterialDialog.Builder(this)
        mDialog.setAnimation(R.raw.animation_refresh)
            .setTitle("Collection save")
            .setMessage(msg)
            .setPositiveButton("Okey"){dialog,_->
                dialog.dismiss()
            }
        mDialog.build().show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setUser(_user: User){
        GlobalScope.launch {
            val test = userViewModel.getUser(_user)
            if (test != null){
                Data.user = test
                Log.d("Storage===>>>",test?.email.toString())
            }
        }
    }

    private fun getVersion():String{
       return Build.VERSION.RELEASE
    }
    @SuppressLint("SuspiciousIndentation")
    private fun syncDirectory(){
        CoroutineScope(IO).launch {
            val stringRequest = StringRequest(
                Request.Method.GET, url_fusio(Route.url, Route.getAllDirectory_endpoint),
                { response ->
                    Log.e("Viva","######### -> $response")
                    val responseObject: List<Directory> = Gson().fromJson(response,object : TypeToken<ArrayList<Directory>>() {}.type)
                    responseObject.forEach {
                        directoryViewModel.insert(it)
                    }
                    Toast.makeText(applicationContext,"Mises à jours détecté for directory",Toast.LENGTH_LONG).show()
                    Log.e("Arrivederci","######### -> $responseObject")
                },
                { error ->
                    Toast.makeText(applicationContext,"Connexion failed retry",Toast.LENGTH_LONG).show()
                    Log.e("Viva","######### -> $error")
                }
            )
            Singleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
        }

    }
    @SuppressLint("SuspiciousIndentation")
    private fun syncInvoiceKey(){
        CoroutineScope(IO).launch {
            val stringRequest = StringRequest(
                Request.Method.GET, url_fusio(Route.url, Route.getAllInvoiceKey_endpoint),
                { response ->
                    Log.e("Viva","######### -> $response")
                   // val obj =  Gson().fromJson(response.toString(), object : TypeToken<List<Directory>>() {}.type)
                    val responseObject: List<InvoiceKey> = Gson().fromJson(response,object : TypeToken<ArrayList<InvoiceKey>>() {}.type)
                    responseObject.forEach {
                        invoiceKeyViewModel.insert(it)
                    }
                    Toast.makeText(applicationContext,"Mises à jours détecté for invoiceKey",Toast.LENGTH_LONG).show()
                    Log.e("Arrivederci","######### -> $responseObject")
                },
                { error ->
                    Toast.makeText(applicationContext,"Connexion failed retry",Toast.LENGTH_LONG).show()
                    Log.e("Viva","######### -> $error")
                }
            )
            Singleton.getInstance(applicationContext).addToRequestQueue(stringRequest)
        }
    }
    private fun getAllInvoicePicture(){
        invoiceViewModel.allInvoice.observe(this){
            var iteration = 0
            it.forEach {i->
                pictureViewModel.getAllImageByInvoice(i.InvoiceId).observe(this){listPicture ->
                    Log.d("Iteration -->","$it")
                    Log.d("Voir plus grand =>","$listPicture")
                    if(!listInvoicePicture.contains(listPicture)){
                        listInvoicePicture.addAll(arrayListOf(listPicture))
                        Log.d("Voir plus haut =>","$listInvoicePicture")
                        Log.e("Iteration awa ->","Taille ${listInvoicePicture.size}")
                    }
                    iteration++
                    if( it.size  == iteration){
                        Data.allInvoicePicture.addAll(listInvoicePicture)
                        Log.e("Akoti na ->","Taille Oyo ${listInvoicePicture.size}")
                    }
                }
            }
        }
    }
    private fun postInvoicePictusre():String{
        var msg = "Nothing data to collection invoice"
        if(listInvoicePicture.isNotEmpty()){
            msg = "Data in collection invoice picture sum ${listInvoicePicture.size} "
        }
        return msg
    }
}