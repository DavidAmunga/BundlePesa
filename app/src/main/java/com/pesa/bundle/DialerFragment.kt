package com.pesa.bundle

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.pesa.bundle.util.Constants
import com.pesa.bundle.util.changeState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dialer.*
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class DialerFragment : SuperBottomSheetFragment() {

    private var mListener: OnPhoneNumberEnterListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dialer, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Setup Timber
        Timber.tag("Dialer Fragment")


        btnOne.setOnClickListener { writeNo(it) }
        btnTwo.setOnClickListener { writeNo(it) }
        btnThree.setOnClickListener { writeNo(it) }
        btnFour.setOnClickListener { writeNo(it) }
        btnFive.setOnClickListener { writeNo(it) }
        btnSix.setOnClickListener { writeNo(it) }
        btnSeven.setOnClickListener { writeNo(it) }
        btnEight.setOnClickListener { writeNo(it) }
        btnNine.setOnClickListener { writeNo(it) }
        btnZero.setOnClickListener { writeNo(it) }
        btnClear.setOnClickListener { removeNo(it) }
        fabClose.setOnClickListener { closeFragment() }

        btn_set_number.changeState(Constants.STATE_DISABLED)


        txt_phone_no.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.toString().length > 0 && s!!.toString()
                        .startsWith("07") && s!!.length == 10
                ) {
                    btn_set_number.changeState(Constants.STATE_ENABLED)
                }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btn_set_number.setOnClickListener {
            mListener?.let {

                it.onPhoneNumberEnterListener(txt_phone_no.text.toString())
                this.dismiss()
            }
        }


    }

    private fun writeNo(view: View) {
        Timber.d("CURRENT_TEXT :--->  ${view.tag.toString()}")

        txt_phone_no.text = txt_phone_no.text.toString() + view.tag.toString()

    }

    private fun removeNo(view: View) {
        Timber.d("CURRENT_TEXT :--->  ${view.tag.toString()}")

        txt_phone_no.text = txt_phone_no.text.toString().dropLast(1)

    }

    private fun closeFragment() {
        mListener?.let {
            it.onPhoneNumberEnterListener(txt_phone_no.text.toString())
            this.dismiss()

        }
    }


    override fun isSheetAlwaysExpanded(): Boolean {
        return true
    }

    override fun isSheetCancelable(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPhoneNumberEnterListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnPhoneNumberEnterListener")
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
    interface OnPhoneNumberEnterListener {
        fun onPhoneNumberEnterListener(phoneNo: String)
    }


}
