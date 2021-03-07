package com.fkc.seduhkopi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Contact {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("nama_klinik")
    @Expose
    var namaKlinik: String? = null

    @SerializedName("alamat")
    @Expose
    var alamat: String? = null

    @SerializedName("telepon")
    @Expose
    var telepon: String? = null

    @SerializedName("plus_kode")
    @Expose
    var plus_kode: String? = null
}