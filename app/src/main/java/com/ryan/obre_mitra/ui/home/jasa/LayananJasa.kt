package com.ryan.obre_mitra.ui.home.jasa

import android.os.Parcel
import android.os.Parcelable

data class LayananJasa(
    val documentId: String? = null,
    val idOwner: String? = null,
    var namaLayanan: String ?= null,
    var pemilik: String ?= null,
    var kategori: String ?= null,
    var biayaJasa: Int ?= null,
    var deskripsi: String ?= null,
    var batasPelayananSehari: Int? = null,
    var jumlahPelayananSaatIni: Int? = null,
    var phoneNumberService: String? = null,
    var photoUrl: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(idOwner)
        parcel.writeString(namaLayanan)
        parcel.writeString(pemilik)
        parcel.writeString(kategori)
        parcel.writeValue(biayaJasa)
        parcel.writeString(deskripsi)
        parcel.writeValue(batasPelayananSehari)
        parcel.writeValue(jumlahPelayananSaatIni)
        parcel.writeString(phoneNumberService)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LayananJasa> {
        override fun createFromParcel(parcel: Parcel): LayananJasa {
            return LayananJasa(parcel)
        }

        override fun newArray(size: Int): Array<LayananJasa?> {
            return arrayOfNulls(size)
        }
    }
}