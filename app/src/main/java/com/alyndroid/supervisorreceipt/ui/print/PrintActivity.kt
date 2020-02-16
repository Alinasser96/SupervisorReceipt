package com.alyndroid.supervisorreceipt.ui.print

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_print.*
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PrintActivity : AppCompatActivity() {

    companion object {
        var myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected = false
        lateinit var address: String
    }

    lateinit var list: ArrayList<ItemData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
        address = intent.getStringExtra(BluetoothDevicesListActivity.EXTRA_ADDRESS)!!
        ConnectToDevice(this).execute()
        list = intent.getParcelableArrayListExtra<ItemData>("itemList") ?: arrayListOf()
        btn_print.setOnClickListener {
            val materialDialog = MaterialDialog.Builder(this)
                .setTitle("طباعة")
                .setMessage("هل أنت متأكد من طباعة الفاتورة؟")
                .setCancelable(false)
                .setPositiveButton(
                    "نعم"
                ) { dialog, _ ->
                    sendCommand()
                    dialog.cancel()
                }
                .setNegativeButton(
                    "لا"
                ) { dialog, _ -> dialog.cancel() }
                .build()
            materialDialog.show()
        }
        btn_change_printer.setOnClickListener { disconnect() }
        btn_done.setOnClickListener {
            startActivity(Intent(this, FiltersActivity::class.java))
            finish()
        }
    }

    private fun sendCommand() {
        if (bluetoothSocket != null) {
            try {
                val os: OutputStream = bluetoothSocket!!
                    .outputStream

                val arabicFont = byteArrayOf(0x1b, 0x77, 0x46)
                os.write(arabicFont)
                val arabic864 = Arabic864()
                val arabicTXT2 = arabic864.Convert("\n", false)
                os.write(arabicTXT2)
                val SNB = arabic864.Convert("\nفاتورة مقترحة                  ", false)
                os.write(SNB)

                val SVName = arabic864.Convert(
                    "\n" + rtlText(
                        "المندوب: " + SharedPreference(this).getValueString("name")!!
                    ), false
                )
                os.write(SVName)

                val customerName = arabic864.Convert(
                    "\n" + rtlText("العميل: " + intent.getStringExtra("customerName")),
                    false
                )
                os.write(customerName)

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                val date = arabic864.Convert("\n" + rtlText("تاريخ: " + formatted), false)
                os.write(date)

                os.write(("=".repeat(48) + "\n").toByteArray())

                val timp = arabic864.Convert(
                    "\n" + rtlText("الوحدة", "الكمية", "الصنف"),
                    false
                )
                os.write(timp)

                for (item in list) {
                    val itemName = if (item.itemname.length > 28)
                        arabic864.Convert(
                            "\n" + rtlText(
                                item.default_unit,
                                item.quantity,
                                item.itemname.substring(0, 28)
                            ), false
                        )
                    else
                        arabic864.Convert(
                            "\n" + rtlText(
                                item.default_unit,
                                item.quantity,
                                item.itemname
                            ), false
                        )
                    os.write(itemName)
                }

                os.write(("\n".repeat(2)).toByteArray())


                val gs = 29
                os.write(intToByteArray(gs).toInt())
                val h = 104
                os.write(intToByteArray(h).toInt())
                val n = 162
                os.write(intToByteArray(n).toInt())
                // Setting Width
                val gs_width = 29
                os.write(intToByteArray(gs_width).toInt())
                val w = 119
                os.write(intToByteArray(w).toInt())
                val n_width = 2
                os.write(intToByteArray(n_width).toInt())
            } catch (e: Exception) {
                Log.e("PrintActivity", "Exe ", e)
            }
        }
    }

    private fun disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    class ConnectToDevice(context: Context) : AsyncTask<Void, Void, String>() {
        private var connectionSuccess = true
        private val context: Context

        init {
            this.context = context
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (bluetoothSocket == null || !isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    bluetoothAdapter.cancelDiscovery()
                    bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectionSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectionSuccess) {
                Toast.makeText(context, "can not connect to this device", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "connected", Toast.LENGTH_LONG).show()
                isConnected = true
            }
            progress.dismiss()
        }
    }

    fun intToByteArray(value: Int): Byte {
        val b = ByteBuffer.allocate(4).putInt(value).array()
        for (k in b.indices) {
            println(
                "Selva  [" + k + "] = " + "0x"
                        + UnicodeFormatter.byteToHex(b[k])
            )
        }
        return b[3]
    }

    fun rtlText(txt: String): String {
        val spaceSize = 48 - txt.length
        val spaces = " ".repeat(spaceSize)
        return txt + spaces
    }

    fun rtlText(txt1: String, txt2: String, txt3: String): String {
        val spaceSize = 48 - (txt1.length + +5 + txt2.length + txt3.length)
        val spaces = " ".repeat(spaceSize)
        return txt3 + spaces + txt2 + "     " + txt1
    }

}