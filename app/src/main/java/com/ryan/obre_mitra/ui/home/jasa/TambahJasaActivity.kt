package com.ryan.obre_mitra.ui.home.jasa


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityTambahJasaBinding
import java.util.UUID

class TambahJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahJasaBinding
    private var userName: String? = null
    private var userPhone: String? = null
    private val REQUEST_IMAGE = 100
    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahJasaBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val serviceRef = userId?.let { db.collection("LayananJasa") }
        val sharedPref = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        userName = sharedPref.getString("USER_NAME", "")
        userPhone = sharedPref.getString("USER_PHONENUMBER", "")
        Log.d("Debug", "Username: $userName")
        Log.d("Debug", "Userphone: $userPhone")

        // Setup AutoCompleteTextView untuk kategori
        val kategoriJasa = resources.getStringArray(R.array.kategoriJasa)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, kategoriJasa)
        binding.autoCompleteTextView.setAdapter(adapter)

        // Inisialisasi referensi Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference.child("image_layanan")

        // Mengatur OnClickListener untuk tombol 'Kirim'
        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                showConfirmationDialog("data layanan jasa") {
                    // Mengambil nilai dari TextInputEditText
                    val namaLayanan = binding.namaLayananEditText.text.toString()
                    val harga = binding.hargaEditText.text.toString().toDoubleOrNull()
                    val batasPelayanan = binding.btsPelayananEditText.text.toString().toInt()
                    val deskripsi = binding.deskripsiEditText.text.toString()
                    val kategori = binding.autoCompleteTextView.text.toString() // Ambil nilai dari AutoCompleteTextView

                    // Jika gambar telah dipilih
                    if (selectedImageUri != null) {
                        // Membuat nama file unik untuk gambar yang diunggah
                        val filename = UUID.randomUUID().toString()
                        // Membuat referensi untuk gambar di Firebase Storage
                        val imageRef = storageRef.child(filename)

                        // Mengunggah gambar ke Firebase Storage
                        val uploadTask = imageRef.putFile(selectedImageUri!!)

                        // Menangani keberhasilan atau kegagalan unggah gambar
                        uploadTask.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Gambar berhasil diunggah, dapatkan URL gambar yang diunggah
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    // URL gambar berhasil didapatkan, simpan data layanan ke Firestore
                                    val layanan = hashMapOf(
                                        "idOwner" to userId,
                                        "pemilik" to userName,
                                        "phoneNumberService" to userPhone,
                                        "namaLayanan" to namaLayanan,
                                        "biayaJasa" to harga,
                                        "deskripsi" to deskripsi,
                                        "batasPelayananSehari" to batasPelayanan,
                                        "jumlahPelayananSaatIni" to 0,
                                        "kategori" to kategori,
                                        "photoUrl" to uri.toString() // Simpan URL gambar di Firestore
                                    )

                                    // Menambahkan data layanan ke Firebase Firestore
                                    serviceRef?.add(layanan)
                                        ?.addOnSuccessListener { documentReference ->
                                            // Data berhasil ditambahkan
                                            Log.d(
                                                TAG,
                                                "Layanan berhasil ditambahkan dengan ID: ${documentReference.id}"
                                            )
                                            // Tampilkan pesan sukses atau lakukan tindakan lain yang sesuai
                                            // Arahkan pengguna ke MenuJasaActivity
                                            val intent =
                                                Intent(this, MenuJasaActivity::class.java).apply {
                                                    flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                                }
                                            startActivity(intent)
                                            finish() // Tutup activity saat ini
                                        }
                                        ?.addOnFailureListener { e ->
                                            // Gagal menambahkan data
                                            Log.w(TAG, "Error adding document", e)
                                            // Tampilkan pesan error atau lakukan penanganan kesalahan yang sesuai
                                        }
                                }
                            } else {
                                // Gagal mengunggah gambar
                                Log.e(TAG, "Failed to upload image", task.exception)
                                // Tampilkan pesan error atau lakukan penanganan kesalahan yang sesuai
                            }
                        }
                    } else {
                        // Jika gambar tidak dipilih
                        Log.e(TAG, "No image selected")
                        // Tampilkan pesan error atau lakukan penanganan kesalahan yang sesuai
                    }
                }
            } else {
                // Tampilkan pesan error jika ada input yang kosong atau tidak valid
                AlertDialog.Builder(this)
                    .setTitle("Input Tidak Lengkap")
                    .setMessage("Silakan lengkapi semua field terlebih dahulu.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        binding.btnPilihGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        binding.btnBack.setOnClickListener {
            // Kembali ke activity sebelumnya
            onBackPressed()
        }
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin $action yang anda masukkan sudah sesuai?")
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

    private fun validateInput(): Boolean {
        val namaLayanan = binding.namaLayananEditText.text.toString()
        val harga = binding.hargaEditText.text.toString().toDoubleOrNull()
        val batasPelayanan = binding.btsPelayananEditText.text.toString().toIntOrNull()
        val deskripsi = binding.deskripsiEditText.text.toString()
        val kategori = binding.autoCompleteTextView.text.toString()

        if (namaLayanan.isBlank() || harga == null || batasPelayanan == null || deskripsi.isBlank() || kategori.isBlank()) {
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.imgLayanan.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val TAG = "TambahJasaActivity"
    }
}