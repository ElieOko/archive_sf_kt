package com.example.archive_sfc.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
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
import com.example.archive_sfc.models.InvoicePicture
import com.example.archive_sfc.models.room.*
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

@ExperimentalGetImage
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var adapterImageStore: AdaptaterImageStore? = null
    private var recyclerView: RecyclerView? = null
    private var actionMode: ActionMode? = null
    private val selectedItems = mutableListOf<Invoice>()
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

    private val invoiceKeyViewModel: InvoiceKeyViewModel by viewModels {
        InvoiceKeyViewModelFactory((activity?.application as UserApplication).repositoryInvoiceKey)
    }

    val list: ArrayList<Invoice> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navView
        val headerView = navigationView.getHeaderView(0)
        val navViewHeaderBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        navViewHeaderBinding.name.text = UserData.name
        navViewHeaderBinding.status.text = UserData.status
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        binding.navView.setNavigationItemSelectedListener {
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
        // The usage of an interface lets you inject your own implementation
        initRecy()
        manageEvent()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.inflateMenu(R.menu.main)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_save_all -> {
                    sendAllInvoice()
                }
            }
            true
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecy() {
// 123456789//MA
        recyclerView = binding.recyclerViewImage
        adapterImageStore = AdaptaterImageStore()
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView?.adapter = adapterImageStore
        allPicture()
        userViewModel.user.observe(this) {
            if (it != null) {
                Log.d("com", it.toString())
                Toast.makeText(
                    requireContext(),
                    it.username,
                    Toast.LENGTH_LONG
                ).show()
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
        val listInvoicePicture: ArrayList<List<Picture>> = ArrayList()
        val user = User(0, UserData.name, UserData.password)
        setUser(user)
        invoiceViewModel.allInvoice.observe(this) {
            var iteration = 0
            it.forEach { i ->
                pictureViewModel.getAllImageByInvoice(i.InvoiceId).observe(this) { listPicture ->
                    Log.d("Iteration $iteration -->", "$it")
                    if (!listInvoicePicture.contains(listPicture)) {
                        Log.e("ICI =>", "$listInvoicePicture")
                        listInvoicePicture.addAll(arrayListOf(listPicture))
                       // sdt.add(stockInvoicePicture)
                    }
                    iteration++
                    if (it.size == iteration) {
                        Log.e("CONTAINS-> ", it.toString())
                        adapterImageStore?.addImageInContenaire(it, listInvoicePicture)
                        //adapterImageStore?.addImageInContenaire(stockInvoicePicture)
                    }
                }
            }
        }

    }

    private fun manageEvent() {
        val bundle = Bundle()
        adapterImageStore?.setOnClickItem {
            if (actionMode != null) {
                toggleSelection(it)
            } else {
                // Handle regular item click
                bundle.putInt("invoiceId", it.InvoiceId)
                findNavController().navigate(R.id.action_nav_home_to_galleryFragment, bundle)
            }
        }
        binding.fab.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            activity?.finish()
        }
        binding.Sync.setOnClickListener {
            syncDirectory()
            syncInvoiceKey()
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
    private fun syncDirectory(){
        CoroutineScope(Dispatchers.IO).launch {
            val stringRequest = StringRequest(
                Request.Method.GET, url_fusio(Route.url, Route.getAllDirectory_endpoint),
                { response ->
                    Log.e("Viva","######### -> $response")
                    val responseObject: List<Directory> = Gson().fromJson(response,object : TypeToken<java.util.ArrayList<Directory>>() {}.type)
                    responseObject.forEach {
                        directoryViewModel.insert(it)
                    }
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

    }
    @SuppressLint("SuspiciousIndentation")
    private fun syncInvoiceKey(){
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
    }
    private fun postInvoicePictusre():String{
        var msg = "Nothing data to collection invoice"

        if( adapterImageStore?.getSize()!= 0){
            msg = "Data in collection invoice picture sum ${adapterImageStore?.getSize()} "
        }
        return msg
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
}