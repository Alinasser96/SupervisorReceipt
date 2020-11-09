package com.alyndroid.supervisorreceipt.ui.editItem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.activity_edit_item.addItemReason_textInputLayout
import kotlinx.android.synthetic.main.activity_edit_item.btn_confirm_issue
import kotlinx.android.synthetic.main.activity_edit_item.editedItem_editText
import kotlinx.android.synthetic.main.activity_edit_item.reason_ET


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
        val button: Button = findViewById(R.id.reason_switch_button)

        button.setOnClickListener {
            if (addItemReason_textInputLayout.visibility == View.GONE) {
                reason_switch_button.text = getString(R.string.choose_reason)
                addItemReason_textInputLayout.visibility = View.VISIBLE
                reason_spinner.visibility = View.GONE
            } else {
                reason_switch_button.text = getString(R.string.write_reason)
                addItemReason_textInputLayout.visibility = View.GONE
                reason_spinner.visibility = View.VISIBLE
            }
        }

        btn_confirm_issue.setOnClickListener {
            if (addItemReason_textInputLayout.visibility == View.VISIBLE && reason_ET.editableText.toString()
                    .isEmpty()
            ) {
                addItemReason_textInputLayout.error = getString(R.string.you_must_enter_reason)
            } else {
                addItemReason_textInputLayout.error = null
                val intent = Intent(this, FinalReceiptActivity::class.java)
                intent.putExtra("edited", editedItem_editText.text.toString())
                intent.putExtra("id", item.id.toString())
                if (addItemReason_textInputLayout.visibility == View.GONE) {
                    intent.putExtra("reason", reason_spinner.selectedItem.toString())
                } else {
                    intent.putExtra("reason", reason_ET.editableText.toString())
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

    }

    fun initSpinner() {
        val reasons = arrayOf(
            "عميل توريدات ولا يتعامل بهذا المنتج",
            "العميل لديه بديل اقل بالسعر ومناسب للمنطقة",
            "المنتج غير مطلوب عند العميل",
            "العدد كثير على مسحوبات العميل",
            "العدد غير منطقي نهائيا",
            "المنتج غيرموجود بالشركة"
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            reasons
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        reason_spinner.adapter = adapter


    }
}
