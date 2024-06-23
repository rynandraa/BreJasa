package com.ryan.obre_mitra.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryan.obre_mitra.databinding.ActivityEditProfileBinding
import com.ryan.obre_mitra.ui.MainActivity
import com.ryan.obre_mitra.ui.MapsActivity
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var photoUrl: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImageToFirebase(it)
        }
    }

    private val getLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                latitude = result.data?.getDoubleExtra("latitude", 0.0)
                longitude = result.data?.getDoubleExtra("longitude", 0.0)
                val alamat = result.data?.getStringExtra("alamat")
                binding.pinPointEditText.setText(alamat)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("image_usaha")

        val currentUser = firebaseAuth.currentUser
        val currentUserId = currentUser?.uid

        currentUserId?.let { userId ->
            firestore.collection("UserMitra").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("fullname")
                        val email = document.getString("email")
                        val phoneNumber = document.getString("phoneNumber")
                        val namaUsaha = document.getString("namaUsaha")
                        val deskripsi = document.getString("deskripsiSingkat")
                        val alamatLengkap = document.getString("alamatLengkap")
                        val alamat =document.getString("alamat")
                        latitude = document.getDouble("latitude")
                        longitude = document.getDouble("longitude")
                        photoUrl = document.getString("photoUrl")

                        binding.nameEditText.setText(name)
                        binding.emailEditText.setText(email)
                        binding.noTeleonEditText.setText(phoneNumber)
                        binding.namaUsahaEditText.setText(namaUsaha)
                        binding.deskripsiEditText.setText(deskripsi)
                        binding.alamatEditText.setText(alamatLengkap)
                        binding.pinPointEditText.setText(alamat)
                        photoUrl?.let {
                            Glide.with(this).load(it).into(binding.imgAvatar)
                        }
                    } else {
                        // Dokumen tidak ditemukan
                    }
                }
                .addOnFailureListener { exception ->
                    // Penanganan kesalahan saat mengambil data dari Firestore
                }
        }

        binding.txtSimpan.setOnClickListener {
            showConfirmationDialog("menyimpan") {
                // Simpan perubahan ke Firestore
                val newName = binding.nameEditText.text.toString()
                val newEmail = binding.emailEditText.text.toString()
                val newPhoneNumber = binding.noTeleonEditText.text.toString()
                val newNamaUsaha = binding.namaUsahaEditText.text.toString()
                val newDeskripsi = binding.deskripsiEditText.text.toString()
                val newAlamatLengkap = binding.alamatEditText.text.toString()
                val newPinPoint = binding.pinPointEditText.text.toString()

                currentUserId?.let { userId ->
                    val user = hashMapOf(
                        "fullname" to newName,
                        "email" to newEmail,
                        "phoneNumber" to newPhoneNumber,
                        "namaUsaha" to newNamaUsaha,
                        "deskripsiSingkat" to newDeskripsi,
                        "alamatLengkap" to newAlamatLengkap,
                        "alamat" to newPinPoint,
                        "photoUrl" to photoUrl,
                        "latitude" to latitude,
                        "longitude" to longitude
                    )

                    firestore.collection("UserMitra").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            // Penanganan sukses menyimpan data
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("FragmentName", "Profil Fragment")
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            // Penanganan kesalahan saat menyimpan data ke Firestore
                        }
                }
            }
        }

        binding.btnEditLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            getLocation.launch(intent)
        }

        binding.btnEditImage.setOnClickListener {
            getImage.launch("image/*")
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val namaUsaha = binding.namaUsahaEditText.text.toString()
        val imageReference = storageReference.child(namaUsaha + "_" + UUID.randomUUID().toString() + ".jpg")

        imageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    photoUrl = downloadUri.toString()
                    Glide.with(this).load(photoUrl).into(binding.imgAvatar)
                    Toast.makeText(this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Simpan")
            .setMessage("Apakah Anda yakin ingin $action perubahan?")
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