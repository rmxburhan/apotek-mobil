package com.example.apotek_lks

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class InvoiceActivity : AppCompatActivity() {
    lateinit var listInvoice : RecyclerView
    private var list : ArrayList<EntityInvoice> = arrayListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    lateinit var imageUri : Uri
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        listInvoice = findViewById(R.id.invoice)
        val txtTotal = findViewById<TextView>(R.id.txtPrice)

        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnSelesai = findViewById<Button>(R.id.btnSelesai)
        val btnShare = findViewById<Button>(R.id.btnShare)

        btnShare.setOnClickListener {
            val s = loadBitmapFromView(this@InvoiceActivity.findViewById(R.id.invoicePng))
            var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHMMss")
            val date = LocalDateTime.now().format(formatter)
            if (s != null) {
                val fileNameToSave = date.toString() + "_Apotek_Invoice.png"
                val file = bitmapToFile(s, fileNameToSave)
                startActivity(Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@InvoiceActivity, "speedtest.provider", file))

                    type = "image/*"
                })
            }
        }

        btnSelesai.setOnClickListener {
            finish()
        }
        btnSave.setOnClickListener{
            try {
                imageUri = createImageUri()

                val s = loadBitmapFromView(this@InvoiceActivity.findViewById(R.id.invoicePng))
            var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHMMss")
            val date = LocalDateTime.now().format(formatter)
            if (s != null) {
                val fileNameToSave = date.toString() + "_Apotek_Invoice.png"
                val file = bitmapToFile(s, fileNameToSave)
                Toast.makeText(this, "struk berhasil disimpan", Toast.LENGTH_SHORT).show()
                startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(FileProvider.getUriForFile(this@InvoiceActivity, "speedtest.provider", file), "image/*")
                })
            }

            } catch (ex : java.lang.Exception) {
                Log.d("err-open", ex.toString())
            }
        }

        var total = intent.getStringExtra("total").toString().toInt()
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        txtTotal.text = numberFormat.format(total).toString()

        load()
    }
    fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File {
        val file = File(getExternalFilesDir(Environment.getExternalStorageDirectory().toString()), fileNameToSave)
        Log.d("err-file",file.toString())
        Log.d("err-file",fileNameToSave.toString())
        try {
            file.createNewFile()
            file.outputStream().run {
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, this)
                flush()
                close()
            }
        } catch (ex : IOException) {
            Log.d("err-flush", ex.toString())
        }
        return file

    }

    fun createImageUri() : Uri {
        val image = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext, "speedtest.provider",
            image
        )
    }


    fun load() {
        thread {
            try {
                var nama_pasien = intent.getStringExtra("nama_pasien")
                var listObatDibeli = intent.getStringArrayListExtra("obat")
                var jenis_obat = intent.getStringExtra("jenis_obat")

                list.add(EntityInvoice("Nama Pasien", nama_pasien.toString()))
                if (listObatDibeli != null){
                    for (i in 0 .. listObatDibeli.size - 1){
                        if (i == 0){
                            list.add(EntityInvoice("Nama Obat", listObatDibeli.get(i)))
                        } else {
                            list.add(EntityInvoice("", listObatDibeli.get(i)))
                        }
                    }
                }
                list.add(EntityInvoice("Jenis Obat", jenis_obat.toString()))

                runOnUiThread {
                    listInvoice.adapter = ListInvoiceAdapter(this, list)
                }
            } catch ( ex : Exception){
                Log.d("err-invoice", ex.toString())
                runOnUiThread {
                    Toast.makeText(this, "terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
class ListInvoiceAdapter(val activity: InvoiceActivity, val data : ArrayList<EntityInvoice>) : RecyclerView.Adapter<ListInvoiceHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListInvoiceHolder {
        return ListInvoiceHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_invoice, parent , false)
        )
    }

    override fun onBindViewHolder(holder: ListInvoiceHolder, position: Int) {
        var item = data.get(position)

        with(holder){
            txtKeys.text = item.keys.toString()
            txtValues.text = item.values.toString()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class ListInvoiceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtKeys = itemView.findViewById<TextView>(R.id.txtKeys)
    var txtValues = itemView.findViewById<TextView>(R.id.txtValues)
}

class EntityInvoice(var keys: String, var values : String) {
}


