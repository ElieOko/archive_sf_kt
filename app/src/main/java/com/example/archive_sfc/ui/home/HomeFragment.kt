package com.example.archive_sfc.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.UserApplication
import com.example.archive_sfc.adaptater.AdaptaterImageStore
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.databinding.FragmentHomeBinding
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModel
import com.example.archive_sfc.room.directory.viewModel.DirectoryViewModelFactory
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModel
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModelFactory
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModel
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModelFactory
import com.example.archive_sfc.ui.gallery.GalleryViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var adapterImageStore : AdaptaterImageStore? =null
    private var  recyclerView: RecyclerView? = null
    val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((activity?.application as UserApplication).repositoryPicture)
    }
    private val directoryViewModel: DirectoryViewModel by viewModels {
        DirectoryViewModelFactory((activity?.application  as UserApplication).repositoryDirectory)
    }
    private val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory((activity?.application  as UserApplication).repositoryInvoice)
    }

    val list :ArrayList<Invoice> = ArrayList()
    private val listId  = mutableListOf<Int>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

//        invoiceViewModel.allInvoice.observe(viewLifecycleOwner){
//            it.forEach {i->
//                if (!listId.contains(i.InvoiceId)){
//                    listId.add(i.InvoiceId)
//                    Log.e("More =>","$listId")
//                }
//            }
//            Log.e("taille ->","${listId.size}")
//        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return  binding.root
    }
/*
 */
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
//    directoryViewModel.allDirectory.observe(this){
//        Log.e("TAG => ","${it.size}")
//    }
//    invoiceViewModel.allInvoice.observe(this){
//        Log.e("TAG invoice => ","${it.size}")
//    }
    initRecy()
    manageEvent()
}

    private fun initRecy() {
// 123456789//MA
        recyclerView = binding.recyclerViewImage
        adapterImageStore = AdaptaterImageStore()
        recyclerView?.layoutManager = GridLayoutManager(requireContext(),2)
        recyclerView?.adapter = adapterImageStore
//        invoiceViewModel.getAllInvoice().observe(requireActivity()){
//            Log.e("LUI=>","${it.size}")
//        }
//        invoiceViewModel.allInvoice.observeForever{
//            Log.e("LOI=>","${it.size}")
//        }
        allPicture()
    }
    private fun allPicture() {
        val listInvoicePicture: ArrayList<List<Picture>> = ArrayList<List<Picture>>()
//        invoiceViewModel.allInvoice.observe(this){
//            it.forEach {i ->
//
//            }
//            list.addAll(it)
//        }
    invoiceViewModel.allInvoice.observe(this){
        var iteration = 0
        it.forEach {i->
            pictureViewModel.getAllImageByInvoice(i.InvoiceId).observe(this){listPicture ->
                Log.d("Iteration $iteration -->","$it")
                Log.d("Voir plus grand =>","$listPicture")
                if(!listInvoicePicture.contains(listPicture)){
                    Log.e("ICI =>","$listInvoicePicture")
                    Log.e("Iteration awa ->","Taille ${listInvoicePicture.size}")
                    listInvoicePicture.addAll(arrayListOf(listPicture))
                }
                iteration++
                if( it.size  == iteration){
                    //Data.allInvoicePicture.addAll(listInvoicePicture)
                    Log.e("VRAIMENT TOLEMBI -> ","${it.size}")
                    Log.e("VRAIMENT TOLEMBI YO -> ","${it}")
                    adapterImageStore?.addImageInContenaire(it,listInvoicePicture)
                    ///Log.e("Akoti na ->","Taille Oyo ${listInvoicePicture.size}")
                    //listInvoicePicture.clear()
                }
            }
        }
    }

//            it.forEach {i->
//

//            }
//        listId.forEach {
//            val x= pictureViewModel.getAllImageByInvoice(it)
//            Log.e("ckeck ","$x")
//        }

    }
    private fun call(list: ArrayList<Invoice>) {

        Toast.makeText(requireContext(),"${list.size} invoice",Toast.LENGTH_LONG).show()
        //Toast.makeText(requireContext(),"${listInvoicePicture.size} invoice-list-picture",Toast.LENGTH_LONG).show()
    }
    private fun manageEvent(){
        adapterImageStore?.setOnClickItem {
            Toast.makeText(requireContext(),it[0].pictureName,Toast.LENGTH_LONG).show()
        }
    }
    fun sizeAdd() : Int? {
       return adapterImageStore?.getSize()
    }
}


