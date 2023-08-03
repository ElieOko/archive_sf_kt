package com.example.archive_sfc.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.R
import com.example.archive_sfc.models.Profil

class AdaptaterMain : RecyclerView.Adapter<AdaptaterMain.ViewHolder>() {

    private var stdList :ArrayList<Profil> = ArrayList()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nom = view.findViewById<TextView>(R.id.name)
        private var status = view.findViewById<TextView>(R.id.status)
        fun bindView(item: Profil) {
          nom.text = item.nameUser
          status.text = item.status
        }
    }
    fun addItem(items: ArrayList<Profil>){
        this.stdList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.nav_header_main, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
      return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = stdList[0]
        holder.bindView(item)
    }
}

//class AdaptaterPiece : RecyclerView.Adapter<AdaptaterPiece.ViewHolder>(){