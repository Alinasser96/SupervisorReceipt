package com.alyndroid.supervisorreceipt.ui.gard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.helpers.buildWifiDialog
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalRecieptViewModel
import com.alyndroid.supervisorreceipt.ui.newItems.NewItemsActivity
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_gard.*

class GardActivity : BaseActivity() {

    private val viewModel: FinalRecieptViewModel by lazy {
        ViewModelProviders.of(this).get(FinalRecieptViewModel::class.java)
    }

    lateinit var itemsList: MutableList<ItemData>
    lateinit var newList: MutableList<ItemData>
    lateinit var adapter: GardItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gard)
        adapter = GardItemsAdapter(this)
        val customerName = intent.getStringExtra("customerName")
        title = "جرد للعميل: $customerName"

        empty_msg_tv.setOnClickListener {
            empty_msg_tv.isVisible = false
            viewModel.getAllItems(
                SharedPreference(this).getValueString("salesman_no")!!
                , intent.getStringExtra("customerNo")!!
            )
        }
        viewModel.response.observe(this, Observer {
            itemsList = it.items.toMutableList()
            val oldList = it.items.filter { d -> d.item_type == "old" }

            if (oldList.isEmpty()) {
                empty_gard_msg_tv.isVisible = true
                items_cardView.isVisible = false
            }

            newList = it.items.filter { d -> d.item_type == "new" }.toMutableList()
            adapter.submitList(oldList.map { d -> d.itemname }.toMutableList())
            adapter.setdata(oldList.map { d -> d.itemname }.toMutableList())
        })

        viewModel.empty.observe(this, Observer {
            items_cardView.isVisible = !it
            gardDone_button.isVisible = !it
            empty_msg_tv.isVisible = it
        })

        GardItems_recyclerView.adapter = adapter



        viewModel.error.observe(this, Observer {
            when (it) {
                1 -> buildWifiDialog(this)
            }
        })

        gardDone_button.setOnClickListener {
            val gardMap = HashMap<String, Any>()
            gardMap["invoice_id"] = itemsList[0].invoice_id
            gardMap["salesman_id"] = SharedPreference(this).getValueString("salesman_no")!!
            gardMap["customer_id"] = intent.getStringExtra("customerNo")!!
            gardMap["item_id"] =
                itemsList.filter { d -> d.item_type != "new" }.map { d -> d.itemno }
            gardMap["gard_quantity"] = adapter.list.map { d -> d.itemCount }
            confirmAction(gardMap)
        }

        viewModel.sendGardResponse.observe(this, Observer {
            if (it.status) {
                lateinit var intent2: Intent
                if (newList.isEmpty()) {
                    intent2 = Intent(this, FinalReceiptActivity::class.java)

                } else {
                    intent2 = Intent(this, NewItemsActivity::class.java)
                    intent2.putExtra("newList", newList as ArrayList)
                }
                val map = convertListToHashMap()
                intent2.putExtra("adapter", map)
                intent2.putExtra("customerName", intent.getStringExtra("customerName"))
                intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
                startActivity(intent2)
            }
        })
        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
                gardDone_button.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                gardDone_button.isEnabled = true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllItems(
            SharedPreference(this).getValueString("salesman_no")!!
            , intent.getStringExtra("customerNo")!!
        )
    }


    private fun confirmAction(gardMap: HashMap<String, Any>) {
        if (empty_gard_msg_tv.isVisible) {
            val intent2 = Intent(this, NewItemsActivity::class.java)
            intent2.putExtra("newList", newList as ArrayList)
            val map = convertListToHashMap()
            intent2.putExtra("adapter", map)
            intent2.putExtra("customerName", intent.getStringExtra("customerName"))
            intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
            startActivity(intent2)
        } else {
            val materialDialog = MaterialDialog.Builder(this)
                .setTitle("انتهى الجرد؟")
                .setMessage("هل أنت متأكد من تأكيد عملية الجرد؟")
                .setCancelable(false)
                .setPositiveButton(
                    "نعم"
                ) { dialog, _ ->
                    viewModel.sendGard(gardMap)
                    dialog.cancel()
                }
                .setNegativeButton(
                    "لا"
                ) { dialog, _ -> dialog.cancel() }
                .build()
            materialDialog.show()
        }
    }

    private fun convertListToHashMap(): HashMap<String, Any> {
        val hashmap = HashMap<String, Any>()
        for (item in adapter.list) {
            hashmap[item.itemName] = item.itemCount
        }
        return hashmap
    }
}
