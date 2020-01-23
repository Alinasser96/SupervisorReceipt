package com.alyndroid.supervisorreceipt.ui.finalReciept

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import kotlinx.android.synthetic.main.product_item.view.*


class ItemsEditableAdapter(
    val context: Context,
    private val areShown: Boolean,
    private val type: String,
    private val listener: ItemClickListener
) :
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

        if (areShown || type == "no_edit"){
            holder.editButton.isVisible = false
        }
        holder.productNameTextView.text = result.itemname
        holder.wehdaTextView.text = result.default_unit

        holder.productNameTextView.setOnClickListener {
            if (result.default_unit == result.small_unit) {
                result.default_unit = result.large_unit
                result.editedQuantity = (result.editedQuantity.toDouble() / result.unit_factor).toString()
            } else {
                result.default_unit = result.small_unit
                result.editedQuantity = (result.editedQuantity.toDouble() * result.unit_factor).toString()
            }
            notifyDataSetChanged()
        }
        if (result.editedQuantity.toDouble().toInt() >= 0) {
            holder.productCountTextView.text = result.editedQuantity
            if (result.status == 2) {
                holder.productCountTextView.setTextColor(context.getColor(R.color.cadmiumred))
            }
        } else {
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
        val wehdaTextView: TextView = view.unit_name_textView
    }

    class ItemClickListener(val clickListener: (item: ItemData) -> Unit) {
        fun onClick(item: ItemData) = clickListener(item)
    }

}