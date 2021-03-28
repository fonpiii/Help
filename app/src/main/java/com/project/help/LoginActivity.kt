package com.project.help

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.disabled.DisabledMainActivity
import com.project.help.disabled.model.RegisterModel
import com.project.help.volunteer.VolunteerMainActivity
import org.jetbrains.anko.toast


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var submit: MaterialButton
    private lateinit var email: EditText
    private lateinit var password: EditText
    var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        submit = findViewById(R.id.submit)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

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
            R.id.iconLeft -> {
                finish()
            }
        }
    }

    private fun onClickBtnSubmit() {
        if (checkInput()) {
            mAuth!!.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length() < 6) {
                        password.error = "รหัสผ่านต้องมีความยาวมากกว่า 6 ตัว"
                    } else {
                        toast("Authentication Failed: " + task.exception)
                    }
                } else {
                    if (checkIsEmailVerified()) {
                        reference.orderByChild("email").equalTo(email.text.toString()).get().addOnSuccessListener { result ->
                            var user = RegisterModel()
                            for (data in result.children) {
                                user = data.getValue(RegisterModel::class.java)!!
                            }

                            if (user!!.userType == ConstValue.UserType_Disabled) {
                                Toast.makeText(this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, DisabledMainActivity::class.java))
                                finishAffinity()
                            } else if (user!!.userType == ConstValue.UserType_Volunteer) {
                                Toast.makeText(this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, VolunteerMainActivity::class.java))
                                finishAffinity()
                            }
                        }.addOnFailureListener{
                            Log.e("firebase", "Error getting data", it)
                        }
                    } else {
                        mAuth!!.signOut()
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("กรุณายืนยันอีเมลเพื่อเข้าสู่ระบบ")
                                .show()
                    }

//                    reference.orderByChild("email").equalTo(email.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
//
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            for (data in dataSnapshot.children) {
//                                val user = data.getValue(RegisterModel::class.java)
//                                Log.i("firebase", "Got value")
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Failed to read value
//                        }
//                    })
                }
            }
        }

    }

    private fun checkIsEmailVerified(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user.isEmailVerified
    }

    private fun checkInput(): Boolean {
        var result = true

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

        return result
    }
}