package com.example.archive_sfc.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.R
import com.example.archive_sfc.models.FileState
import com.example.archive_sfc.models.Profil

class AdaptaterImageContenaire : RecyclerView.Adapter<AdaptaterImageContenaire.ViewHolder>() {
    private var stdList :ArrayList<FileState> = ArrayList()

    fun addImageInContenaire(items: ArrayList<FileState>){
        this.stdList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.content_image, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return stdList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = stdList[position]
        holder.bindView(item)
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.imageView)
       // private var counterText = view.findViewById<TextView>(R.id.counterItem)
        fun bindView(item: FileState) {
               image.setImageURI(item.uri)

          //  counterText.text = "Image ${item.size}"
        }
    }

}