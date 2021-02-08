package com.pesa.bundle.util

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser
import io.paperdb.Paper
import java.util.ArrayList

/**
 * Created by miles on 2/2/16.
 */
class Preferences(private val _context: Context) {
    private val PREFS_NAME: String = "BundlePesa"
    var settings: SharedPreferences

    fun setUser(user: FirebaseUser) {
        Paper.book().write("user", user)
    }

    fun getUser(): FirebaseUser? {
        return Paper.book().read("user")
    }


    init {
        settings = _context.getSharedPreferences(PREFS_NAME, 0)
        Paper.init(_context)
    }
}
