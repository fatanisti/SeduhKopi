package com.fkc.seduhkopi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ContactList {
    @SerializedName("contact")
    @Expose
    var contact: List<Contact> = ArrayList()
}