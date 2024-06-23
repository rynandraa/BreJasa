package com.ryan.obre_mitra.ui.history

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.databinding.ActivityHistoryDetailBinding
import com.ryan.obre_mitra.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private var idPesanan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        // Mengambil data RiwayatPemesanan dari intent
        val riwayatPemesanan = intent.getParcelableExtra<RiwayatPemesanan>("Data")
        idPesanan = intent.getStringExtra("IdPesanan")

        // Log untuk memeriksa ID Pesanan yang diterima
        Log.d("HistoryDetailActivity", "ID Pesanan: $idPesanan")

        // Mengisi TextView dengan data RiwayatPemesanan
        binding.textViewNamaLayanan.text = riwayatPemesanan?.namaLayanan ?: ""
        binding.textViewNamaPelanggan.text = riwayatPemesanan?.namaPelanggan ?: ""
        binding.textViewNomorPelanggan.text = riwayatPemesanan?.nomorPelanggan ?: ""
        binding.textViewAlamatPelanggan.text = riwayatPemesanan?.alamatPelangganMaps ?: ""
        binding.textViewDetailAlamat.text = riwayatPemesanan?.detailAlamatPelanggan ?: ""
        binding.textViewJenisPesanan.text = riwayatPemesanan?.jenisPesanan ?: ""
        binding.textViewKategoriLayanan.text = riwayatPemesanan?.kategoriLayanan ?: ""
        binding.textViewMetodeBayar.text = riwayatPemesanan?.metodeBayar ?: ""
        binding.textViewIdPesanan.text = idPesanan ?: ""  // Pastikan TextView ini diisi dengan idPesanan
        binding.textStatusPesanan.text = riwayatPemesanan?.statusPesanan ?: ""

        // Mengubah format tanggal pesanan dengan SimpleDateFormat
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val formattedDate = riwayatPemesanan?.tanggalPesanan?.let { dateFormat.format(it.toDate()) }
        binding.textViewTanggalPesanan.text = formattedDate ?: ""

        val formattedHarga = "Rp ${riwayatPemesanan?.totalBiaya.toString()}"
        binding.textViewTotalBiaya.text = formattedHarga

        // Inisialisasi tombol
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Periksa status pesanan dan atur visibilitas tombol
        idPesanan?.let { id ->
            db.collection("RiwayatPesanan").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val statusPesanan = document.getString("statusPesanan")
                        if (statusPesanan == "Diproses") {
                            binding.btnPesananSelesai.visibility = View.VISIBLE
                            binding.btnPesananBatal.visibility = View.VISIBLE
                        } else {
//                            binding.btnPesananSelesai.visibility = View.GONE
//                            binding.btnPesananBatal.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengambil status pesanan", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnPesananSelesai.setOnClickListener {
            showConfirmationDialog("Selesai") {
                idPesanan?.let { id ->
                    updatePesananSelesai(id)
                } ?: Toast.makeText(this, "ID pesanan tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPesananBatal.setOnClickListener {
            showConfirmationDialog("Dibatalkan") {
                idPesanan?.let { id ->
                    updatePesananBatal(id)
                } ?: Toast.makeText(this, "ID pesanan tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOpenMaps.setOnClickListener {
            openMaps(riwayatPemesanan?.idPesanan)
        }

        binding.btnHubungiPelanggan.setOnClickListener {
            hubungiPelanggan(riwayatPemesanan?.nomorPelanggan)
        }
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin menandai pesanan ini sebagai $action?")
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

    private fun updatePesananSelesai(id: String) {
        db.collection("RiwayatPesanan").document(id)
            .update("statusPesanan", "Selesai")
            .addOnSuccessListener {
                Toast.makeText(this, "Pesanan telah diselesaikan", Toast.LENGTH_SHORT).show()
                // Menghapus antrian dari LayananJasa
                hapusAntrian(id)
                // Mengarahkan ke HistoryFragment
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("FragmentName", "HistoryFragment")
                startActivity(intent)
                finish() // Menutup activity saat ini
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengupdate status pesanan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePesananBatal(id: String) {
        db.collection("RiwayatPesanan").document(id)
            .update("statusPesanan", "Dibatalkan")
            .addOnSuccessListener {
                Toast.makeText(this, "Pesanan telah dibatalkan", Toast.LENGTH_SHORT).show()
                // Menghapus antrian dari LayananJasa
                hapusAntrian(id)
                // Mengarahkan ke HistoryFragment
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("FragmentName", "HistoryFragment")
                startActivity(intent)
                finish() // Menutup activity saat ini
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengupdate status pesanan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hapusAntrian(idPesanan: String) {
        db.collection("RiwayatPesanan").document(idPesanan)
            .get()
            .addOnSuccessListener { riwayatPesananDocument ->
                val nomorPesanan = riwayatPesananDocument.getString("nomorPesanan")
                if (nomorPesanan != null) {
                    db.collection("LayananJasa").get()
                        .addOnSuccessListener { layananJasaSnapshot ->
                            for (layananJasaDocument in layananJasaSnapshot.documents) {
                                val antrian = layananJasaDocument["antrian"] as? MutableList<Map<String, Any>> ?: mutableListOf()
                                val updatedAntrian = antrian.filterNot { item ->
                                    item["nomorPesanan"] == nomorPesanan
                                }
                                db.collection("LayananJasa").document(layananJasaDocument.id)
                                    .update("antrian", updatedAntrian)
                                    .addOnSuccessListener {
                                        // Pembaruan berhasil
                                        Log.d("Firestore", "Antrian updated successfully.")
                                    }
                                    .addOnFailureListener { e ->
                                        // Gagal memperbarui
                                        Log.w("Firestore", "Error updating antrian", e)
                                    }
                            }
                        }.addOnFailureListener { e ->
                            // Gagal mengambil data dari LayananJasa
                            Log.w("Firestore", "Error getting documents from LayananJasa", e)
                        }
                }
            }.addOnFailureListener { e ->
                // Gagal mengambil data dari RiwayatPesanan
                Log.w("Firestore", "Error getting documents from RiwayatPesanan", e)
            }
    }

    private fun openMaps(id: String?) {
        if (id != null) {
            db.collection("RiwayatPesanan").document(id)
                .get()
                .addOnSuccessListener { document ->
                    val latitude = document.getDouble("latitudePelanggan")
                    val longitude = document.getDouble("longitudePelanggan")
                    if (latitude != null && longitude != null) {
                        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Google Maps tidak terinstal.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Data lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengambil data lokasi", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "ID pesanan tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hubungiPelanggan(phoneNumber: String?) {
        if (phoneNumber != null) {
            // Menambahkan kode negara Indonesia jika belum ada
            val fullPhoneNumber = if (phoneNumber.startsWith("+62")) {
                phoneNumber
            } else {
                "+62$phoneNumber"
            }
            val url = "https://api.whatsapp.com/send?phone=$fullPhoneNumber"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp tidak terinstal.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Nomor telepon tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}
