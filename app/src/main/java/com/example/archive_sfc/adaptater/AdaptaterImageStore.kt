package com.example.archive_sfc.adaptater

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.archive_sfc.R
import com.example.archive_sfc.models.InvoicePicture
import com.example.archive_sfc.models.room.Invoice
import com.example.archive_sfc.models.room.Picture

class AdaptaterImageStore : RecyclerView.Adapter<AdaptaterImageStore.ViewHolder>(){
    var sdtListInvoice =  mutableListOf<Invoice>()
    private var sdt : ArrayList<InvoicePicture> = ArrayList()
    var stdListImage :ArrayList<List<Picture>>  =  ArrayList()
    private var onClickItem :((Invoice)->Unit)? =null
    private var onClickDeleteItem :(()->Unit)? =null
    private var onLongClickItem :((Invoice)->Unit)? =null
    val listTemp :ArrayList<List<Picture>> = ArrayList()
    private var isEnabled = false
    fun deleteAll(){
        this.sdtListInvoice.clear()

        //notifyDataSetChanged()
    }
    fun delete(invoice: Invoice){
        val position = this.sdtListInvoice.indexOf(invoice)

        sdtListInvoice.removeAt(position)
        notifyItemRemoved(position)
        sdtListInvoice.remove(invoice)
        notifyItemRangeChanged(position, sdtListInvoice.size)
        notifyDataSetChanged()
    }
    fun onLongClickItem(callback:(Invoice) -> Unit){
        this.onLongClickItem = callback
    }
    fun  setOnClickItem(callback: (Invoice)->Unit){
        this.onClickItem = callback
    }
    fun  setOnClickDeleteitem(callback: ()->Unit){
        this.onClickDeleteItem = callback
    }

    fun addImageInContenaire(
        invoice: Invoice,
        listPicture:ArrayList<Picture>,
        invoices: MutableList<Invoice>
    ){
            if(!this.sdtListInvoice.contains(invoice)){
                this.sdtListInvoice.add(invoice)
            }
            if(!this.stdListImage.contains(listPicture)){
                this.stdListImage.add(listPicture)
            }
            notifyDataSetChanged()
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sdtListInvoice[position]
        val itemPicture: List<Picture> = stdListImage[position]
        holder.bindView(item,itemPicture,holder)
        holder.itemView.setOnClickListener{
            onClickItem?.invoke(item)
        }
        holder.itemView.setOnLongClickListener{
            onLongClickItem?.invoke(item)
            true
        }
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
       return sdtListInvoice.size
    }
    fun getSize():Int{
        return sdtListInvoice.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.imageView)
        private var contentSize = view.findViewById<TextView>(R.id.totalContent)
        private var codeInvoice = view.findViewById<TextView>(R.id.codeInvoice)
        private var card = view.findViewById<CardView>(R.id.card2)
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bindView(item: Invoice, pictures: List<Picture>, holder: ViewHolder) {
            try {
                        contentSize.text = "${pictures.size}file(s)"
                        image.setImageBitmap(pictures[0].contentFile)
                        codeInvoice.text = item.invoiceCode
                        var color: Int =R.color.white
                        if (item.isSelect){
                            color = R.color.select
                        }
                        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, color))
                        card.backgroundTintList = colorStateList
            }
            catch (e:Exception){
                Log.e("ERROR -ADPT---","${e.message}")
            }
        }
    }
    }
