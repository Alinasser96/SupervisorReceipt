package com.alyndroid.supervisorreceipt.ui.filters

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.alyndroid.instabugpremierleague.ui.matchs.FiltersAdapter
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.CustomersResponse
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.login.LoginActivity
import com.alyndroid.supervisorreceipt.ui.map.MapActivity
import com.alyndroid.supervisorreceipt.ui.subordinates.SalesMenActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_activity_dashboard.*

class FiltersActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    private val viewModel: FiltersViewModel by lazy {
        ViewModelProviders.of(this).get(FiltersViewModel::class.java)
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var logoutMenuItem: MenuItem
    //    @BindView(R.id.tool_bar)
    private lateinit var toolbar: Toolbar
    private var toggle: ActionBarDrawerToggle? = null

    private lateinit var customersList: CustomersResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        when (SharedPreference(this).getValueString("type")) {
            "sv" -> {
                viewModel.getAllCustomers(intent.getStringExtra("salesManNo")!!)
                title = getString(R.string.superVisorApp)
                changeSalesMan_Button.visibility = View.VISIBLE
            }
            "sm" -> {
                viewModel.getAllCustomers(SharedPreference(this).getValueString("salesman_no")!!)
                title = getString(R.string.SalesManApp)
                changeSalesMan_Button.visibility = View.GONE
            }
        }


        viewModel.allResponse.observe(this, Observer {
            customersList = it
        })

        chooseCustomers_Button.setOnClickListener {
            filters_progressBar.visibility = View.VISIBLE
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("customers", customersList)
            startActivity(intent)
        }

        changeSalesMan_Button.setOnClickListener {
            val intent = Intent(this, SalesMenActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.loading.observe(this, Observer {
            if (it) {
                changeSalesMan_Button.isEnabled = false
                chooseCustomers_Button.isEnabled = false
                filters_progressBar.visibility = View.VISIBLE
            } else {
                filters_progressBar.visibility = View.GONE
                changeSalesMan_Button.isEnabled = true
                chooseCustomers_Button.isEnabled = true
            }
        })

        toolbar = filters_toolbar
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle!!)
        toggle!!.syncState()
        logoutMenuItem = navigationView.menu.findItem(R.id.logout)

        navigationView.setNavigationItemSelectedListener(this)


        navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_full_name).text =
            SharedPreference(this).getValueString("name")


        navigationView.menu.findItem(R.id.nav_version).title =
            this.packageManager.getPackageInfo(this.packageName, 0).versionName

    }

    override fun onResume() {
        super.onResume()
        filters_progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.logout -> logoutAction()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutAction() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("هل أنت متأكد من تسجيل الخروج ؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, id ->
                //here
                SharedPreference(this).logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        alertDialogBuilder.setNegativeButton(
            "لا"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }


}
