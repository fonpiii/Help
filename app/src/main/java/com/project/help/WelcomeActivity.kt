package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.disabled.DisabledMainActivity
import com.project.help.model.UserModel
import com.project.help.volunteer.VolunteerMainActivity

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    //region Global variable
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton
    var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var txtCountDisabled: TextView
    private lateinit var txtCountVolunteer: TextView
    //endregion Global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        txtCountDisabled = findViewById(R.id.countDisabled)
        txtCountVolunteer = findViewById(R.id.countVolunteer)

        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)

        setFirebaseDatabase()
        getCountUserType(ConstValue.UserType_Disabled)
        getCountUserType(ConstValue.UserType_Volunteer).toString()
    }

    private fun getCountUserType(userType: String) {
        var count: Int = 0
        reference.orderByChild("userType").equalTo(userType).get().addOnSuccessListener { users ->
            for (data in users.children) {
                count++
            }
            if (userType == ConstValue.UserType_Disabled) {
                txtCountDisabled.text = count.toString()
            } else if (userType == ConstValue.UserType_Volunteer) {
                txtCountVolunteer.text = count.toString()
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting disabled count", it)
        }
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("User")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnLogin -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.btnRegister -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}