package com.example.android.temptext.database

import android.content.Context
import android.content.SharedPreferences
import java.util.*
private const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"
class UserID {
    private var uniqueID: String? = null

    @Synchronized
    fun id(context: Context): String? {

        if (uniqueID == null) {
            val sharedPrefs: SharedPreferences = context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE
            )
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)
            //creates new userId if one isn't created
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString()

            }
        }
        return uniqueID
    }
}