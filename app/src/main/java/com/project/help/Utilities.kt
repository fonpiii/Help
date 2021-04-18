package com.project.help

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.ServerValue
import dmax.dialog.SpotsDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

        fun convertTimeToPostDetails(timeStamp: Long): String {
            var result = ""
            val formatterFull = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val formatterTime = SimpleDateFormat("HH:mm:ss")
            val secondApiFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            val currentDate = LocalDate.parse(formatterFull.format(Date()) , secondApiFormat)
            var dayCurrent = currentDate.dayOfMonth


            val postTimeStamp = LocalDate.parse(formatterFull.format(Date(timeStamp)) , secondApiFormat)
            var dayPost = postTimeStamp.dayOfMonth

            if (currentDate.year == postTimeStamp.year && currentDate.month == postTimeStamp.month) {
                result = when {
                    (dayCurrent - dayPost) == 0 -> {
                        "วันนี้ " + formatterTime.format(Date(timeStamp).time)
                    }
                    (dayCurrent - dayPost) == 1 -> {
                        "เมื่อวานนี้ " + formatterTime.format(Date(timeStamp).time)
                    }
                    else -> {
                        formatterFull.format(Date(timeStamp).time)
                    }
                }
            }
            return result
        }
    }

    object Other {
        fun callTelephone(tel: String): Intent {
            return Intent(Intent.ACTION_CALL, Uri.parse("tel:$tel"))
        }
    }
}