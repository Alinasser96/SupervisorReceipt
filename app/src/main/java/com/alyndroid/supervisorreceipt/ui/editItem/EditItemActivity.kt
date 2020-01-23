package com.alyndroid.supervisorreceipt.ui.editItem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import kotlinx.android.synthetic.main.activity_edit_item.*
import kotlinx.android.synthetic.main.product_item.*

class EditItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        val item = intent.getParcelableExtra<ItemData>("item")!!
        suggested_tv.text = item.editedQuantity
        editedItem_editText.setText(item.editedQuantity)
        item_name_textView.text = item.itemname
        unit_textView.text = "الوحدة: " + item.default_unit

        btn_confirm_issue.setOnClickListener {
            if (reason_ET.text.toString().isNotEmpty()) {
                issue_content_text_input_layout.error = null
                val intent = Intent(this, FinalReceiptActivity::class.java)
                intent.putExtra("edited", editedItem_editText.text.toString())
                intent.putExtra("reason", reason_ET.text.toString())
                intent.putExtra("id", item.id.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                issue_content_text_input_layout.error = getString(R.string.you_must_enter_reason)
            }
        }

    }
}
