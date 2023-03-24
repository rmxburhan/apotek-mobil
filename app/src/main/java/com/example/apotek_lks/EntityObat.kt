package com.example.speedtest_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EntityObat(val id : Int, val nama_obat : String, val harga : Int, var isChecked : Boolean) : Parcelable {
}
