package com.project.help

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import cn.pedant.SweetAlert.SweetAlertDialog
import dmax.dialog.SpotsDialog
import java.util.*
import java.util.regex.Pattern

class Utilities {
    object Validation {
        @JvmStatic
        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validatePassword(password: String): Boolean {
            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            val pattern = Pattern.compile(passwordPattern);
            val matcher = pattern.matcher(password);

            return matcher.matches();
        }
    }

    object Alert {
        fun alertDialog(text: String, errorType: Int, page: Context) {
            SweetAlertDialog(page, errorType)
                    .setTitleText(text)
                    .show()
        }

        fun loadingDialog(page: Context, message: String): Dialog {

            return SpotsDialog.Builder()
                .setContext(page)
                .setMessage(message)
                .setCancelable(false).build()
        }
    }

    object Converter {
        fun getDateTimeFromFirebase(timeStamp: Long): Date? {
            return Date(timeStamp)
        }
    }
}