package com.example.domesticviolencealert

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_contacts.view.*
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.TextUtils
import com.google.firebase.firestore.*

private const val ARG_CONTACTS = "suspect"

class ContactsFragment : Fragment() {

    private var allSuspects = ArrayList<Suspect>()
    private var contactSuspects = ArrayList<Suspect>()

    companion object {
        @JvmStatic
        fun newInstance(allSuspects: ArrayList<Suspect>) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_CONTACTS, allSuspects)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            allSuspects = it.getParcelableArrayList(ARG_CONTACTS)
            Log.d(Constants.TAG, "Enter contacts frag with allSuspects ${allSuspects.size}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        val builder = getContacts()
        view.list_contacts.setText(builder, TextView.BufferType.SPANNABLE)

        view.contact_suspects_button.setOnClickListener {
            Utils.switchFragment(context!!, SuspectListFragment.newInstance(contactSuspects))
        }

        view.back_button.setOnClickListener {
            Utils.switchFragment(context!!, SearchFragment())
        }

        return view
    }

    private fun getContacts(): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val resolver = context!!.contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                var email = ""
                val emailsCursor = resolver.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + id, null, null)
                while (emailsCursor.moveToNext()) {
                    Log.d(Constants.TAG, "Found email")
                    email = emailsCursor.getString(emailsCursor.getColumnIndex(Email.DATA))
                    break
                }
                emailsCursor.close()


                var phone = ""
                if (phoneNumber > 0) {
                    val cursorPhone = context!!.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )

                            phone = phoneNumValue.removePrefix("1").removePrefix(" ")
                        }
                    }
                    cursorPhone.close()
                }

                val nameText = "Name: $name\n"
                val nameSpan = SpannableStringBuilder(nameText)
                val checkName = checkName(name, nameText, nameSpan)

                val phoneText = "Phone Number: $phone\n"
                val phoneSpan = SpannableStringBuilder(phoneText)
                val checkPhone = checkPhone(phone, phoneText, phoneSpan)

                val emailText = "Email: $email\n\n"
                val emailSpan = SpannableStringBuilder(emailText)
                val checkEmail = checkEmail(email, emailText, emailSpan)

                if (checkName || checkPhone || checkEmail){
                    val contactSpan = TextUtils.concat(nameSpan, phoneSpan, emailSpan)
                    builder.append(contactSpan)
                } else {
                    builder.append("")
                }


            }
        } else {
            Toast.makeText(context!!, "No contact found", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        return builder
    }

    private fun checkName(name: String, nameText: String, nameSpan: SpannableStringBuilder) : Boolean{
        for (s in allSuspects) {
            if (s.name.contains(name) && name.isNotEmpty()) {
                nameSpan.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.helpRed)),
                    0,
                    nameText.length,
                    0
                )

                if (!contactSuspects.contains(s)) {
                    Log.d(Constants.TAG, "adding ${s.name} to list")
                    contactSuspects.add(s)
                }
                return true
            }
        }
        return false
    }

    private fun checkEmail(email: String, emailText: String, emailSpan: SpannableStringBuilder): Boolean{
        for (s in allSuspects) {
            if (s.email == email && s.email.isNotEmpty()) {
                emailSpan.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.helpRed)),
                    0,
                    emailText.length,
                    0
                )

                if (!contactSuspects.contains(s)) {
                    Log.d(Constants.TAG, "adding ${s.email} to list")
                    contactSuspects.add(s)
                }
                return true
            }
        }
        return false
    }

    private fun checkPhone(phone: String, phoneText: String, phoneSpan: SpannableStringBuilder) : Boolean{
        for (s in allSuspects) {
            if (s.phone == phone && s.phone.isNotEmpty()) {
                phoneSpan.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.helpRed)),
                    0,
                    phoneText.length,
                    0
                )

                if (!contactSuspects.contains(s)) {
                    Log.d(Constants.TAG, "adding ${s.phone} to list")
                    contactSuspects.add(s)
                }
                return true
            }
        }
        return false
    }



}

