package com.example.apotek_lks

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var backPressedTime:Long = 0
    lateinit var backToast:Toast
    lateinit var profil : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);
        }

        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        btnLogin.setOnClickListener{
            var username = edtUsername.text.toString()
            var password = edtPassword.text.toString()
            thread {
                try {
                    if (username != "" && password != ""){
                        val status = login(username, password)

                        runOnUiThread {
                            if (status){
                                startActivity(Intent(this, HomeActivity::class.java))
                                edtUsername.text.clear()
                                edtPassword.text.clear()
                            } else {
                                Toast.makeText(this, "username atau password salah", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (ex : Exception){
                    Log.d("err-login", ex.toString())
                    runOnUiThread {
                        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnRegister.setOnClickListener{
                startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(username : String, password : String): Boolean {
        val url = URL("${Connect.base_url}api/login")

        with(url.openConnection() as HttpURLConnection){
            requestMethod = "POST"
            setRequestProperty("Content-type", "application/x-www-form-urlencoded")

            with(outputStream.bufferedWriter()){
                write("username=${Uri.encode(username)}&password=${Uri.encode(password)}")
                flush()
            }

            return when(responseCode){
                200 -> {
                    val result = inputStream.bufferedReader().readText()
                    val data = JSONObject(result)
                    val userdata = data.getJSONObject("payload")
                    Connect.token = "Bearer " + data["token"].toString()
                    getSharedPreferences("login_session", MODE_PRIVATE)
                        .edit()
                        .putString("nama", userdata["nama"].toString())
                        .putString("username", userdata["username"].toString())
                        .putString("id", userdata["id"].toString())
                        .putString("alamat", userdata["alamat"].toString())
                        .putString("token", data["token"].toString())
                        .apply()
                    true
                } else -> false
            }
        }
    }


    override fun onBackPressed() {
        backToast = Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG)
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}