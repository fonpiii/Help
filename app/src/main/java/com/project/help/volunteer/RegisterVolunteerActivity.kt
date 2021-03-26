package com.project.help.volunteer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.project.help.R
import com.project.help.WelcomeActivity

class RegisterVolunteerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var txtToolbar: TextView
    private lateinit var iconLeft: ImageView
    private lateinit var submit: Button
    private lateinit var firstName: EditText
    private lateinit var tel: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var rePassword: EditText
    private lateinit var smallFirstName: TextView
    private lateinit var smallLastName: TextView
    private lateinit var smallTel: TextView
    private lateinit var smallEmail: TextView
    private lateinit var smallRePassword: TextView
    private lateinit var smallPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_volunteer)

        txtToolbar = findViewById(R.id.txtToolbar)
        submit = findViewById(R.id.submit)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        tel = findViewById(R.id.tel)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        rePassword = findViewById(R.id.re_password)
        smallFirstName = findViewById(R.id.smallFirstName)
        smallLastName = findViewById(R.id.smallLastName)
        smallTel = findViewById(R.id.smallTel)
        smallEmail = findViewById(R.id.smallEmail)
        smallPassword = findViewById(R.id.smallPassword)
        smallRePassword = findViewById(R.id.smallRePassword)

        submit.setOnClickListener(this)

        //region On init
        setToolbar()
        //endregion On init
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
        txtToolbar.text = getString(R.string.register_volunteer)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.submit -> {
                onClickBtnSubmit()
            }
            R.id.iconLeft -> finish()
        }
    }

    private fun onClickBtnSubmit() {
        clearSmallError()
        if (checkInput()) {
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("ระบบได้ทำการยืนยันการลงทะเบียนแล้วโปรดเข้าสู่ระบบเพื่อใช้งาน")
                    .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener {
                        val intent = Intent(this, WelcomeActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    })
                    .show()
        }

    }

    private fun clearSmallError() {
        smallFirstName.visibility = View.GONE
        smallLastName.visibility = View.GONE
        smallTel.visibility = View.GONE
        smallEmail.visibility = View.GONE
        smallPassword.visibility = View.GONE
        smallRePassword.visibility = View.GONE
    }

    private fun checkInput(): Boolean {
        var result = true

        if (firstName.text.isNullOrEmpty()) {
            smallFirstName.visibility = View.VISIBLE
            result = false
        }

        if (lastName.text.isNullOrEmpty()) {
            smallLastName.visibility = View.VISIBLE
            result = false
        }

        if (tel.text.isNullOrEmpty()) {
            smallTel.visibility = View.VISIBLE
            result = false
        }

        if (email.text.isNullOrEmpty()) {
            smallEmail.visibility = View.VISIBLE
            result = false
        }

        if (password.text.isNullOrEmpty()) {
            smallPassword.visibility = View.VISIBLE
            result = false
        }

        if (rePassword.text.isNullOrEmpty()) {
            smallRePassword.text = "กรุณากรอกยืนยันรหัสผ่าน"
            smallRePassword.visibility = View.VISIBLE
            result = false
        }

        if (!password.text.isNullOrEmpty() && rePassword.text.isNullOrEmpty()) {
            smallRePassword.text = "กรุณากรอกยืนยันรหัสผ่าน"
            smallRePassword.visibility = View.VISIBLE
            result = false
        }

        if (!password.text.isNullOrEmpty() && !rePassword.text.isNullOrEmpty()) {
            if (password.text.trim().toString() != rePassword.text.trim().toString()) {
                smallRePassword.text = "ยืนยันรหัสผ่านไม่ถูกต้อง"
                smallRePassword.visibility = View.VISIBLE
                result = false
            }
        }
        return result
    }
}