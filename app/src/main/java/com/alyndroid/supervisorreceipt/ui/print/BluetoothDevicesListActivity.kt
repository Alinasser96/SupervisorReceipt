package com.alyndroid.supervisorreceipt.ui.print

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.pojo.ItemData
import kotlinx.android.synthetic.main.activity_bluetooth_devices_list.*
import kotlinx.android.synthetic.main.activity_main.*

class BluetoothDevicesListActivity : AppCompatActivity() {
    var m_bluetoothAdapter: BluetoothAdapter? = null
    lateinit var m_pairedDevices : Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_Address"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices_list)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter==null){
            Toast.makeText(this, "this device dose not support bluetooth", Toast.LENGTH_LONG).show()
            return
        }
        if (!m_bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener {
            pairedDeviceList()
        }
    }

    private fun pairedDeviceList(){
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if (m_pairedDevices.isNotEmpty()){
            for (device in m_pairedDevices){
                list.add(device)
            }
        } else {
            Toast.makeText(this, "no paired bluetooth devices", Toast.LENGTH_LONG).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list.map { l->l.name })
        devices_list.adapter = adapter
        devices_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device = list[position]
            val address = device.address

            val intent1 = Intent(this, PrintActivity::class.java)
            intent1.putExtra(EXTRA_ADDRESS, address)
            intent1.putExtra("itemList", intent.getParcelableArrayListExtra<ItemData>("itemList"))
            intent1.putExtra("customerName", intent.getStringExtra("customerName"))
            startActivity(intent1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if (resultCode == Activity.RESULT_OK){
                if (m_bluetoothAdapter!!.isEnabled){
                    Toast.makeText(this, "bluetooth has been enabled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "bluetooth has been disabled", Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "bluetooth enabling has been canceled", Toast.LENGTH_LONG).show()
            }
        }
    }
}
