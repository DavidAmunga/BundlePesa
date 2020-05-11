package com.pesa.bundle.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ivbaranov.mli.MaterialLetterIcon
import com.pesa.bundle.R
import com.pesa.bundle.model.Contact


class ContactsAdapter(data: ArrayList<Contact>?, context: Context, mListener: ContactListener) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {


    private val data: ArrayList<Contact>? = data
    private val context: Context = context
    private val listener: ContactListener = mListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflator: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflator.inflate(R.layout.contact_item_layout, parent, false)

        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact = data!!.get(position)
        holder.txtName.text = contact.name
        holder.txtPhoneNo.text = contact.phoneNo


        holder.nameIcon.letter = contact.name.first().toString()
        holder.nameIcon.shapeColor = context.getResources().getColor(R.color.colorPrimary)
        holder.nameIcon.shapeType = MaterialLetterIcon.Shape.ROUND_RECT
        holder.nameIcon.roundRectRx = 8.0f
        holder.nameIcon.roundRectRy = 8.0f

        holder.itemView.setOnClickListener {
            listener.onContactSelect(contact)
        }


    }


    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var nameIcon: MaterialLetterIcon
        internal var txtName: TextView
        internal var txtPhoneNo: TextView

        init {
            nameIcon = itemView.findViewById(R.id.name_icon)
            txtName = itemView.findViewById(R.id.txt_name)
            txtPhoneNo = itemView.findViewById(R.id.txt_phone_no)
        }
    }

    interface ContactListener {
        fun onContactSelect(contact: Contact)
    }
}