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
import com.example.archive_sfc.models.ApiResponseDirectory
import com.example.archive_sfc.models.ApiResponseInvoiceKey
import com.example.archive_sfc.models.room.Directory
import com.example.archive_sfc.models.room.InvoiceKey
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModel
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModelFactory
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModel
import com.example.archive_sfc.room.invoicekey.viewmodel.InvoiceKeyViewModelFactory
import com.example.archive_sfc.room.user.viewmodel.UserViewModel
import com.example.archive_sfc.room.user.viewmodel.UserViewModelFactory
import com.example.archive_sfc.utils.url_fusio
import com.example.archive_sfc.volley.Singleton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

@ExperimentalGetImage
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //private var adapter : AdaptaterMain? =null
    //var profil :ArrayList<Profil> = ArrayList()
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as UserApplication).repository)
    }
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((application as UserApplication).repositoryDirectory)
    }
    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((application as UserApplication).repositoryInvoiceKey)
    }
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
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


       // updateData(store)
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

}