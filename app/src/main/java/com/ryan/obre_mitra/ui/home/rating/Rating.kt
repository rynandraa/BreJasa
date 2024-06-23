package com.ryan.obre_mitra.ui.home.rating

import com.google.firebase.Timestamp

data class Rating(
    val comment: String? = null,
    val namaPelanggan: String? = null,
    val rating: Double? = null,
    val tanggalPesan: Timestamp? = null
)
