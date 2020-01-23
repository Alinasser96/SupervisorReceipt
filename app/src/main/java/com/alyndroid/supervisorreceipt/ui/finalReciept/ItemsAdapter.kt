package com.alyndroid.supervisorreceipt.ui.finalReciept

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import kotlinx.android.synthetic.main.product_item_final.view.*
import kotlinx.android.synthetic.main.product_item_final.view.product_count_tv
import kotlinx.android.synthetic.main.product_item_final.view.product_name_textView


class ItemsAdapter(val context: Context) :
    ListAdapter<ItemData, ItemsAdapter.MatchesViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
            return MatchesViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.product_item_final,
                    parent,
                    false
                )
            )

    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        val result = getItem(position)
        holder.productNameTextView.text = result.itemname
        holder.wehdaTextView.text = result.default_unit
        holder.productNameTextView.setOnClickListener {
            if (result.default_unit == result.small_unit) {
                result.default_unit = result.large_unit
                result.quantity = (result.quantity.toDouble() / result.unit_factor!!).toString()
            } else {
                result.default_unit = result.small_unit
                result.quantity = (result.quantity.toDouble() * result.unit_factor!!).toString()
            }
            notifyDataSetChanged()
        }
        if (result.quantity.toDouble().toInt()>=0) {
            holder.productCountTextView.text = result.quantity
        }else{
            holder.productCountTextView.text = "0"
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
        val wehdaTextView: TextView = view.wehada_final_name_textView
    }


}