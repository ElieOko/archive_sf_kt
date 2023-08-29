package com.example.archive_sfc.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.archive_sfc.UserApplication
import com.example.archive_sfc.adaptater.AdaptaterMainGalerie
import com.example.archive_sfc.databinding.FragmentGalleryBinding
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModel
import com.example.archive_sfc.room.picture.viewmodel.PictureViewModelFactory

class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var adapaterSlide : AdaptaterMainGalerie
    private lateinit var vpSlide :ViewPager2
    private val pictureViewModel: PictureViewModel by viewModels {
        PictureViewModelFactory((activity?.application as UserApplication).repositoryPicture)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val invoiceId = arguments?.getInt("invoiceId", 0)
        adapaterSlide = AdaptaterMainGalerie()
        vpSlide = binding.viewPage
        vpSlide.adapter = adapaterSlide
        pictureViewModel.getAllImageByInvoice(invoiceId!!).observe(this){listPicture ->
        adapaterSlide.addItem(listPicture)
        }
        Toast.makeText(requireContext(),"$invoiceId arriverderci",Toast.LENGTH_LONG).show()
    }
}