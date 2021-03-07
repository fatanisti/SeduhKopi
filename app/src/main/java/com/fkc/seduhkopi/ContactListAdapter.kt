package com.fkc.seduhkopi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fkc.seduhkopi.databinding.ActivityContactsRowBinding

class ContactListAdapter(
    private var dataList: List<Contact>
) : RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {

    var listener: RecyclerViewClickListener? = null

    inner class ContactViewHolder(
        val itemContactsRowBinding: ActivityContactsRowBinding
    ) : RecyclerView.ViewHolder(itemContactsRowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.activity_contacts_row, parent, false)
    )

    override fun onBindViewHolder(holder: ContactViewHolder, i: Int) {
        val contact = dataList[i]

        holder.itemContactsRowBinding.namaKlinik.text = contact.namaKlinik
        holder.itemContactsRowBinding.alamat.text = contact.alamat
        holder.itemContactsRowBinding.telepon.text = contact.telepon

        holder.itemContactsRowBinding.listKontak.setOnClickListener {
            listener?.onItemClicked(it, dataList[i])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}