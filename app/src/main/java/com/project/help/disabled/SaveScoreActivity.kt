package com.project.help.disabled

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.R
import com.project.help.model.CommentAdapter
import com.project.help.model.CommentResponse
import com.project.help.model.UserModel
import java.util.ArrayList

class SaveScoreActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconLeft: ImageView
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerFeed: RecyclerView
    private lateinit var btnSaveScore: Button
    private lateinit var user: UserModel
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_score)

        shimmer = findViewById(R.id.shimmerFrameLayout)
        recyclerFeed = findViewById(R.id.recycler_feed)
        btnSaveScore = findViewById(R.id.btnSaveScore)

        btnSaveScore.setOnClickListener(this)

        //region On init
        setFirebaseDatabase()
        setToolbar()

        if (!intent.getStringExtra("PostDetailId").toString().isNullOrEmpty()) {
            postId = intent.getStringExtra("PostDetailId").toString()
            getComments()
        }

        if ((intent.getParcelableExtra("User") as? UserModel) != null) {
            user = (intent.getParcelableExtra("User") as? UserModel)!!
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
                    if (comment.createBy != user.userId) {
                        comment.id = data.key.toString()
                        comments.add(comment)
                    }
                    // For test
//                    comment.id = data.key.toString()
//                    comments.add(comment)
                }

                // Sort postDetails by date
                comments.sortByDescending { it.createDate }

                if (comments.size != 0) {
                    recyclerFeed.adapter = CommentAdapter(comments, user, "SaveScore")
                    recyclerFeed.layoutManager = LinearLayoutManager(this)
                    recyclerFeed.setHasFixedSize(true)
                    closeShimmer()
                } else {
                    closeShimmer()
                }
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
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("คำเตือน ?")
                .setContentText("ต้องการยืนยันการปิดโพสต์นี้ ใช่หรือไม่")
                .setCancelText("ไม่")
                .setCancelClickListener { sDialog -> sDialog.cancel() }
                .setConfirmText("ใช่")
                .setConfirmClickListener { sDialog ->
                    var databaseComment = FirebaseDatabase.getInstance().getReference("PostDetails")
                    databaseComment.child(postId).child("close").setValue(true).addOnSuccessListener {
                        sDialog.dismissWithAnimation()
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("ปิดโพสต์เสร็จสิ้น")
                                .show()
                        finish()
                    }
                }
                .show()
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