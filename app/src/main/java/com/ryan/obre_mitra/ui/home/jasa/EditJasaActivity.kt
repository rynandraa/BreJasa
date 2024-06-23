package com.ryan.obre_mitra.ui.home.jasa

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityEditJasaBinding

class EditJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditJasaBinding
    private var imageUri: Uri? = null
    private val storageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditJasaBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        // Dapatkan data Layanan dari intent
        val layananjasa = intent.getParcelableExtra<LayananJasa>("Data")
        val documentId = intent.getStringExtra("DocumentId")

        // Setel nilai-nilai dari layananjasa ke elemen-elemen UI
        binding.namaLayananEditText.setText(layananjasa?.namaLayanan)
        binding.hargaEditText.setText(layananjasa?.biayaJasa.toString())
        binding.btsPelayananEditText.setText(layananjasa?.batasPelayananSehari.toString())
        binding.deskripsiEditText.setText(layananjasa?.deskripsi)

        // Setup AutoCompleteTextView untuk kategori
        val kategoriJasa = resources.getStringArray(R.array.kategoriJasa)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, kategoriJasa)
        binding.autoCompleteTextView.setAdapter(adapter)

        // Set nilai awal untuk AutoCompleteTextView
        binding.autoCompleteTextView.setText(layananjasa?.kategori, false)

        // Load image using Glide
        layananjasa?.photoUrl?.let {
            Glide.with(this).load(it).into(binding.imgLayanan)
        }

        // Handle pilih gambar
        binding.btnPilihGambar.setOnClickListener {
            selectImage()
        }

        // Handle klik tombol kembali
        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        // Handle klik tombol kirim
        binding.btnSubmit.setOnClickListener {
            showConfirmationDialog("menyimpan perubahan pada"){
                if (layananjasa != null && documentId != null) {
                    saveChangesToFirestore(documentId, layananjasa)
                }
            }
        }
        // Handle klik tombol hapus
        binding.btndelete.setOnClickListener {
            showConfirmationDialog("Menghapus") {
                if (layananjasa != null && documentId != null) {
                    deleteLayananJasa(documentId, layananjasa)
                }
            }
        }
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin $action layanan jasa ini?")
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

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.imgLayanan.setImageURI(imageUri)
        }
    }

    private fun saveChangesToFirestore(documentId: String, layananjasa: LayananJasa) {
        val namaLayanan = binding.namaLayananEditText.text.toString()
        val harga = binding.hargaEditText.text.toString().toInt()
        val batasPelayanan = binding.btsPelayananEditText.text.toString().toInt()
        val deskripsi = binding.deskripsiEditText.text.toString()
        val kategori = binding.autoCompleteTextView.text.toString()

        val db = FirebaseFirestore.getInstance()
        val layananJasaRef = db.collection("LayananJasa").document(documentId)

        binding.progressBar.visibility = View.VISIBLE

        if (imageUri != null) {
            val imageRef = storageReference.child("images_layanan/${layananjasa.namaLayanan}.jpg")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val updatedLayananJasa = layananjasa.copy(
                            namaLayanan = namaLayanan,
                            biayaJasa = harga,
                            batasPelayananSehari = batasPelayanan,
                            deskripsi = deskripsi,
                            kategori = kategori,
                            photoUrl = uri.toString()
                        )
                        updateFirestore(layananJasaRef, updatedLayananJasa)
                    }
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            val updatedLayananJasa = layananjasa.copy(
                namaLayanan = namaLayanan,
                biayaJasa = harga,
                batasPelayananSehari = batasPelayanan,
                deskripsi = deskripsi,
                kategori = kategori
            )
            updateFirestore(layananJasaRef, updatedLayananJasa)
        }
    }

    private fun updateFirestore(layananJasaRef: DocumentReference, updatedLayananJasa: LayananJasa) {
        layananJasaRef.update(
            mapOf(
                "namaLayanan" to updatedLayananJasa.namaLayanan,
                "biayaJasa" to updatedLayananJasa.biayaJasa,
                "batasPelayananSehari" to updatedLayananJasa.batasPelayananSehari,
                "deskripsi" to updatedLayananJasa.deskripsi,
                "kategori" to updatedLayananJasa.kategori,
                "photoUrl" to updatedLayananJasa.photoUrl
            )
        ).addOnSuccessListener {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
            val intent =
                Intent(this, MenuJasaActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            startActivity(intent)
            finish()
        }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteLayananJasa(documentId: String, layananjasa: LayananJasa) {
        val db = FirebaseFirestore.getInstance()
        val layananJasaRef = db.collection("LayananJasa").document(documentId)

        binding.progressBar.visibility = View.VISIBLE

        layananJasaRef.delete()
            .addOnSuccessListener {
                layananjasa.photoUrl?.let { url ->
                    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                    imageRef.delete()
                        .addOnSuccessListener {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Layanan jasa berhasil dihapus", Toast.LENGTH_SHORT).show()
                            val intent =
                                Intent(this, MenuJasaActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Gagal menghapus gambar: ${it.message}", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                } ?: run {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Layanan jasa berhasil dihapus", Toast.LENGTH_SHORT).show()
                    val intent =
                        Intent(this, MenuJasaActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal menghapus layanan jasa: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}