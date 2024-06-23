package com.ryan.obre_mitra.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ZoomControls
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityMapsBinding
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var savedLocation: LatLng? = null
    private var secondAlamat: String? = null
    private lateinit var zoomControls: ZoomControls
    private var isLocationDetected: Boolean = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                getMyLastLocation()
            } else {
                Toast.makeText(
                    this@MapsActivity,
                    "Izin lokasi dibutuhkan untuk menampilkan lokasi pengguna",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dapatkan SupportMapFragment dan dapatkan pemberitahuan ketika peta siap untuk digunakan.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        zoomControls = findViewById(R.id.zoom_controls)

        // Setelah pengguna memilih lokasi dan sebelum menutup aktivitas
        binding.btnSimpanLokasi.setOnClickListener {
            if (isLocationDetected || savedLocation != null) {
                val intent = Intent()
                intent.putExtra("latitude", savedLocation?.latitude)
                intent.putExtra("longitude", savedLocation?.longitude)
                intent.putExtra("alamat", secondAlamat)
                Log.d("Alamat : ", secondAlamat.toString())
                setResult(RESULT_OK, intent)
                Log.d("Debug", "Batas: ${savedLocation?.latitude}")
                finish()
            } else {
                Toast.makeText(this@MapsActivity, "Tidak ada data lokasi yang tersedia", Toast.LENGTH_SHORT).show()
            }
        }
        // Untuk mendapatkan lokasi user
        getMyLastLocation()
    }

    private fun setupZoomControls() {
        zoomControls.setOnZoomInClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }
        zoomControls.setOnZoomOutClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupZoomControls()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            getMyLastLocation()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        mMap.setOnMapClickListener { latLng ->
            mMap.clear() // Menghapus semua marker sebelumnya
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Lokasi Dipilih")
                    .draggable(true)
            )
            savedLocation = latLng // Menyimpan lokasi yang dipilih
            getAddressFromLocation(latLng) // Mendapatkan alamat dari lokasi yang dipilih
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        if (::mMap.isInitialized) { // Periksa apakah mMap sudah diinisialisasi
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title("Lokasi Saya")
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            isLocationDetected = true
                            savedLocation = latLng
                        } else {
                            // mMap belum diinisialisasi, lakukan penanganan khusus di sini jika diperlukan
                        }
                    } else {
                        Toast.makeText(
                            this@MapsActivity,
                            "Lokasi tidak ditemukan. Coba lagi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getAddressFromLocation(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val kecamatan = address.locality
                val kabupaten = address.subAdminArea
                val provinsi = address.adminArea
                secondAlamat = "$kecamatan, $kabupaten, $provinsi"
                Log.d("Alamat", "$kecamatan, $kabupaten, $provinsi")
            } else {
                Log.d("Alamat", "Tidak ada alamat ditemukan")
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error getting address", e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLastLocation()
            } else {
                Toast.makeText(
                    this@MapsActivity,
                    "Izin lokasi dibutuhkan untuk menampilkan lokasi pengguna",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}