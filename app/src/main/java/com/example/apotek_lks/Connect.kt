package com.example.apotek_lks

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Connect {
    companion object{
        var token: String? = ""
        val base_url = "http://192.168.124.72:8000/"

        fun register(nama : String, username : String, alamat : String, password : String) : Boolean {
            val url = URL("${base_url}api/register")

            with(url.openConnection() as HttpURLConnection){
                requestMethod = "POST"
                setRequestProperty("Content-type", "application/x-www-form-urlencoded")

                with(outputStream.bufferedWriter()){
                    write("nama=${Uri.encode(nama)}&username=${Uri.encode(username)}&alamat=${Uri.encode(alamat)}&password=${Uri.encode(password)}")
                    flush()
                }

                return when(responseCode){
                    200 -> {
                        true 
                    } else -> false
                }
            }
        }

        fun getListObat() : JSONArray? {
            val url = URL("${base_url}api/obat")

            with(url.openConnection() as HttpURLConnection){
                requestMethod = "GET"
                setRequestProperty("Authorization", token)

                return when(responseCode){
                    200 -> {
                        val result = inputStream.bufferedReader().readText()
                        val data = JSONObject(result)
                        data.getJSONArray("data")
                    } else -> null
                }
            }
        }

        fun storeTransaksi(no_transaksi : String, tgl_transaksi : String, nama_kasir : String, total_bayar : Int, user_id : String, obat_id : Int) : Boolean{
            val url = URL("${base_url}api/transaksi")


            with(url.openConnection() as HttpURLConnection){
                requestMethod = "POST"
                setRequestProperty("Content-type", "application/x-www-form-urlencoded")
                setRequestProperty("Authorization", token)

                with(outputStream.bufferedWriter()){
                    write("no_transaksi=${Uri.encode(no_transaksi)}&tgl_transaksi=${Uri.encode(tgl_transaksi)}&nama_kasir=${Uri.encode(nama_kasir)}&total_bayar=${Uri.encode(total_bayar.toString())}&user_id=${Uri.encode(user_id)}&obat_id=${Uri.encode(obat_id.toString())}")
                    flush()
                }

                return when(responseCode){
                    200 -> {
                        val result = inputStream.bufferedReader().readText()
                        true
                    } else -> false
                }
            }
        }

//        fun getProfil() : JSONArray?{
//            val url = URL("${base_url}api/profil")
//
//            with(url.openConnection() as HttpURLConnection){
//                requestMethod = "GET"
//                setRequestProperty("Authorization", token)
//
//                return when(responseCode){
//                    200 -> {
//                        val result = inputStream.bufferedReader().readText()
//                        val data = JSONObject(result)
//                        data.getJSONArray("data")
//                    } else -> null
//                }
//            }
//        }

    }

}