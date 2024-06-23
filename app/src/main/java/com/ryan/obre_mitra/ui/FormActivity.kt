package com.ryan.obre_mitra.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryan.obre_mitra.databinding.ActivityFormBinding
import java.util.UUID


class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var formlatitude: Double? = null
    private var formlongitude: Double? = null
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inisialisasi FirebaseFirestore, FirebaseStorage dan FirebaseAuth
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("image_usaha")
        auth = FirebaseAuth.getInstance()

        // Set onClickListener untuk tombol submit
        binding.btnSubmit.setOnClickListener { showConfirmationDialog("usaha") { submitForm() } }

        // Set onClickListener untuk tombol Cek Lokasi
        binding.btnLokasi.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAPS)
        }

        // Set onClickListener untuk tombol Pilih Gambar
        binding.btnPilihGambar.setOnClickListener {
            pickImage()
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        // Tambahkan TextWatcher untuk memonitor perubahan pada EditText
        binding.namaUsahaEditText.addTextChangedListener(textWatcher)
        binding.deskripsiEditText.addTextChangedListener(textWatcher)
        binding.alamatEditText.addTextChangedListener(textWatcher)
        binding.alamatLengkapEditText.addTextChangedListener(textWatcher)

        // Cek status awal tombol submit
        checkFormFields()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Implementasi penanganan hasil Activity
        if (requestCode == REQUEST_CODE_MAPS && resultCode == RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            val alamat = data.getStringExtra("alamat")

            // Hanya mengatur nilai latitude dan longitude jika mereka valid (tidak 0.0)
            if (latitude != 0.0 && longitude != 0.0) {
                formlatitude = latitude
                formlongitude = longitude
            }

            alamat?.let {
                binding.alamatEditText.setText(it)
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imgUsaha.setImageURI(imageUri)
        }

        // Cek status tombol submit setelah memilih gambar atau lokasi
        checkFormFields()
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
    }

    private fun submitForm() {
        // Ambil data dari input
        val namaUsaha = binding.namaUsahaEditText.text.toString().trim()
        val deskripsi = binding.deskripsiEditText.text.toString().trim()
        val alamat = binding.alamatEditText.text.toString().trim()
        val alamatLengkap = binding.alamatLengkapEditText.text.toString().trim()

        // Cek apakah semua field terisi
        if (TextUtils.isEmpty(namaUsaha) || TextUtils.isEmpty(deskripsi) || TextUtils.isEmpty(alamatLengkap)) {
            Toast.makeText(this, "Harap lengkapi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek apakah data lokasi sudah diisi
        if (formlatitude == null || formlongitude == null) {
            Toast.makeText(this, "Harap pilih lokasi pada peta terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan progress bar
        binding.progressBar.visibility = View.VISIBLE

        // Proses upload gambar jika ada
        if (imageUri != null) {
            val ref = storageReference.child(namaUsaha + "_" + UUID.randomUUID().toString() + ".jpg")
            ref.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveDataToFirestore(namaUsaha, deskripsi, alamat, alamatLengkap, imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    // Sembunyikan progress bar
                    binding.progressBar.visibility = View.GONE
                    // Tampilkan pesan kesalahan
                    Toast.makeText(this@FormActivity, "Gagal mengunggah gambar: " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("FormActivity", "Error uploading image", e)
                }
        } else {
            saveDataToFirestore(namaUsaha, deskripsi, alamat, alamatLengkap, null)
        }
    }

    private fun saveDataToFirestore(namaUsaha: String, deskripsi: String, alamat: String, alamatLengkap: String, imageUrl: String?) {
        // Dapatkan UID pengguna yang sedang login
        val userId = auth.currentUser?.uid

        // Buat data untuk disimpan di Firestore
        val data = hashMapOf(
            "namaUsaha" to namaUsaha,
            "deskripsiSingkat" to deskripsi,
            "alamat" to alamat,
            "alamatLengkap" to alamatLengkap,
            "latitude" to formlatitude,
            "longitude" to formlongitude,
            "photoUrl" to imageUrl
        )

        // Simpan data ke Firestore dengan UID sebagai kunci dokumen
        userId?.let {
            db.collection("UserMitra").document(it)
                .update(data as Map<String, Any>)
                .addOnSuccessListener {
                    // Sembunyikan progress bar
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@FormActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    // Setelah data berhasil disimpan, arahkan pengguna ke MainActivity
                    val intent = Intent(this@FormActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    // Jika update gagal, coba dengan set(data, SetOptions.merge()) untuk memastikan data tidak hilang
                    db.collection("UserMitra").document(it)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            // Sembunyikan progress bar
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@FormActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            // Setelah data berhasil disimpan, arahkan pengguna ke MainActivity
                            val intent = Intent(this@FormActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e2 ->
                            // Sembunyikan progress bar
                            binding.progressBar.visibility = View.GONE
                            // Tampilkan pesan kesalahan
                            Toast.makeText(this@FormActivity, "Gagal menyimpan data: " + e2.message, Toast.LENGTH_SHORT).show()
                            Log.e("FormActivity", "Error adding document", e2)
                        }
                }
        }
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin data $action yang diisi sudah benar?")
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

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            checkFormFields()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun checkFormFields(): Boolean {
        val namaUsaha = binding.namaUsahaEditText.text.toString().trim()
        val deskripsi = binding.deskripsiEditText.text.toString().trim()
        val alamatLengkap = binding.alamatLengkapEditText.text.toString().trim()

        val isFormComplete = namaUsaha.isNotEmpty() && deskripsi.isNotEmpty() && alamatLengkap.isNotEmpty()
        binding.btnSubmit.isEnabled = isFormComplete
        return isFormComplete
    }

    companion object {
        private const val REQUEST_CODE_MAPS = 1001
        private const val PICK_IMAGE_REQUEST = 1002
    }
}