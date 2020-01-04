package com.alyndroid.instabugpremierleague.ui.matchs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import kotlinx.android.synthetic.main.filter_item.view.*


class FiltersAdapter(val context: Context, val filterClickListner: FilterClickListner) :
    ListAdapter<String, FiltersAdapter.FiltersViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        return FiltersViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.filter_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {
        val result = getItem(position)
        holder.filterNameTextView.text = result
        holder.filterNameTextView.setOnClickListener { filterClickListner.onClick(result) }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    class FiltersViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val filterNameTextView: TextView = view.filterName_textView
    }

    class FilterClickListner(val clickListener: (filter: String) -> Unit) {
        fun onClick(filter: String) = clickListener(filter)
    }
}