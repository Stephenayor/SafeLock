package com.example.safelock.utils

import android.content.Context
import android.widget.Toast

class Tools {
    companion object{
        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}