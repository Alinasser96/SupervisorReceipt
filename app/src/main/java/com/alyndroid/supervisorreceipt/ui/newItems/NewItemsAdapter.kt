package com.alyndroid.supervisorreceipt.ui.newItems

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.GardModel
import com.alyndroid.supervisorreceipt.pojo.ItemData
import kotlinx.android.synthetic.main.new_item_item.view.*


class NewItemsAdapter(val context: Context, val listner: ItemClickListener) :
    ListAdapter<ItemData, NewItemsAdapter.MatchesViewHolder>(DiffCallback) {
    private var isSelectedAll: Boolean = false
    public var list:MutableList<GardModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        return MatchesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.new_item_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        val result = getItem(position)
        holder.itemNameTextView.text = result.itemname
        holder.itemNameTextView.isChecked = isSelectedAll
        holder.itemNameTextView.setOnCheckedChangeListener { compoundButton, b ->
            listner.onChecked(result)
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem == newItem
        }

    }

    fun selectAll() {
        isSelectedAll = true
        notifyDataSetChanged()
    }

    class MatchesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var itemNameTextView: CheckBox = view.itemName_checkBox
    }

    class ItemClickListener(val clickListener: (item: ItemData) -> Unit) {
        fun onChecked(item: ItemData) = clickListener(item)
    }
}