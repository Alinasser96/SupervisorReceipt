package com.alyndroid.supervisorreceipt.ui.gard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import kotlinx.android.synthetic.main.gard_item.view.*
import android.text.Editable
import android.text.TextWatcher
import com.alyndroid.supervisorreceipt.pojo.GardModel


class GardItemsAdapter(val context: Context) :
    ListAdapter<String, GardItemsAdapter.MatchesViewHolder>(DiffCallback) {
    public var list:MutableList<GardModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        return MatchesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.gard_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val result = getItem(position)
        holder.itemNameTextView.text = result
        holder.productNoTextView.setText(list[position].itemCount.toString())
        holder.productNoTextView.setTextColor(context.getColor(R.color.red_orange))
        holder.productNoTextView.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (holder.productNoTextView.text.toString()!="")
                list[position].itemCount = holder.productNoTextView.text.toString().toInt()
            }

        })



    }


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    fun setdata(list: MutableList<String>?){
        this.list = list!!.map { d->GardModel(d, 0) }.toMutableList()
        notifyDataSetChanged()
    }

    class MatchesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        lateinit var itemNameTextView: TextView
        lateinit var productNoTextView: EditText
        init {
            itemNameTextView = view.product_name
            productNoTextView = view.gardItemNo_picker

        }

    }

}