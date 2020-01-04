package com.alyndroid.supervisorreceipt.ui.finalReciept

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import kotlinx.android.synthetic.main.product_item.view.*


class ItemsEditableAdapter(val context: Context, val listener: ItemClickListener) :
    ListAdapter<ItemData, ItemsEditableAdapter.MatchesViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
            return MatchesViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.product_item,
                    parent,
                    false
                )
            )

    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        val result = getItem(position)
        holder.productNameTextView.text = result.itemname
        if (result.editedQuantity.toDouble().toInt()>=0) {
            holder.productCountTextView.text = result.editedQuantity
            if (result.editedQuantity!= result.quantity){
                holder.productCountTextView.setTextColor(context.getColor(R.color.cadmiumred))
            }
        }else{
            holder.productCountTextView.text = "0"
        }

        holder.editButton.setOnClickListener {
            listener.onClick(result)
        }



    }


    companion object DiffCallback : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class MatchesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val productNameTextView: TextView = view.product_name_textView
        val productCountTextView: TextView = view.product_count_tv
        val editButton: ImageView = view.edit
    }

    class ItemClickListener(val clickListener: (item: ItemData) -> Unit) {
        fun onClick(item: ItemData) = clickListener(item)
    }

}