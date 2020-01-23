package com.alyndroid.supervisorreceipt.ui.subordinates

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import kotlinx.android.synthetic.main.activity_subordinates.*

class SalesMenActivity : BaseActivity() {

    private val viewModel: SalesMenViewModel by lazy {
        ViewModelProviders.of(this).get(SalesMenViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subordinates)
        title = getString(R.string.chooseSalesMan)
        viewModel.getAllSalesMen(SharedPreference(this).getValueString("salesman_no")!!)
        val adapter = SalesMenAdapter(this, SalesMenAdapter.SalesManClickListener {
            val intent = Intent(this, FiltersActivity::class.java)
            intent.putExtra("salesManNo", it.SubordinateId)
            intent.putExtra("salesManName", it.salesmannamea)
            startActivity(intent)
        })
        salesmen_recyclerView.adapter = adapter
        viewModel.response.observe(this, Observer {
            adapter.submitList(it)
        })
    }
}
