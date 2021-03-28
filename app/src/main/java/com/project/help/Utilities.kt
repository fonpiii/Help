package com.project.help

import android.text.TextUtils
import android.util.Patterns
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
}