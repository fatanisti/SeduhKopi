package com.fkc.seduhkopi

import android.view.View

interface RecyclerViewClickListener {
    fun onItemClicked(view: View, contact: Contact)
}