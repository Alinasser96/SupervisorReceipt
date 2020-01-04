package com.alyndroid.supervisorreceipt.ui.addItems

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.FamiliesData
import com.alyndroid.supervisorreceipt.pojo.ItemByFamiliyData
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_add_items.btn_confirm_issue
import kotlinx.android.synthetic.main.activity_add_items.editedItem_editText
import kotlinx.android.synthetic.main.activity_add_items.reason_ET
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.activity_map.*

class AddItemsActivity : AppCompatActivity(), OnItemSelectedListener {

    lateinit var familiesList: List<FamiliesData>
    lateinit var itemsList: MutableList<ItemByFamiliyData>
    lateinit var oldItemsList: List<ItemData>
    override fun onNothingSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(view: View?, position: Int, id: Long) {
        when(view!!.parent.parent.parent.parent.parent.parent){
            families_spinner->{
                viewModel.getItemByFamiliy(familiesList[position].BrandNo)
                items_spinner.isVisible = true

            }
            items_spinner->{
                quantity_layout.isVisible = true
            }
        }
    }

    private val viewModel: AddItemsViewModel by lazy {
        ViewModelProviders.of(this).get(AddItemsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_items)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        viewModel.getFamilies()
        oldItemsList = intent.getParcelableArrayListExtra<ItemData>("items").toList()
        viewModel.response.observe(this, Observer {
            familiesList = it.data
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, it.data.map { d -> d.BrandNameA }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            families_spinner.setAdapter(adapter)
            families_spinner.setOnItemSelectedListener(this)
        })


        viewModel.itemResponse.observe(this, Observer {
            itemsList = it.data.toMutableList()
            for (i in it.data){
                if (oldItemsList.any { d->d.itemname==i.ItemNameA }){
                    itemsList.remove(i)
                }
            }
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, itemsList.map { d -> d.ItemNameA }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            items_spinner.setAdapter(adapter)
            items_spinner.setOnItemSelectedListener(this)
        })

        viewModel.loading.observe(this, Observer {
            addItems_progressBar.isVisible = it
        })


        btn_confirm_issue.setOnClickListener{
            if (reason_ET.text.toString().isNotEmpty()) {
            val intent = Intent(this, FinalReceiptActivity::class.java)
            intent.putExtra("Family", families_spinner.selectedItem.toString())
            intent.putExtra("itemName", itemsList[items_spinner.selectedPosition].ItemNameA)
            intent.putExtra("itemNo", itemsList[items_spinner.selectedPosition].ItemNo)
            intent.putExtra("count", editedItem_editText.text.toString())
            intent.putExtra("reason", reason_ET.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
            } else {
                addItemReason_textInputLayout.error = getString(R.string.you_must_enter_reason)
            }
        }
    }
}
