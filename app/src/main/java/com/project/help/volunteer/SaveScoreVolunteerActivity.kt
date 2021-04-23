package com.project.help.volunteer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.model.CommentResponse
import com.project.help.model.UserModel
import java.util.ArrayList

class SaveScoreVolunteerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var toolbar: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var txtUsername: TextView
    private lateinit var ratingDisabled: RatingBar
    private lateinit var ratingScore: RatingBar
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var btnSaveScore: Button
    private lateinit var user: UserModel
    private lateinit var userDisabled: UserModel
    private lateinit var postUserId: String
    private lateinit var postDetailId: String
    private lateinit var postHelpId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_score_volunteer)

        btnSaveScore = findViewById(R.id.btnSaveScore)
        imgProfile = findViewById(R.id.imgProfile)
        txtUsername = findViewById(R.id.txtUsername_Feed)
        ratingDisabled = findViewById(R.id.ratingDisabled)
        ratingScore = findViewById(R.id.ratingScore)

        btnSaveScore.setOnClickListener(this)

        //region On init
        setFirebaseDatabase()
        setToolbar()

        if (!intent.getStringExtra("PostUserId").toString().isNullOrEmpty()) {
            postUserId = intent.getStringExtra("PostUserId").toString()
            getUserDisabled()
        }

        if (!intent.getStringExtra("PostHelpId").toString().isNullOrEmpty()) {
            postHelpId = intent.getStringExtra("PostHelpId").toString()
        }

        if (!intent.getStringExtra("PostDetailId").toString().isNullOrEmpty()) {
            postDetailId = intent.getStringExtra("PostDetailId").toString()
        }

        if ((intent.getParcelableExtra("User") as? UserModel) != null) {
            user = (intent.getParcelableExtra("User") as? UserModel)!!
        }
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("User")
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        toolbar = findViewById(R.id.toolbar)
        toolbar.setImageResource(R.drawable.header_volunteer)
        iconLeft.setOnClickListener(this)
    }

    private fun getUserDisabled() {
        if (!postUserId.isNullOrEmpty()) {
            reference.orderByKey().equalTo(postUserId).get().addOnSuccessListener {
                for (data in it.children) {
                    userDisabled = data.getValue(UserModel::class.java)!!
                    userDisabled.userId = data.key
                }

                if (userDisabled.firstName + " " + userDisabled.lastName == "ผู้พิการ 1") {
                    imgProfile.setImageResource(R.drawable.user)
                }
                txtUsername.text = userDisabled.firstName + " " + userDisabled.lastName
                ratingDisabled.rating = userDisabled.scoreDisabled.toFloat()
            }
        }
    }

    private fun saveScoreToDb() {
        val alertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        alertDialog.titleText = "คำเตือน ?"
        alertDialog.contentText = "ยืนยันการให้คะแนน ใช่หรือไม่"
        alertDialog.cancelText = "ไม่"
        alertDialog.setCancelClickListener { sDialog -> sDialog.cancel() }
        alertDialog.setConfirmText("ใช่")
            .setConfirmClickListener { sDialog ->
                var score = if (userDisabled.scoreDisabled == 0.0) {
                    ratingScore.rating
                } else {
                    ((ratingScore.rating + userDisabled.scoreDisabled)/2)
                }
                var databaseUser= FirebaseDatabase.getInstance().getReference("User")
                databaseUser.child(postUserId).child("scoreDisabled").setValue(score).addOnSuccessListener {
                    var databasePostHelp= FirebaseDatabase.getInstance().getReference("PostHelp")
                    databasePostHelp.child(postHelpId).child("assignScore").setValue(true).addOnSuccessListener {
                        sDialog.dismissWithAnimation()
                        var alert = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        alert.titleText = "ให้คะแนนเสร็จสิ้น"
                        alert.setConfirmClickListener {
                            finish()
                        }
                        alert.show()
                    }
                }
            }
        alertDialog.show()
        val btnConfirm = alertDialog.findViewById(R.id.confirm_button) as Button
        btnConfirm.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed))
        val btnCancel = alertDialog.findViewById(R.id.cancel_button) as Button
        btnCancel.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlack))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSaveScore -> {
                saveScoreToDb()
            }
            R.id.iconLeft -> finish()
        }
    }
}