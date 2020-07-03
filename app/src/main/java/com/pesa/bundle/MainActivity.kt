package com.pesa.bundle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.api.HoverParameters
import com.hover.sdk.permissions.PermissionActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.pesa.bundle.adapter.ContactsAdapter
import com.pesa.bundle.model.Contact
import com.pesa.bundle.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), Hover.DownloadListener, Hover.ActionChoiceListener,
    PermissionListener, DialerFragment.OnPhoneNumberEnterListener,
    SelectContactsFragment.OnContactSelectedListener {

    private val PERMISSIONS_HOVER_REQUEST: Int = 31
    private var selectedBundleOption: String? = null
    private var otherPhoneNo: String? = null
    private var contactOption: String? = null
    private var bundleOption: Int? = null

    private val BUNDLE_OPTIONS = arrayListOf("Data Bundle", "Voice Bundle")
    private val CONTACT_OPTIONS =
        arrayListOf(
            Constants.CONTACT_OPTION_MY_NUMBER,
            Constants.CONTACT_OPTION_DIALER
        )

    private val PERMISSIONS = listOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE
    )

    private lateinit var bundleAdapter: ArrayAdapter<String>
    private lateinit var contactAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Init Timber
        Timber.tag("MainActivity")

//        Init Bundle Options
        bundle_option.inputType = InputType.TYPE_NULL
        bundleAdapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            BUNDLE_OPTIONS
        )
        Timber.d("Start BundlePesa")
        bundle_option.setAdapter(bundleAdapter)

        bundle_option.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                parent.getItemAtPosition(position).toString().let { it ->
                    selectedBundleOption = it
                    Timber.d("Selected Contact Option is $selectedBundleOption")

                    bundleOption = position + 1;

                }

            }

        //        Init Contact Options
        contact_option.inputType = InputType.TYPE_NULL
        contactAdapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            CONTACT_OPTIONS
        )
        contact_option.setAdapter(contactAdapter)
        contact_option.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                parent.getItemAtPosition(position).toString().let { selectedContactOption ->
                    Timber.d("Selected Contact Option is $selectedContactOption")

                    verifyContactOption(option = selectedContactOption);
                }
            }

//        Initialize Hover
        Hover.initialize(applicationContext, this)
        Hover.setBranding("BundlePesa", R.mipmap.ic_launcher, this)
//        Initiate Hover Permissions Activity
        val intent = Intent(this, PermissionActivity::class.java)
        startActivityForResult(intent, 123)


        handleNextButton(Constants.STATE_DISABLED)

        til_amount.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.toString().length > 0) {
                    handleNextButton(Constants.STATE_ENABLED)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        btn_next.setOnClickListener({
            performAction()

        })

    }

    private fun verifyContactOption(option: String) {
        if (option.equals(Constants.CONTACT_OPTION_MY_NUMBER)) {
            contactOption = Constants.CONTACT_OPTION_MY_NUMBER

            otherPhoneNo?.let {
//                Disable Other PhoneNo
                txt_other_phone_no.visibility = View.GONE
            }
        } else if (option.equals(Constants.CONTACT_OPTION_DIALER)) {
            contactOption = Constants.CONTACT_OPTION_DIALER
//            Show Dialer
            val sheet = DialerFragment()
            sheet.show(supportFragmentManager, "Dialer Sheet")
        } else if (option.equals(Constants.CONTACT_OPTION_SELECT_CONTACTS)) {
            contactOption = Constants.CONTACT_OPTION_SELECT_CONTACTS

//            Init Permissions
            Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(this)
                .check()

        }
    }

    private fun performAction() {
//

        val action: String =
            if (otherPhoneNo != null) Constants.ACTION_OTHER_NUMBER else Constants.ACTION_MY_NUMBER

        Constants.ACTION_OTHER_NUMBER
        val intent = HoverParameters.Builder(this@MainActivity)
            .request(action)
            .style(R.style.hoverStyle)
            .setHeader("Buying Bundle")
            .initialProcessingMessage(resources.getString(R.string.initialProcessingMessage))
            .showUserStepDescriptions(true);

        if (bundleOption != null) intent.extra("bundleOption", bundleOption.toString())
        if (otherPhoneNo != null) intent.extra(
            "otherNumber",
            otherPhoneNo
        )

        intent.extra(
            "amount",
            txt_amount.getText().toString().trim()
        )

        startActivityForResult(intent.buildIntent(), PERMISSIONS_HOVER_REQUEST)
    }


    fun handleNextButton(state: String) {
        when (state) {
            Constants.STATE_ENABLED -> {
                btn_next.setEnabled(true)
//                ContextCompat sorts for >M
                btn_next.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            Constants.STATE_DISABLED -> {
                btn_next.setEnabled(false)
                btn_next.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.darker_gray
                    )
                )
            }

        }
    }


    override fun onSuccess(actions: ArrayList<HoverAction>?) {
        Timber.d("Successfully downloaded %d actions", actions?.size ?: 0)
    }

//		Toast.makeText(this, "Successfully downloaded " + actions.size() + " actions", Toast.LENGTH_LONG).show();


    override fun onError(message: String?) {
        Timber.d("Error: %s", message!!)
    }

    override fun onActionChosen(p0: String?) {
        Timber.d("Action Chosen " + p0!!)

    }

    override fun onCanceled() {
        TODO("Not yet implemented")
    }


    override fun onPhoneNumberEnterListener(phoneNo: String) {
        Timber.d("Phone No is $phoneNo")

        if (phoneNo.length < 10) {
            return
        }
        phoneNo.let {
            otherPhoneNo = it
        }
        otherPhoneNo?.let {
            txt_other_phone_no.text = it
            txt_other_phone_no.visibility = View.VISIBLE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Timber.d("Request CODE $requestCode ")
        Timber.d("Result CODE $resultCode ")


        if (requestCode === PERMISSIONS_HOVER_REQUEST && resultCode === Activity.RESULT_OK) {


            val data = data!!.getStringArrayExtra("session_messages")
            Timber.d("Session Messages : $data")


//        Initialize Hover
            Hover.initialize(applicationContext, this)


//            if (data.size > 0) {
            Toast.makeText(this, "Completed!", Toast.LENGTH_SHORT).show()

//            Snackbar.make(root_layout, "Completed!", Snackbar.LENGTH_SHORT).show()
//            }


            txt_amount.text?.clear();


        } else if (requestCode === 0 && resultCode === Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Error: " + data!!.getStringExtra("error"), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        Timber.d("Granted Permissions $response")


//          Select Contacts
        val sheet = SelectContactsFragment()
        sheet.show(supportFragmentManager, "Select Contacts")
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        token?.continuePermissionRequest();
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
    }

    override fun onContactSelectedListener(contact: Contact) {
        Timber.d("Contact Selected $contact")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.nav_change_lang -> {
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


}
