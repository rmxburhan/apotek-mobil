package com.example.apotek_lks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_android.EntityObat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject
import kotlin.concurrent.thread

class BottomFragment : BottomSheetDialogFragment() {

    private var listPilihan : ArrayList<EntityObat> = ArrayList()
    private var listPilihanFiltered : ArrayList<EntityObat> = ArrayList()
    lateinit var listObat : RecyclerView
    lateinit var search : SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_bottom, container, false)
        listObat = view.findViewById(R.id.listObat)
        search = view.findViewById(R.id.searchView)
        search.clearFocus()
        search.setOnClickListener {
            search.requestFocus()
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String): Boolean {
                filteredList(text)
                return true
            }

            fun filteredList(text: String) {
                Log.d("text-random", text)
                listPilihanFiltered.clear()
                listPilihanFiltered.addAll(listPilihan.filter {
                    it.nama_obat.lowercase().startsWith(text.lowercase())
                })
                listObat.adapter?.notifyDataSetChanged()
            }
        })
        load()
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            with(Bundle()) {
                putParcelableArrayList("asd", listPilihan)
                setFragmentResult("req", this)
                findNavController().popBackStack()
//                findNavController().navigate(R.id.action_bottomSheetObat_to_navigation_home, this)
            }
        }
        return view
    }


private fun load() {
    thread {
        try {
            val data = Connect.getListObat()

            activity?.runOnUiThread {
                if (data != null) {
                    for (i in 0..data.length() - 1) {
                        var x: JSONObject = data.getJSONObject(i)
                        listPilihan.add(
                            EntityObat(
                                x["id"].toString().toInt(),
                                x["nama_obat"].toString(),
                                x["harga"].toString().toInt(),
                                false
                            )
                        )
                    }
                    listPilihanFiltered.clear()
                    listPilihanFiltered.addAll(listPilihan)

                    listObat.adapter = ListObatAdapter(listPilihanFiltered, this)
                } else {
                    Toast.makeText(activity, "tidak ada data obat", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        } catch (ex: Exception) {
            Log.d("err-getlist", ex.toString())
            activity?.runOnUiThread {
                Toast.makeText(activity, "terjadi kesalahan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
}


class ListObatAdapter(val listPilihan: ArrayList<EntityObat>, val bottomSheet: BottomFragment) : RecyclerView.Adapter<ListObatHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListObatHolder {
        return ListObatHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_list_obat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListObatHolder, position: Int) {
        var item = listPilihan.get(position)

        with(holder){
            txtNamaObat.text = item.nama_obat.toString()
            checkBox.setOnClickListener {
                listPilihan.find { it.id == item.id.toString().toInt() }?.isChecked = checkBox.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return listPilihan.size
    }

}

class ListObatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtNamaObat = itemView.findViewById<TextView>(R.id.txtNamaObat)
    var checkBox = itemView.findViewById<CheckBox>(R.id.checkBoxObat)
}

