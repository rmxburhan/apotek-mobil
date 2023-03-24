package com.example.apotek_lks.ui.dashboard

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.apotek_lks.Connect
import com.example.apotek_lks.InvoiceActivity
import com.example.apotek_lks.R
import com.example.apotek_lks.databinding.FragmentDashboardBinding
import com.example.apotek_lks.databinding.FragmentHomeBinding
import com.example.speedtest_android.EntityObat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private var jenisObat = arrayOf("Tablet", "Kapsul")
    private var listNamaObatDipilih : ArrayList<String> = arrayListOf()
    private var listIdObatDipilih : ArrayList<Int> = arrayListOf()
    lateinit var profil : SharedPreferences

    var nama = ""
    var harga = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Multi obat
        profil = activity?.getSharedPreferences("login_session", MODE_PRIVATE)!!
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.layout_jenis_obat, jenisObat)
        binding.edtJenisObat.setAdapter(arrayAdapter)
        binding.edtPilihObat.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_bottomFragment)
        }

        setFragmentResultListener("req") { key, bundle ->
            bundle?.getParcelableArrayList<EntityObat>("asd")?.run {
                listNamaObatDipilih.clear()
                listIdObatDipilih.clear()
                nama = ""
                harga = 0   
                binding.edtPilihObat.setText("")
                forEach {
                    if(it.isChecked){
                        harga += it.harga
                        nama += "${it.nama_obat},"
                        listNamaObatDipilih.add(it.nama_obat)
                        listIdObatDipilih.add(it.id)
                    }
                }

                nama = if (nama.length > 0) {
                    "[" + nama.substring(0, nama.length - 1) + "]"
                } else {
                    ""
                }

                binding.txtTotal.setText("Rp. " + harga.toString())
                binding.edtPilihObat.setText(nama)
            }
        }


        binding.btnTambah.setOnClickListener {
            thread {
                try {
                    if (harga != 0 && binding.edtJenisObat.text.toString() != "" && binding.edtPilihObat.text.toString() != "" && binding.edtNamaPasien.text.toString() != "") {
                        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHMMss"))
                        val dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        var nama_kasir = profil.getString("nama", null).toString()
                        var id_user = profil.getString("id", null).toString()

                        var status : Boolean = false
                        for (i in 0 .. listIdObatDipilih.size - 1){
                            status = Connect.storeTransaksi(date.toString(),dateNow, nama_kasir, harga, id_user, listIdObatDipilih.get(i))
                        }

                        activity?.runOnUiThread {
                            if (status){
                                val intent = Intent(activity, InvoiceActivity::class.java)
                                intent.putExtra("nama_pasien", binding.edtNamaPasien.text.toString())
                                intent.putExtra("obat", listNamaObatDipilih)
                                intent.putExtra("jenis_obat", binding.edtJenisObat.text.toString())
                                intent.putExtra("total", harga.toString())
                                startActivity(intent)
                                nama = ""
                                harga = 0
                                listNamaObatDipilih.clear()
                                binding.edtNamaPasien.setText("")
                                binding.edtJenisObat.setText("")
                                binding.edtPilihObat.setText("")
                            } else {
                                Toast.makeText(activity, "gagal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "lengkapi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (ex : Exception) {
                    Log.d("err-add", ex.toString())
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}