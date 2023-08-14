package com.example.archive_sfc.adaptater

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.R
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.models.room.Picture

class AdaptaterImageStore : RecyclerView.Adapter<AdaptaterImageStore.ViewHolder>(){
    private var sdtListInvoice : List<Invoice> = ArrayList()
    private var stdListImage :ArrayList<List<Picture>> = ArrayList()
    private var onClickItem :((List<Picture>)->Unit)? =null
    private var onClickDeleteItem :((Picture)->Unit)? =null
    private var onLongClickItem :((List<Picture>)->Unit)? =null

    fun onLongClickItem(callback:(List<Picture>) -> Unit){
        this.onLongClickItem = callback
    }
    fun  setOnClickItem(callback: (List<Picture>)->Unit){
        this.onClickItem = callback
    }
    fun  setOnClickDeleteitem(callback: (Picture)->Unit){
        this.onClickDeleteItem = callback
    }
    fun addImageInContenaire(listInvoice:List<Invoice>,items: ArrayList<List<Picture>>){

        this.stdListImage = items
        this.sdtListInvoice = listInvoice
        notifyDataSetChanged()
    }

    fun getSize():Int{
        return sdtListInvoice.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(sdtListInvoice,stdListImage[position])
        holder.itemView.setOnClickListener{ onClickItem?.invoke(stdListImage[position])}
//        holder.itemView.setOnLongClickListener{
//            onLongClickItem?.invoke(stdListImage[position])
//        }

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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.imageView)
        private var contentSize = view.findViewById<TextView>(R.id.totalContent)
        private var codeInvoice = view.findViewById<TextView>(R.id.codeInvoice)
        @SuppressLint("SetTextI18n")
        fun bindView(item: List<Invoice>, pictures: List<Picture>) {
            contentSize.text = "${pictures.size}file(s)"
            image.setImageBitmap(pictures[0].contentFile)
            item.forEach {
                if(it.InvoiceId == pictures[0].InvoiceFId){
                    codeInvoice.text = it.invoiceCode
                }
            }
        }
        }
    }
