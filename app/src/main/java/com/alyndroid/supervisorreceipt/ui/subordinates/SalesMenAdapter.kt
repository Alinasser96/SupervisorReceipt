package com.alyndroid.supervisorreceipt.ui.subordinates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.SalesManData
import kotlinx.android.synthetic.main.family_item_final.view.*


class SalesMenAdapter(val context: Context, val listener: SalesManClickListener) :
    ListAdapter<SalesManData, SalesMenAdapter.MatchesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        return MatchesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.salesman_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        val result = getItem(position)
        holder.salesManTextView.text = result.salesmannamea
        holder.salesManTextView.setOnClickListener{listener.onClick(result)}
    }


    companion object DiffCallback : DiffUtil.ItemCallback<SalesManData>() {
        override fun areItemsTheSame(oldItem: SalesManData, newItem: SalesManData): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: SalesManData, newItem: SalesManData): Boolean {
            return oldItem == newItem
        }

    }



    class MatchesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val salesManTextView: TextView = view.findViewById(R.id.salesMan_textView)
    }

    class SalesManClickListener(val clickListener: (salesman: SalesManData) -> Unit) {
        fun onClick(salesman: SalesManData) = clickListener(salesman)
    }
}