package com.alyndroid.supervisorreceipt.ui.gard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.databinding.ActivityGardBinding
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.helpers.buildWifiDialog
import com.alyndroid.supervisorreceipt.pojo.CoordinatorItemData
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.ui.base.BaseActivity
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalReceiptActivity
import com.alyndroid.supervisorreceipt.ui.finalReciept.FinalRecieptViewModel
import com.alyndroid.supervisorreceipt.ui.newItems.NewItemsActivity
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_gard.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class GardActivity : BaseActivity() {

    private val viewModel: FinalRecieptViewModel by lazy {
        ViewModelProviders.of(this).get(FinalRecieptViewModel::class.java)
    }

    val REQUEST_IMAGE1_CAPTURE = 1
    val REQUEST_IMAGE2_CAPTURE = 2
    val REQUEST_IMAGE3_CAPTURE = 3
    var currentPhotoPath1: String? = null
    var currentPhotoPath2: String? = null
    var currentPhotoPath3: String? = null
    lateinit var itemsList: MutableList<ItemData>
    lateinit var coItemsList: MutableList<CoordinatorItemData>
    lateinit var newList: MutableList<ItemData>
    lateinit var adapter: GardItemsAdapter
    lateinit var binding: ActivityGardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gard)
        adapter = GardItemsAdapter(this)
        val customerName = intent.getStringExtra("customerName")
        title = "جرد للعميل: $customerName"

        if (SharedPreference(this).getValueString("type") == "co") {
            binding.coBtnsLayout.visibility = View.VISIBLE
            binding.coImagesLayout.visibility = View.VISIBLE
            viewModel.getAllCoItems(intent.getStringExtra("customerNo")!!)
        } else if (SharedPreference(this).getValueString("type") == "sm") {
            binding.coBtnsLayout.visibility = View.GONE
            binding.coImagesLayout.visibility = View.GONE
        }

        empty_msg_tv.setOnClickListener {
            empty_msg_tv.isVisible = false
            viewModel.getAllItems(
                SharedPreference(this).getValueString("salesman_no")!!
                , intent.getStringExtra("customerNo")!!
            )
        }

        binding.btn1.setOnClickListener {
            dispatchTakePictureIntent(REQUEST_IMAGE1_CAPTURE)
        }
        binding.btn2.setOnClickListener {
            dispatchTakePictureIntent(REQUEST_IMAGE2_CAPTURE)
        }
        binding.btn3.setOnClickListener {
            dispatchTakePictureIntent(REQUEST_IMAGE3_CAPTURE)
        }
        GardItems_recyclerView.adapter = adapter
        viewModel.response.observe(this, Observer {
            itemsList = it.items.toMutableList()
            val oldList = it.items.filter { d -> d.item_type == "old" }

            if (oldList.isEmpty()) {
                empty_gard_msg_tv.isVisible = true
                gardDone_button.isVisible = true
                items_cardView.isVisible = false
            }

            newList = it.items.filter { d -> d.item_type == "new" }.toMutableList()
            adapter.submitList(oldList.map { d -> d.itemname }.toMutableList())
            adapter.setdata(oldList.map { d -> d.itemname }.toMutableList())
        })

        viewModel.coItemsResponse.observe(this, Observer {
            coItemsList = it.items.toMutableList()
            adapter.submitList(coItemsList.map { d -> d.itemname }.toMutableList())
            adapter.setdata(coItemsList.map { d -> d.itemname }.toMutableList())
        })

        viewModel.empty.observe(this, Observer {
            items_cardView.isVisible = !it
            gardDone_button.isVisible = !it
            empty_msg_tv.isVisible = it
        })





        viewModel.error.observe(this, Observer {
            when (it) {
                1 -> buildWifiDialog(this)
            }
        })

        gardDone_button.setOnClickListener {

            if (currentPhotoPath1==null || currentPhotoPath2==null||currentPhotoPath3==null){
                Toast.makeText(this, "يجب ادخال 3 صور", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (SharedPreference(this).getValueString("type") == "co") {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                val builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                builder.addFormDataPart("coordinator_id", "59")
                    .addFormDataPart("customer_id", intent.getStringExtra("customerNo")!!)
                    .addFormDataPart("date", formatted)

                coItemsList.map { d -> d.itemno }.forEach {
                    builder.addFormDataPart("item_id[]", it.toString())
                }
                adapter.list.map { d -> d.itemCount }.forEach {
                    builder.addFormDataPart("gard_quantity[]", it.toString())
                }

                adapter.list.map { d -> d.itemCount }.forEach {
                    builder.addFormDataPart("snb_quantiy[]", it.toString())
                }

                if (currentPhotoPath1 != null) {
                    val file1 = File(currentPhotoPath1)
                    val bmp = BitmapFactory.decodeFile(file1.absolutePath)
                    val bos = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)

                    builder.addFormDataPart(
                        "img1",
                        file1.name,
                        RequestBody.create(MultipartBody.FORM, bos.toByteArray())
                    )
                }
                if (currentPhotoPath2 != null) {
                    val file2 = File(currentPhotoPath2)
                    val bmp2 = BitmapFactory.decodeFile(file2.absolutePath)
                    val bos2 = ByteArrayOutputStream()
                    bmp2.compress(Bitmap.CompressFormat.JPEG, 30, bos2)

                    builder.addFormDataPart(
                        "img2",
                        file2.name,
                        RequestBody.create(MultipartBody.FORM, bos2.toByteArray())
                    )
                }

                if (currentPhotoPath3 != null) {
                    val file3 = File(currentPhotoPath3)
                    val bmp3 = BitmapFactory.decodeFile(file3.absolutePath)
                    val bos3 = ByteArrayOutputStream()
                    bmp3.compress(Bitmap.CompressFormat.JPEG, 30, bos3)

                    builder.addFormDataPart(
                        "img3",
                        file3.name,
                        RequestBody.create(MultipartBody.FORM, bos3.toByteArray())
                    )

                }
                val requestBody: RequestBody = builder.build()
                confirmCoAction(requestBody)
            } else {
                if (empty_gard_msg_tv.isVisible) {
                    val intent2: Intent = if (newList.isEmpty())
                        Intent(this, FinalReceiptActivity::class.java)
                    else
                        Intent(this, NewItemsActivity::class.java)
                    intent2.putExtra("newList", newList as ArrayList)
                    val map = convertListToHashMap()
                    intent2.putExtra("adapter", map)
                    intent2.putExtra("customerName", intent.getStringExtra("customerName"))
                    intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
                    startActivity(intent2)
                } else {
                    val gardMap = HashMap<String, Any>()
                    gardMap["invoice_id"] = itemsList[0].invoice_id
                    gardMap["salesman_id"] = SharedPreference(this).getValueString("salesman_no")!!
                    gardMap["customer_id"] = intent.getStringExtra("customerNo")!!
                    gardMap["item_id"] =
                        itemsList.filter { d -> d.item_type != "new" }.map { d -> d.itemno }
                    gardMap["gard_quantity"] = adapter.list.map { d -> d.itemCount }
                    confirmAction(gardMap)
                }
            }


        }

        viewModel.sendGardResponse.observe(this, Observer {
            if (SharedPreference(this).getValueString("type") == "sm") {
                if (it.status) {
                    lateinit var intent2: Intent
                    if (newList.isEmpty()) {
                        intent2 = Intent(this, FinalReceiptActivity::class.java)

                    } else {
                        intent2 = Intent(this, NewItemsActivity::class.java)
                        intent2.putExtra("newList", newList as ArrayList)
                    }
                    val map = convertListToHashMap()
                    intent2.putExtra("adapter", map)
                    intent2.putExtra("customerName", intent.getStringExtra("customerName"))
                    intent2.putExtra("customerNo", intent.getStringExtra("customerNo"))
                    startActivity(intent2)
                }
            } else {
                Toast.makeText(this, "done", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, FiltersActivity::class.java))
            }
        })
        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
                gardDone_button.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                gardDone_button.isEnabled = true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
//            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            when (requestCode) {
                REQUEST_IMAGE1_CAPTURE -> setPic(binding.img1, currentPhotoPath1!!)
                REQUEST_IMAGE2_CAPTURE -> setPic(binding.img2, currentPhotoPath2!!)
                REQUEST_IMAGE3_CAPTURE -> setPic(binding.img3, currentPhotoPath3!!)
            }

        }
    }

    private fun setPic(imageView: ImageView, currentPhotoPath: String) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap.rotate(270f))
        }
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    @Throws(IOException::class)
    private fun createImageFile(requestCode: Int): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            when (requestCode) {
                REQUEST_IMAGE1_CAPTURE -> currentPhotoPath1 = absolutePath
                REQUEST_IMAGE2_CAPTURE -> currentPhotoPath2 = absolutePath
                REQUEST_IMAGE3_CAPTURE -> currentPhotoPath3 = absolutePath
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (SharedPreference(this).getValueString("type") == "sm") {
            viewModel.getAllItems(
                SharedPreference(this).getValueString("salesman_no")!!
                , intent.getStringExtra("customerNo")!!
            )
        }

    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(requestCode)
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, requestCode)
                }
            }
        }
    }


    private fun confirmCoAction(requestBody: RequestBody) {

        val materialDialog = MaterialDialog.Builder(this)
            .setTitle("انتهى الجرد؟")
            .setMessage("هل أنت متأكد من تأكيد عملية الجرد؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, _ ->
                viewModel.sendCoGard(requestBody)
                dialog.cancel()
            }
            .setNegativeButton(
                "لا"
            ) { dialog, _ -> dialog.cancel() }
            .build()
        materialDialog.show()

    }


    private fun confirmAction(gardMap: HashMap<String, Any>) {

        val materialDialog = MaterialDialog.Builder(this)
            .setTitle("انتهى الجرد؟")
            .setMessage("هل أنت متأكد من تأكيد عملية الجرد؟")
            .setCancelable(false)
            .setPositiveButton(
                "نعم"
            ) { dialog, _ ->
                viewModel.sendGard(gardMap)
                dialog.cancel()
            }
            .setNegativeButton(
                "لا"
            ) { dialog, _ -> dialog.cancel() }
            .build()
        materialDialog.show()

    }

    private fun convertListToHashMap(): HashMap<String, Any> {
        val hashmap = HashMap<String, Any>()
        for (item in adapter.list) {
            hashmap[item.itemName] = item.itemCount
        }
        return hashmap
    }
}
