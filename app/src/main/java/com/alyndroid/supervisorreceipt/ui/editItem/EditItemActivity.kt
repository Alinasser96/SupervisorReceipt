package com.alyndroid.supervisorreceipt.ui.editItem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import kotlinx.android.synthetic.main.activity_edit_item.*


class EditItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        initSpinner()
        val item = intent.getParcelableExtra<ItemData>("item")!!
        suggested_tv.text = item.editedQuantity
        editedItem_editText.setText(item.editedQuantity)
        item_name_textView.text = item.itemname
        unit_textView.text = "الوحدة: " + item.default_unit

        btn_confirm_issue.setOnClickListener {
                val intent = Intent(this, FinalReceiptActivity::class.java)
                intent.putExtra("edited", editedItem_editText.text.toString())
                intent.putExtra("reason", reason_ET.selectedItem.toString())
                intent.putExtra("id", item.id.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
        }

    }

    fun initSpinner() {
        val reasons = arrayOf(
            "عميل توريدات ولا يتعامل بهذا المنتج",
            "العميل لديه بديل اقل بالسعر ومناسب للمنطقة",
            "المنتج غير مطلوب عند العميل",
            "العدد كثير على مسحوبات العميل",
            "العدد غير منطقي نهائيا"
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            reasons
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        reason_ET.adapter = adapter


    }
}
