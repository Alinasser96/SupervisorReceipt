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
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.ItemData
import kotlinx.android.synthetic.main.family_item_final.view.*


class FamiliesAdapter(val context: Context, val listener: ItemsEditableAdapter.ItemClickListener) :
    ListAdapter<String, FamiliesAdapter.MatchesViewHolder>(DiffCallback) {
    private lateinit var itemsList: List<ItemData>
    private lateinit var familiesList: MutableList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        return MatchesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.family_item_final,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        val result = getItem(position)
        holder.dateTextView.text = result
        if (SharedPreference(context).getValueString("type") == "sm") {
            val adapter = ItemsAdapter(context)
            adapter.submitList(itemsList.filter { d ->
                d.itemcategory == holder.dateTextView.text
            })
            adapter.notifyDataSetChanged()
            holder.recyclerView.adapter = adapter
        } else {
            val adapter = ItemsEditableAdapter(context, listener)
            adapter.submitList(itemsList.filter { d ->
                d.itemcategory == holder.dateTextView.text
            })
            holder.recyclerView.adapter = adapter
        }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    override fun submitList(list: MutableList<String>?) {
        super.submitList(list)
        familiesList = list!!
    }

    fun setMatches(itemsList: List<ItemData>) {
        this.itemsList = itemsList
        notifyDataSetChanged()
    }


    class MatchesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.family_name_textView
        val recyclerView: RecyclerView = view.final_item_recycler
    }

}