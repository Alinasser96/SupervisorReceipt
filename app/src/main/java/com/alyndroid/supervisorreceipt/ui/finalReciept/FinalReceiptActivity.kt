package com.alyndroid.supervisorreceipt.ui.finalReciept

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.databinding.ActivityFinalReceiptBinding
import com.alyndroid.supervisorreceipt.helpers.SharedPref
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.helpers.buildWifiDialog
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.addItems.AddItemsActivity
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.editItem.EditItemActivity
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import com.alyndroid.supervisorreceipt.ui.print.BluetoothDevicesListActivity
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_final_receipt.*
import kotlin.math.roundToInt


class FinalReceiptActivity : BaseActivity() {
    private val viewModel: FinalRecieptViewModel by lazy {
        ViewModelProviders.of(this).get(FinalRecieptViewModel::class.java)
    }

    private lateinit var itemList: MutableList<ItemData>
    private lateinit var familiesList: MutableList<String>
    private lateinit var adapter: FamiliesAdapter
    private lateinit var add: MenuItem
    private lateinit var type: String
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val print = menu.findItem(R.id.action_fav)
        val confirm = menu.findItem(R.id.confirm_invoice)
        add = menu.findItem(R.id.action_add)
        type = SharedPreference(this).getValueString("type")!!
        print.isVisible = type == "sm"
        confirm.isVisible = type == "sv" && !intent.getBooleanExtra("areShown", false)
        add.isVisible = type == "sv" && !intent.getBooleanExtra("areShown", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_fav -> {
            printAction()
            true
        }
        R.id.confirm_invoice -> {
            confirmAction()
            true
        }
        R.id.action_add -> {
            addAction()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun printAction() {
        val alertDialogBuilder = MaterialDialog.Builder(this)
        alertDialogBuilder.setMessage("هل أنت متأكد من تأكيد الفاتورة؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, _ ->
                viewModel.sendSalesmanInvoice(
                    itemList
                    , SharedPreference(this).getValueString("salesman_no")!!
                    , intent.getStringExtra("customerNo")!!
                )
                dialog.cancel()
            }
        alertDialogBuilder.setNegativeButton(
            "لا"
        ) { dialog, _ -> dialog.cancel() }
        val alert = alertDialogBuilder.build()
        alert.show()
    }


    private fun confirmAction() {
        val alertDialogBuilder = MaterialDialog.Builder(this)
        alertDialogBuilder.setMessage("هل أنت متأكد من تأكيد الفاتورة؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, _ ->
                viewModel.sendSupervisorInvoice(
                    itemList,
                    SharedPreference(this).getValueString("salesman_no")!!
                )
                dialog.cancel()
            }
        alertDialogBuilder.setNegativeButton(
            "لا"
        ) { dialog, _ -> dialog.cancel() }
        val alert = alertDialogBuilder.build()
        alert.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "الفاتورة المقترحة"
        when (SharedPreference(this).getValueString("type")) {
            "sv" -> {
                viewModel.getAllItems(intent.getStringExtra("customerNo")!!)
            }
            "sm" -> {
                viewModel.getAllItems(
                    SharedPreference(this).getValueString("salesman_no")!!
                    , intent.getStringExtra("customerNo")!!
                )
            }
        }
        val binding: ActivityFinalReceiptBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_final_receipt)
        binding.viewmodel = viewModel
        var map = HashMap<Any, Any>()
        if (SharedPreference(this).getValueString("type") == "sm") {
            map = intent.getSerializableExtra("adapter") as HashMap<Any, Any>
        }

        adapter = FamiliesAdapter(this, intent.getBooleanExtra("areShown", false),
            ItemsEditableAdapter.ItemClickListener {
                val intent = Intent(this, EditItemActivity::class.java)
                intent.putExtra("item", it)
                startActivityForResult(intent, 1000)
            })


        viewModel.empty.observe(this, Observer {
            empty_msg_tv.isVisible = it
        })

        viewModel.response.observe(this, Observer { it1 ->
            val new = it1.new
            var it = it1.items.toMutableList()
            if (new != null && new.isNotEmpty()){
                new.forEach {
                    it.isEditedItem = true
                }
                it.addAll(new)
            }



            add.isVisible = it1.type!="no_edit" && type == "sv" && !intent.getBooleanExtra("areShown", false)
            if (SharedPreference(this).getValueString("type") == "sm") {
                it = it1.items.filter { d -> d.item_type == "old" }.toMutableList()
                it.addAll(intent.getSerializableExtra("newList") as Collection<ItemData>)
            }

            val list = it.toMutableList()
            itemList = list

            familiesList = it.map { it.itemcategory }.distinct().toMutableList()
            adapter.apply {
                setItems(it)
                submitList(familiesList)
                setType(it1.type)
            }
            if (SharedPreference(this).getValueString("type") == "sm") {
                for (i in it.filter { d -> d.item_type == "old" }) {
                    if (map[i.itemname] as Int == 0) {
                        i.quantity = (i.quantity.toDouble() * SharedPreference(this).getValueString(
                            SharedPref.gard_number
                        )!!.toDouble()).roundToInt()
                                .toString()
                    } else {
                        i.quantity = (i.quantity.toDouble() - map[i.itemname] as Int).toString()
                    }
                }
            }
            it.forEach { i ->
                i.editedQuantity = i.quantity
                i.default_unit = i.small_unit
                i.status = 0
            }
        })

        binding.familiesRecycler.adapter = adapter


        viewModel.fromSV.observe(this, Observer {
            not_confirmed_invoice_textView.isVisible = !it
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBarFinal.visibility = View.VISIBLE
            } else {
                progressBarFinal.visibility = View.GONE
            }
        })

        viewModel.error.observe(this, Observer {
            when (it) {
                1 -> buildWifiDialog(this)
            }
        })

        viewModel.sendInvoiceResponse.observe(this, Observer {
            if (it.status) {
                Toast.makeText(this, "تم التأكيد بنجاح", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FiltersActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.sendSalesmanInvoiceResponse.observe(this, Observer {
            if (it.status) {
                Toast.makeText(this, "تم التأكيد بنجاح", Toast.LENGTH_SHORT).show()
                val intent2 = Intent(this, BluetoothDevicesListActivity::class.java)
                intent2.putExtra("itemList", ArrayList<ItemData>(itemList))
                intent2.putExtra("customerName", intent.getStringExtra("customerName"))
                startActivity(intent2)
                finish()
            } else {
                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 -> when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(this, data?.getStringExtra("id"), Toast.LENGTH_SHORT).show()
                    itemList.find { it.id == data?.getStringExtra("id")!!.toInt() }?.apply {
                        editedQuantity = data?.getStringExtra("edited")!!
                        status = if (editedQuantity.toInt() > 0) {
                            2
                        } else {
                            3
                        }
                        reason = data.getStringExtra("reason")!!
                    }
                    adapter.setItems(itemList)
                    adapter.notifyDataSetChanged()

                }
            }

            2000 -> when (resultCode) {
                RESULT_OK -> {
                    if (!familiesList.contains(data?.getStringExtra("Family")!!))
                        familiesList.add(data.getStringExtra("Family")!!)
                    adapter.submitList(familiesList)
                    adapter.notifyDataSetChanged()
                    itemList.add(
                        ItemData(
                            itemname = data.getStringExtra("itemName")!!
                            , itemno = data.getStringExtra("itemNo")!!
                            , editedQuantity = data.getStringExtra("count")!!
                            , status = 1
                            , unit_factor = data.getStringExtra("factor")!!.toInt()
                            , default_unit = data.getStringExtra("unit")!!
                            , reason = data.getStringExtra("reason")!!
                            , itemcategory = data.getStringExtra("Family")!!
                        )
                    )
                    adapter.setItems(itemList)
                    adapter.notifyDataSetChanged()

                }
            }
        }
    }

    private fun addAction() {
        val intent = Intent(this, AddItemsActivity::class.java)
        intent.putExtra("items", itemList as ArrayList<ItemData>)
        startActivityForResult(intent, 2000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
