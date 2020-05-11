package com.pesa.bundle

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pesa.bundle.adapter.ContactsAdapter
import com.pesa.bundle.model.Contact
import kotlinx.android.synthetic.main.fragment_select_contacts.*

/**
 * A simple [Fragment] subclass.
 */
class SelectContactsFragment : DialogFragment(), ContactsAdapter.ContactListener {

    private var mListener: OnContactSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactList.layoutManager = LinearLayoutManager(activity)

        var mycontacts: ArrayList<Contact>? = getAllContacts()



        contactList.adapter = ContactsAdapter(mycontacts, view.context, this)

    }

    private fun getAllContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>()
        val cr: ContentResolver = activity!!.contentResolver
        val cursor: Cursor? =
            cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if ((if (cursor != null) cursor.count else 0) > 0) {
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pcur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ",
                        arrayOf(id), null
                    )

                    while (pcur!!.moveToNext()) {
                        val phoneNo =
                            pcur.getString(pcur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        var contact = Contact(name, phoneNo)
                        contacts.add(contact)
                    }

                    pcur.close()
                }
            }
        }
        if (cursor != null) cursor.close()

        return contacts
    }


    fun isSheetAlwaysExpanded(): Boolean {
        return true
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactSelectedListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnContactSelectedListener")
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * Here we define the methods that we can fire off
     * in our parent Activity once something has changed
     * within the fragment.
     */
    interface OnContactSelectedListener {
        fun onContactSelectedListener(contact: Contact)
    }

    override fun onContactSelect(contact: Contact) {
        mListener?.onContactSelectedListener(contact)
    }

}
