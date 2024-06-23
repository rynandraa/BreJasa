package com.ryan.obre_mitra.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.ui.login.LoginActivity
import com.ryan.obre_mitra.ui.login.LoginViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        view.findViewById<View>(R.id.pengaturanAccbtn).setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<View>(R.id.hubungikamibtn).setOnClickListener{
            val intent = Intent(requireContext(), ContactUsActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<View>(R.id.aboutUsBtn).setOnClickListener{
            val intent = Intent(requireContext(), AboutUsActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<View>(R.id.logoutBtn).setOnClickListener {
            showConfirmationDialog("logout?") {
            lifecycleScope.launch {
                // Melakukan logout
                val loginViewModel = ViewModelProvider(
                    requireActivity(),
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                )[LoginViewModel::class.java]
                loginViewModel.logoutUser()

                // Redirect pengguna ke layar login
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
            }
        }



        // Mendapatkan userID pengguna yang saat ini login
        val userId = FirebaseAuth.getInstance().uid

        // Mengambil data dari dokumen UserMitra berdasarkan userID
        userId?.let { uid ->
            firestore.collection("UserMitra").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    // Memeriksa apakah dokumen ada
                    if (document.exists()) {
                        // Mengambil nilai nama, email, dan photoUrl dari dokumen
                        val nama = document.getString("fullname")
                        val email = document.getString("email")
                        val photoUrl = document.getString("photoUrl")

                        // Menetapkan nilai nama dan email ke TextView di layout
                        view.findViewById<TextView>(R.id.text_nama_pengguna)?.text = nama
                        view.findViewById<TextView>(R.id.text_email)?.text = email

                        // Menetapkan nilai photoUrl ke CircleImageView di layout
                        val imageView = view.findViewById<CircleImageView>(R.id.img_avatar)
                        Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.sample_avatar) // Placeholder jika tidak ada gambar
                            .error(R.drawable.sample_avatar) // Gambar yang ditampilkan jika gagal memuat
                            .into(imageView)
                    } else {
                        // Jika dokumen tidak ada, tampilkan pesan kesalahan
                        Toast.makeText(
                            requireContext(),
                            "Dokumen tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Jika gagal mengambil data, tampilkan pesan kesalahan
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin $action")
            .setPositiveButton("Ya") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}