package com.example.contactsapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.R
import com.example.contactsapp.model.Contact

/**
 * [RecyclerView.Adapter] for adapting instances of [Contact]
 * to a row item.
 */
class ContactsAdapter(
    private var contacts: List<Contact> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun updateContacts(contacts: List<Contact>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        ) {}

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.name).text = context.getString(R.string.name, contact.name)
            findViewById<TextView>(R.id.email).text =
                context.getString(R.string.email, contact.email)
            findViewById<TextView>(R.id.phone_number).text =
                context.getString(R.string.phone_number, contact.phone)
        }
    }
}