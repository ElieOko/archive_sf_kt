package com.example.archive_sfc.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.UserApplication
import com.example.archive_sfc.adaptater.AdaptaterImageStore
import com.example.archive_sfc.constante.Data
import com.example.archive_sfc.databinding.FragmentHomeBinding
import com.example.archive_sfc.models.room.Picture
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModel
import com.example.archive_sfc.room.invoice.viewmodel.InvoiceViewModelFactory
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModel
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModelFactory

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private var adapterImageStore : AdaptaterImageStore? =null
    private lateinit var  recyclerView: RecyclerView

    val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((activity?.application as UserApplication).repositoryPicture)
    }
    val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory((activity?.application  as UserApplication).repositoryInvoice)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val textView: TextView = binding!!.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        initRecy()
        return binding!!.root
    }
/*
 */
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    super.onViewCreated(view, savedInstanceState)
}
    private fun initRecy() {
// 123456789//MA
        Toast.makeText(requireContext(),"Elie Oko",Toast.LENGTH_LONG).show()
        adapterImageStore = AdaptaterImageStore()
        recyclerView = binding!!.recyclerViewImage
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        recyclerView.adapter = adapterImageStore
        val listInvoicePicture: ArrayList<List<Picture>> = ArrayList<List<Picture>>()
        invoiceViewModel.allInvoice.observe(this){

            var iteration = 0
            it.forEach {i->
                pictureViewModel.getAllImageByInvoice(i.InvoiceId).observe(this){listPicture ->
                    Log.d("Iteration -->","$it")
                    Log.d("Voir plus grand =>","$listPicture")
//                val item:ArrayList<ArrayList<List<Picture>>> = ArrayList()
                    //item.add(listPicture)
                    if(!listInvoicePicture.contains(listPicture)){
                        listInvoicePicture.addAll(arrayListOf(listPicture))
                        Log.d("Voir plus haut =>","$listInvoicePicture")
                        Log.e("Iteration awa ->","Taille ${listInvoicePicture.size}")
                    }
                    iteration++
                    if( it.size  == iteration){
                        Data.allInvoicePicture.addAll(listInvoicePicture)
                        adapterImageStore?.addImageInContenaire(listInvoicePicture)
                        Log.e("Akoti na ->","Taille Oyo ${listInvoicePicture.size}")
                    }
                }
            }
        }
    }

}


