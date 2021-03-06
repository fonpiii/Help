package com.project.help.volunteer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.Utilities
import com.project.help.WelcomeActivity
import com.project.help.disabled.DisabledMainActivity
import com.project.help.disabled.model.RegisterDisabledModel
import com.project.help.volunteer.model.RegisterVolunteerModel
import org.jetbrains.anko.toast

class RegisterVolunteerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var submit: MaterialButton
    private lateinit var firstName: EditText
    private lateinit var tel: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var rePassword: EditText
    private lateinit var country: EditText
    private lateinit var zipCode: EditText
    var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_volunteer)

        submit = findViewById(R.id.submit)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        tel = findViewById(R.id.tel)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        rePassword = findViewById(R.id.re_password)
        country = findViewById(R.id.country)
        zipCode = findViewById(R.id.zipCode)

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
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Utilities.Alert.alertDialog("มีบัญชีที่สัมพันธ์กับอีเมลนี้อยู่แล้ว", SweetAlertDialog.ERROR_TYPE, this)
                    }
                } else {
                    if (sendEmailVerification()) {
                        var model = RegisterVolunteerModel(firstName.text.trim().toString(), lastName.text.trim().toString(),
                                tel.text.trim().toString(), email.text.trim().toString(), ConstValue.UserType_Volunteer,
                                "", country.text.trim().toString(), zipCode.text.trim().toString(), 0.0, 0.0, ServerValue.TIMESTAMP)
                        var id = reference.push().key
                        reference.child(id!!).setValue(model)

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("ระบบได้ทำการยืนยันการลงทะเบียนและได้ส่งอีเมลเพื่อยืนยันตัวตนแล้วโปรดเข้าสู่ระบบเพื่อใช้งาน")
                                .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener {
                                    mAuth!!.signOut();
                                    startActivity(Intent(this, WelcomeActivity::class.java))
                                    finishAffinity()
                                })
                                .show()
                    }
                }
            }
        }

    }

    private fun sendEmailVerification(): Boolean {
        var result = true
        val user = FirebaseAuth.getInstance().currentUser
        user.sendEmailVerification().addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.d("Send Email", it.exception.toString())
                result = false
            }
        }
        return result
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
        } else {
            if (!Utilities.Validation.isValidEmail(email.text.trim().toString()) || password.length() < 6)
            {
                email.error = "รูปแบบอีเมลไม่ถูกต้อง"
                result = false
            }
        }

        if (password.text.isNullOrEmpty()) {
            password.error = "กรุณากรอกรหัสผ่าน"
            result = false
        } else {
            if (!Utilities.Validation.validatePassword(password.text.trim().toString())) {
                password.error = "รหัสผ่านต้องมีอย่างน้อยหกตัวให้ผสมกันทั้งตัวเลข ตัวอักษร และอักษรพิเศษ"
                result = false
            }
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

        if (country.text.isNullOrEmpty()) {
            country.error = "กรุณากรอกจังหวัด"
            result = false
        }

        if (zipCode.text.isNullOrEmpty()) {
            zipCode.error = "กรุณากรอกรหัสไปรษณีย์"
            result = false
        }

        if (!zipCode.text.isNullOrEmpty() && zipCode.text.length < 5) {
            zipCode.error = "รูปแบบรหัสไปรษณีย์ไม่ถูกต้อง"
            result = false
        }
        return result
    }
}