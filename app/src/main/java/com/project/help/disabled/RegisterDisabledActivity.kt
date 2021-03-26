package com.project.help.disabled

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.WelcomeActivity
import com.project.help.disabled.model.RegisterModel
import org.jetbrains.anko.toast

class RegisterDisabledActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var txtToolbar: TextView
    private lateinit var iconLeft: ImageView
    private lateinit var submit: Button
    private lateinit var firstName: EditText
    private lateinit var tel: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var rePassword: EditText
    var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_disabled)

        txtToolbar = findViewById(R.id.txtToolbar)
        submit = findViewById(R.id.submit)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        tel = findViewById(R.id.tel)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        rePassword = findViewById(R.id.re_password)

        submit.setOnClickListener(this)

        //region On init
        setToolbar()
        checkSessionLogin()
        setFirebaseDatabase()
        //endregion On init
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("User")
    }

    private fun checkSessionLogin() {
        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, DisabledMainActivity::class.java))
            finish()
        }
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
        if (checkInput()) {
            mAuth!!.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length() < 6) {
                        password.error = "Please check you password. Password must have minimum 6 characters."
                    } else {
                        toast("Authentication Failed: " + task.exception)
                        Log.d("Test", task.exception.toString())
                    }
                } else {
                    var model = RegisterModel(firstName.text.trim().toString(), lastName.text.trim().toString(),
                                tel.text.trim().toString(), email.text.trim().toString(), ConstValue.UserType_Disabled)
                    var id = reference.push().key
                    reference.child(id!!).setValue(model)

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("ระบบได้ทำการยืนยันการลงทะเบียนแล้วโปรดเข้าสู่ระบบเพื่อใช้งาน")
                        .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener {
                            mAuth!!.signOut();
                            val intent = Intent(this, WelcomeActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        })
                        .show()
                }
            }
        }

    }

    private fun checkInput(): Boolean {
        var result = true

        if (firstName.text.isNullOrEmpty()) {
            firstName.error = "กรุณากรอกชื่อ"
            result = false
        }

        if (lastName.text.isNullOrEmpty()) {
            lastName.error = "กรุณากรอกนามสกุล"
            result = false
        }

        if (tel.text.isNullOrEmpty()) {
            tel.error = "กรุณากรอกเบอร์โทรศัพท์"
            result = false
        }

        if (email.text.isNullOrEmpty()) {
            email.error = "กรุณากรอกอีเมล"
            result = false
        }

        if (password.text.isNullOrEmpty()) {
            password.error = "กรุณากรอกรหัสผ่าน"
            result = false
        }

        if (rePassword.text.isNullOrEmpty()) {
            rePassword.error = "กรุณากรอกยืนยันรหัสผ่าน"
            result = false
        }

        if (!password.text.isNullOrEmpty() && rePassword.text.isNullOrEmpty()) {
            rePassword.error = "กรุณากรอกยืนยันรหัสผ่าน"
            result = false
        }

        if (!password.text.isNullOrEmpty() && !rePassword.text.isNullOrEmpty()) {
            if (password.text.trim().toString() != rePassword.text.trim().toString()) {
                rePassword.error = "ยืนยันรหัสผ่านไม่ถูกต้อง"
                result = false
            }
        }
        return result
    }
}