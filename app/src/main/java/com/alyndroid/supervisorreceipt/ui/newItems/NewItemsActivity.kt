package com.alyndroid.supervisorreceipt.ui.newItems

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPref
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import kotlinx.android.synthetic.main.activity_new_items.*

class NewItemsActivity : BaseActivity() {

    lateinit var needed: String
    var list: MutableList<ItemData> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_items)
        needed = SharedPreference(this).getValueString(SharedPref.min_needed_items)!!
        title = "قائمة الأصناف الجديدة"
        settText("0")
        val adapter = NewItemsAdapter(this, NewItemsAdapter.ItemClickListener {
            if (list.contains(it)) {
                list.remove(it)
            } else {
                list.add(it)
            }
            settText(list.size.toString())
        })
        val newItems = intent.getSerializableExtra("newList") as MutableList<ItemData>
        adapter.submitList(newItems)
        newItems_recycler.adapter = adapter


        if (newItems.size <=  needed.toInt()){
            adapter.selectAll()
            list.addAll(newItems)
            settText(list.size.toString())
        }
        done_new_button.setOnClickListener {

            val arraySize = (intent.getSerializableExtra("newList") as ArrayList<ItemData>).size

            if (needed.toInt() > arraySize) {
                if (list.size == arraySize) {
                    val intent2 = Intent(this, FinalReceiptActivity::class.java)
                    intent2.putExtra("adapter", intent.getSerializableExtra("adapter"))
                    intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
                    intent2.putExtra("customerName", intent.getStringExtra("customerName"))
                    intent2.putExtra("newList", list as ArrayList)
                    startActivity(intent2)
                } else {
                    Toast.makeText(this, "يجب إكمال $arraySize أصناف ", Toast.LENGTH_LONG).show()
                }
            } else {
                if (list.size < needed.toInt()) {
                    Toast.makeText(this, "يجب إكمال $needed أصناف ", Toast.LENGTH_LONG).show()
                } else {
                    val intent2 = Intent(this, FinalReceiptActivity::class.java)
                    intent2.putExtra("adapter", intent.getSerializableExtra("adapter"))
                    intent2.putExtra("newList", list as ArrayList)
                    intent2.putExtra("customerName", intent.getStringExtra("customerName"))
                    intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
                    startActivity(intent2)
                }
            }

        }
    }

    private fun settText(n: String) {
        needed_items_textView.text =
            "يجب إدخال عدد " + needed + " أصناف جديدة" + "\n" + "تم إدخال $n / $needed"
    }
}
