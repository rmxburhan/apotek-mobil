package com.example.apotek_lks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import javax.microedition.khronos.egl.EGLDisplay
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtNama = findViewById<EditText>(R.id.edtNama)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtAlamat = findViewById<EditText>(R.id.edtAlamat)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val nama = edtNama.text.toString()
            val username = edtUsername.text.toString()
            val alamat = edtAlamat.text.toString()
            val password = edtPassword.text.toString()
            thread {
                try {
                    if (nama != "" && username != "" && alamat != "" && password != ""){
                        val status = Connect.register(nama, username, alamat, password)

                        if (status) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("isSuccess", true)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "gagal registrasi data tidak valid atau username sudah ada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Lengkapi seluruh data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (ex : Exception){
                    Log.d("err-register", ex.toString())
                    runOnUiThread {
                        Toast.makeText(this, "Gagal registrasi", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
}