package com.alyndroid.supervisorreceipt.ui.finalReciept

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.databinding.ActivityFinalReceiptBinding
import com.alyndroid.supervisorreceipt.helpers.SharedPref
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.addItems.AddItemsActivity
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.editItem.EditItemActivity
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import kotlinx.android.synthetic.main.activity_final_receipt.*


class FinalReceiptActivity : BaseActivity() {
    private val viewModel: FinalRecieptViewModel by lazy {
        ViewModelProviders.of(this).get(FinalRecieptViewModel::class.java)
    }

    private lateinit var itemList: MutableList<ItemData>
    private lateinit var familiesList: MutableList<String>
    private lateinit var adapter: FamiliesAdapter
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val print = menu.findItem(R.id.action_fav)
        val confirm = menu.findItem(R.id.confirm_invoice)
        val add = menu.findItem(R.id.action_add)
        val type = SharedPreference(this).getValueString("type")
        print.isVisible = type == "sm"
        confirm.isVisible = type == "sv"
        add.isVisible = type == "sv"
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
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("هل أنت متأكد من تأكيد الفاتورة؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, id ->
                viewModel.sendSalesmanInvoice(itemList
                ,SharedPreference(this).getValueString("salesman_no")!!
                ,intent.getStringExtra("customerNo")!!)
            }
        alertDialogBuilder.setNegativeButton(
            "لا"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }


    private fun confirmAction() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("هل أنت متأكد من تأكيد الفاتورة؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, id ->
                viewModel.sendSupervisorInvoice(itemList, SharedPreference(this).getValueString("salesman_no")!!)
            }
        alertDialogBuilder.setNegativeButton(
            "لا"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
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

        adapter = FamiliesAdapter(this, ItemsEditableAdapter.ItemClickListener {
            val intent = Intent(this, EditItemActivity::class.java)
            intent.putExtra("id", it.id.toString())
            intent.putExtra("count", it.quantity)
            startActivityForResult(intent, 1000)
        })



        viewModel.response.observe(this, Observer { it1 ->
            var it = it1
            if (SharedPreference(this).getValueString("type") == "sm") {
                it = it1.filter { d -> d.item_type == "old" }.toMutableList()
                it.addAll(intent.getSerializableExtra("newList") as Collection<ItemData>)
            }
            val list = it.toMutableList()
            itemList = list
            adapter.setMatches(it)
            familiesList = it.map { it.itemcategory }.distinct().toMutableList()
            adapter.submitList(familiesList)
            if (SharedPreference(this).getValueString("type") == "sm") {
                for (i in it) {
                    if (map[i.itemname] as Int == 0) {
                        i.quantity =
                            (i.quantity.toInt() * SharedPreference(this).getValueString(SharedPref.gard_number)!!.toDouble()).toInt()
                                .toString()
                    } else {
                        i.quantity = (i.quantity.toInt() - map[i.itemname] as Int).toString()
                    }
                }
            }
            it.forEach { i ->
                i.editedQuantity = i.quantity
                i.status = 0
            }
        })

        binding.familiesRecycler.adapter = adapter


        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBarFinal.visibility = View.VISIBLE
            } else {
                progressBarFinal.visibility = View.GONE
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
                val intent = Intent(this, FiltersActivity::class.java)
                startActivity(intent)
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
                    //do your work here
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
                    adapter.setMatches(itemList)
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
                            , reason = data.getStringExtra("reason")!!
                            , itemcategory = data.getStringExtra("Family")!!
                        )
                    )
                    adapter.setMatches(itemList)
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
