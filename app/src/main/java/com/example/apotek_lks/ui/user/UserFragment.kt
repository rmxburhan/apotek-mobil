package com.example.apotek_lks.ui.user

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.apotek_lks.MainActivity
import com.example.apotek_lks.databinding.FragmentDashboardBinding
import com.example.apotek_lks.databinding.FragmentHomeBinding

class UserFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var profil : SharedPreferences
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profil = activity?.getSharedPreferences("login_session", MODE_PRIVATE)!!

        binding.txtNamaUser.setText(profil.getString("nama", null))
        binding.txtUsername.setText(profil.getString("username", null))
        binding.txtAlamat.setText(profil.getString("alamat", null))

        binding.btnLogout.setOnClickListener{
            profil.edit().clear().apply()
            activity?.finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}