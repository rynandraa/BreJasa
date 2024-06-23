package com.ryan.obre_mitra.ui.history

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp


data class RiwayatPemesanan(
    val idPesanan: String? = null,
    val alamatPelangganMaps: String? = null,
    val detailAlamatPelanggan: String? = null,
    val idLayanan: String? = null,
    val idOwner: String? = null,
    val idPelanggan: String? = null,
    val jenisPesanan: String? = null,
    val jumlahPesanan: Int? = null,
    val kategoriLayanan: String? = null,
    val latitudePelanggan: Double? = null,
    val longitudePelanggan: Double? = null,
    val metodeBayar: String? = null,
    val namaLayanan: String? = null,
    val namaPelanggan: String? = null,
    val nomorAntrian: Int? = null,
    val nomorPelanggan: String? = null,
    val statusPesanan: String? = null,
    val tanggalPesanan: Timestamp? = null,
    val totalBiaya: Int? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idPesanan)
        parcel.writeString(alamatPelangganMaps)
        parcel.writeString(detailAlamatPelanggan)
        parcel.writeString(idLayanan)
        parcel.writeString(idOwner)
        parcel.writeString(idPelanggan)
        parcel.writeString(jenisPesanan)
        parcel.writeValue(jumlahPesanan)
        parcel.writeString(kategoriLayanan)
        parcel.writeValue(latitudePelanggan)
        parcel.writeValue(longitudePelanggan)
        parcel.writeString(metodeBayar)
        parcel.writeString(namaLayanan)
        parcel.writeString(namaPelanggan)
        parcel.writeValue(nomorAntrian)
        parcel.writeString(nomorPelanggan)
        parcel.writeString(statusPesanan)
        parcel.writeParcelable(tanggalPesanan, flags)
        parcel.writeValue(totalBiaya)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RiwayatPemesanan> {
        override fun createFromParcel(parcel: Parcel): RiwayatPemesanan {
            return RiwayatPemesanan(parcel)
        }

        override fun newArray(size: Int): Array<RiwayatPemesanan?> {
            return arrayOfNulls(size)
        }
    }
}