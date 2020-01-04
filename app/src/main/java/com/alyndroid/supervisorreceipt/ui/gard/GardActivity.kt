package com.alyndroid.supervisorreceipt.ui.gard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalRecieptViewModel
import com.alyndroid.supervisorreceipt.ui.newItems.NewItemsActivity
import kotlinx.android.synthetic.main.activity_gard.*

class GardActivity : BaseActivity() {

    private val viewModel: FinalRecieptViewModel by lazy {
        ViewModelProviders.of(this).get(FinalRecieptViewModel::class.java)
    }

    lateinit var itemsList: MutableList<ItemData>
    lateinit var newList: MutableList<ItemData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gard)
        val adapter = GardItemsAdapter(this)
        val customerName = intent.getStringExtra("customerName")
        title = "جرد للعميل: $customerName"

        viewModel.getAllItems(
            SharedPreference(this).getValueString("salesman_no")!!
            , intent.getStringExtra("customerNo")!!
        )
        empty_msg_tv.setOnClickListener {
            empty_msg_tv.isVisible = false
            viewModel.getAllItems(
                SharedPreference(this).getValueString("salesman_no")!!
                , intent.getStringExtra("customerNo")!!
            )
        }
        viewModel.response.observe(this, Observer {
            itemsList = it.toMutableList()
            val oldList = it.filter { d->d.item_type=="old" }
            newList = it.filter { d->d.item_type=="new" }.toMutableList()
            adapter.submitList(oldList.map { d -> d.itemname }.toMutableList())
            adapter.setdata(oldList.map { d -> d.itemname }.toMutableList())
        })

        viewModel.empty.observe(this, Observer {

            items_cardView.isVisible = !it
            gardDone_button.isVisible = !it
            empty_msg_tv.isVisible = it

        })

        GardItems_recyclerView.adapter = adapter

        fun convertListToHashmap(): HashMap<String, Any> {
            val hashmap = HashMap<String, Any>()
            for (item in adapter.list) {
                hashmap[item.itemName] = item.itemCount
            }
            return hashmap
        }

        gardDone_button.setOnClickListener {
            val gardMap = HashMap<String, Any>()
            gardMap["invoice_id"] = itemsList[0].invoice_id
            gardMap["salesman_id"] = SharedPreference(this).getValueString("salesman_no")!!
            gardMap["customer_id"] = "Egy_08274"
            gardMap["item_id"] = itemsList.map { d->d.itemno }
            gardMap["gard_quantity"] = adapter.list.map { d->d.itemCount }
            viewModel.sendGard(gardMap)
            val intent2 = Intent(this, NewItemsActivity::class.java)
            val map = convertListToHashmap()
            intent2.putExtra("adapter", map)
            intent2.putExtra("newList", newList as ArrayList)
            intent2.putExtra("customerName", intent.getStringExtra("customerName"))
            intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
            startActivity(intent2)
        }

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })
    }

}
