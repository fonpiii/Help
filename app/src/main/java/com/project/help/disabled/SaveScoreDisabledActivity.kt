package com.project.help.disabled

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.model.CommentAdapter
import com.project.help.model.CommentResponse
import com.project.help.model.UserDisabledModel
import java.util.ArrayList

class SaveScoreDisabledActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var btnSaveScore: Button
    private lateinit var userDisabled: UserDisabledModel
    private lateinit var postId: String
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutIsItem: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_score_disabled)

        shimmer = findViewById(R.id.shimmerFrameLayout)
        recyclerFeed = findViewById(R.id.recycler_feed)
        btnSaveScore = findViewById(R.id.btnSaveScore)
        layoutEmpty = findViewById(R.id.layout_Empty)
        layoutIsItem = findViewById(R.id.layout_IsItem)

        btnSaveScore.setOnClickListener(this)

        //region On init
        setFirebaseDatabase()
        setToolbar()

        if (!intent.getStringExtra("PostDetailId").toString().isNullOrEmpty()) {
            postId = intent.getStringExtra("PostDetailId").toString()
            getComments()
        }

        if ((intent.getParcelableExtra("User") as? UserDisabledModel) != null) {
            userDisabled = (intent.getParcelableExtra("User") as? UserDisabledModel)!!
        }
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("PostDetails")
    }

    private fun getComments() {
        if (!postId.isNullOrEmpty()) {
            shimmer.startShimmerAnimation()
            var database = FirebaseDatabase.getInstance().getReference("Comments")
            database.orderByChild("postDetailId").equalTo(postId).get().addOnSuccessListener { result ->
                var comments = ArrayList<CommentResponse>()
                for (data in result.children) {
                    var comment: CommentResponse = data.getValue(CommentResponse::class.java)!!
                    if (comment.createBy != userDisabled.userId && comment.userType == ConstValue.UserType_Volunteer) {
                        comment.id = data.key.toString()
                        comments.add(comment)
                    }
                    // For test
//                    comment.id = data.key.toString()
//                    comments.add(comment)
                }

                // Sort postDetails by date
                comments.sortByDescending { it.createDate }

                layoutEmpty.visibility = View.GONE
                layoutIsItem.visibility = View.VISIBLE

                if (comments.size == 0) {
                    layoutEmpty.visibility = View.VISIBLE
                    layoutIsItem.visibility = View.GONE
                }

                recyclerFeed.adapter = CommentAdapter(comments, userDisabled, "SaveScore")
                recyclerFeed.layoutManager = LinearLayoutManager(this)
                recyclerFeed.setHasFixedSize(true)
                closeShimmer()
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }
    }

    private fun closeShimmer() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        iconLeft.setOnClickListener(this)
    }

    private fun saveScoreToDb() {
        val alertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        alertDialog.titleText = "คำเตือน ?"
        alertDialog.contentText = "ต้องการยืนยันการปิดโพสต์นี้ ใช่หรือไม่"
        alertDialog.cancelText = "ไม่"
        alertDialog.setCancelClickListener { sDialog -> sDialog.cancel() }
        alertDialog.confirmText = "ใช่"
        alertDialog.setConfirmClickListener { sDialog ->
                    var databaseComment = FirebaseDatabase.getInstance().getReference("PostDetails")
                    databaseComment.child(postId).child("close").setValue(true).addOnSuccessListener {
                        sDialog.dismissWithAnimation()
                        var alert = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        alert.titleText = "ปิดโพสต์เสร็จสิ้น"
                        alert.setConfirmClickListener {
                            finish()
                        }
                        alert.show()
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