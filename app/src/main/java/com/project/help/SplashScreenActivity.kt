package com.project.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.disabled.DisabledMainActivity
import com.project.help.model.UserModel
import com.project.help.volunteer.VolunteerMainActivity

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler: Handler
    var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        getSessionLogin()
        setFirebaseDatabase()

        handler = Handler()
        // Delayed 3 seconds to open main activity
        handler.postDelayed({
            if (mAuth!!.currentUser != null) {
                reference.orderByChild("email").equalTo(mAuth!!.currentUser.email).get().addOnSuccessListener { result ->
                    var user = UserModel()
                    for (data in result.children) {
                        user = data.getValue(UserModel::class.java)!!
                    }

                    if (user!!.userType == ConstValue.UserType_Disabled) {
                        var intent = Intent(this, DisabledMainActivity::class.java)
                        intent.putExtra("User", user)
                        startActivity(intent)
                        finishAffinity()
                    } else if (user!!.userType == ConstValue.UserType_Volunteer) {
                        startActivity(Intent(this, VolunteerMainActivity::class.java))
                        finishAffinity()
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            } else {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("User")
    }

    private fun getSessionLogin() {
        mAuth = FirebaseAuth.getInstance()
    }
}