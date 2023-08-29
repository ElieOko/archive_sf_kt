package com.example.archive_sfc.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.R
import com.example.archive_sfc.models.room.Picture

class AdaptaterMainGalerie : RecyclerView.Adapter<AdaptaterMainGalerie.ViewHolder>() {

    private var stdList :List<Picture> = ArrayList()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.imageSlide)
        fun bindView(item: Picture) {
            image.setImageBitmap(item.contentFile)
        }
    }
    fun addItem(items: List<Picture>){
        this.stdList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slide, parent,
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
}

//class AdaptaterPiece : RecyclerView.Adapter<AdaptaterPiece.ViewHolder>(){