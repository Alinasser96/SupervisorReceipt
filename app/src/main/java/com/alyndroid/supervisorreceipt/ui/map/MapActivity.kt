package com.alyndroid.supervisorreceipt.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.All
import com.alyndroid.supervisorreceipt.pojo.CustomersData
import com.alyndroid.supervisorreceipt.pojo.CustomersResponse
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import com.alyndroid.supervisorreceipt.ui.gard.GardActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : BaseActivity(), OnMapReadyCallback, OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {


    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLatLong: LatLng? = null
    var currentLocation: Location? = null
    private var googleMap: GoogleMap? = null
    private lateinit var customers: List<All>
    var newCustomers = mutableListOf<All>()
    var editedCustomers = mutableListOf<All>()
    lateinit var usedCustomers: CustomersData
    private val nearbyCustomers = mutableListOf<All>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        title = getString(R.string.customers_Map)
        progressBar.visibility = View.VISIBLE
        usedCustomers = intent.getParcelableExtra<CustomersResponse>("customers")!!.data
        customers = usedCustomers.all
        formatCustomers()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        chip.setOnCheckedChangeListener(this)
        new_customers_chip.setOnCheckedChangeListener(this)
        edited_customers_chip.setOnCheckedChangeListener(this)
        all_customers_chip.setOnCheckedChangeListener(this)
        all_customers_chip.isClickable = false

    }


    override fun onResume() {
        super.onResume()
        chip.isChecked = false
        getLastLocation()
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        progressBar.visibility = View.GONE
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        setupMap(location, false)
                    }
                }
            } else {
                buildLocationDialog(this)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            setupMap(mLastLocation, false)
        }
    }

    private fun buildLocationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enable Your Location")
            .setMessage("we need to access your location so you should enable it?")
            .setPositiveButton("Ok, Enable it") { dialog, which ->
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Not Now") { dialog, which ->
                Toast.makeText(applicationContext, "You are not agree.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onNothingSelected() {}

    override fun onItemSelected(view: View?, position: Int, id: Long) {
        val name = (view as TextView).text
        var customerData:All? = null
        for (customer in customers){
            if (customer.customernamea==name){
                customerData= customer
                break
            }
        }

        if (SharedPreference(this).getValueString("type") == "sm") {
            val intent = Intent(this, GardActivity::class.java)
            intent.putExtra("customerNo", customerData!!.customerno)
            intent.putExtra("customerName", customerData.customernamea)
            startActivity(intent)
        } else {
            val intent = Intent(this, FinalReceiptActivity::class.java)
            if (chip.isChecked) {
                intent.putExtra("customerNo", customerData!!.customerno)
                intent.putExtra("customerName", customerData.customernamea)
            } else {
                var nearbyCustomer = nearbyCustomers.find { d->d.customernamea==name}!!
                intent.putExtra("customerNo", nearbyCustomer.customerno)
                intent.putExtra("customerName", nearbyCustomer.customernamea)
            }
            intent.putExtra("areShown", chip.isChecked)
            startActivity(intent)
        }
    }

    private fun setupMap(location: Location, showAll: Boolean) {
        if (googleMap != null)
            googleMap!!.clear()
        if (customers.isEmpty())
        { Toast.makeText(this, "لا يوجد عملاء حاليين", Toast.LENGTH_LONG).show()
            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, emptyList()
            )
            names_spinner.setAdapter(adapter)
            return}
        nearbyCustomers.clear()
        currentLatLong = LatLng(location.latitude, location.longitude)
        currentLocation = Location("")
        currentLocation!!.latitude = location.latitude
        currentLocation!!.longitude = location.longitude
        val markerOptions: MarkerOptions =
            MarkerOptions().position(currentLatLong!!).title("your location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon))

        val distances = mutableListOf<Float>()
        val builder = LatLngBounds.Builder()
        for (customer in customers) {
            if (customer.latitude != null) {
                val location = Location("")
                location.latitude = customer.latitude.toDouble()
                location.longitude = customer.longitude!!.toDouble()
                distances.add(currentLocation!!.distanceTo(location))
                val markerOptions: MarkerOptions = MarkerOptions().position(
                    LatLng(
                        customer.latitude.toDouble(),
                        customer.longitude.toDouble()
                    )
                ).title(customer.customernamea).snippet(customer.customerno)
                if (showAll) {
                    builder.include(
                        LatLng(
                            customer.latitude.toDouble(),
                            customer.longitude.toDouble()
                        )
                    )
                    googleMap!!.addMarker(markerOptions).showInfoWindow()
                }
            }
        }


        if (distances.isEmpty())
            finish()


        if (!showAll) {
            for (i in 0..4) {
                val minDistance = distances.min()!!
                if (minDistance > 100)
                    break
                val customer = customers[distances.indexOf(minDistance)]
                nearbyCustomers.add(customer)
                builder.include(
                    LatLng(
                        customer.latitude!!.toDouble(),
                        customer.longitude!!.toDouble()
                    )
                )
                val markerOptions: MarkerOptions = MarkerOptions().position(
                    LatLng(
                        customer.latitude.toDouble(),
                        customer.longitude.toDouble()
                    )
                ).title(customer.customernamea).snippet(customer.customerno)
                googleMap!!.addMarker(markerOptions).showInfoWindow()
                distances[distances.indexOf(minDistance)] = distances.max()!!


            }


            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item, nearbyCustomers.map { s -> s.customernamea }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            names_spinner.setAdapter(adapter)
            names_spinner.setOnItemSelectedListener(this)
            if (nearbyCustomers.isEmpty()) {
                googleMap!!.addMarker(markerOptions).showInfoWindow()
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 17f))
                Toast.makeText(this, "no nearby customers", Toast.LENGTH_LONG).show()
                return
            }
        }

        builder.include(currentLatLong)

        googleMap.let {
            it!!.addMarker(markerOptions).showInfoWindow()
            it.setOnInfoWindowClickListener {
                if (it.title != "your location") {
                    if (SharedPreference(this).getValueString("type") == "sm") {
                        val intent = Intent(this, GardActivity::class.java)
                        intent.putExtra("customerNo", it.snippet)
                        intent.putExtra("customerName", it.title)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, FinalReceiptActivity::class.java)
                        intent.putExtra("customerNo", it.snippet)
                        intent.putExtra("customerName", it.title)
                        intent.putExtra("areShown", showAll)
                        startActivity(intent)
                    }
                }
            }
            if (showAll) {
                it.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
            } else {
                it.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 350))
            }
        }

    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

        when (p0!!.id) {
            R.id.chip -> {
                if (googleMap != null)
                    googleMap!!.clear()
                if (p1) {
                    setupMap(currentLocation!!, true)
                } else {
                    setupMap(currentLocation!!, false)
                }
            }
            R.id.new_customers_chip -> {
                if (p1) {
                    Toast.makeText(this, "new checked", Toast.LENGTH_LONG).show()
                    all_customers_chip.isClickable = true
                    new_customers_chip.isClickable = false
                    edited_customers_chip.isClickable = true
                    customers = newCustomers
                    setupMap(currentLocation!!, chip.isChecked)
                } else {
                }
            }
            R.id.edited_customers_chip -> {
                if (p1) {
                    Toast.makeText(this, "edited checked", Toast.LENGTH_LONG).show()
                    all_customers_chip.isClickable = true
                    new_customers_chip.isClickable = true
                    edited_customers_chip.isClickable = false
                    customers = editedCustomers
                    setupMap(currentLocation!!, chip.isChecked)
                } else {
                }
            }
            R.id.all_customers_chip -> {
                if (p1) {
                    Toast.makeText(this, "all checked", Toast.LENGTH_LONG).show()
                    all_customers_chip.isClickable = false
                    new_customers_chip.isClickable = true
                    edited_customers_chip.isClickable = true
                    customers = usedCustomers.all
                    setupMap(currentLocation!!, chip.isChecked)
                } else {
                }
            }
        }
        if (p1) {
            if (chip.isChecked) {
                val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item, customers.map { s -> s.customernamea }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                names_spinner.setAdapter(adapter)
                names_spinner.setOnItemSelectedListener(this)
            }
        }
    }


    private fun formatCustomers() {
        for (i in customers) {
            if (usedCustomers.new.contains(i.customerno)) {
                newCustomers.add(i)
            }
            if (usedCustomers.new_item.map { c -> c.customerno }.distinct().contains(i.customerno)) {
                editedCustomers.add(i)
            }
        }
    }

}

