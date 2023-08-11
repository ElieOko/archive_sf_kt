package com.example.archive_sfc.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.R
import com.example.archive_sfc.models.room.Picture

class AdaptaterImageStore : RecyclerView.Adapter<AdaptaterImageStore.ViewHolder>(){
    private var stdListImage :ArrayList<List<Picture>> = ArrayList()

    fun addImageInContenaire(items: ArrayList<List<Picture>>){
        this.stdListImage = items
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(stdListImage[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.image_contenaire, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return stdListImage.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        private var image = view.findViewById<ImageView>(R.id.imageView)
        private var contentSize  = view.findViewById<TextView>(R.id.totalContent)
        private var codeInvoice  = view.findViewById<TextView>(R.id.codeInvoice)
        fun bindView(item: List<Picture>) {
            if(item.isNotEmpty()){
                image.setImageBitmap(item[0].contentFile)
                contentSize.text = "${item.size}file(s)"
                codeInvoice.text = "Invoice {${item[0].InvoiceFId}}"
            }
        }
    }
}