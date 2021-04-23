package com.project.help.volunteer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.disabled.model.PostDetailsResponse
import com.project.help.disabled.model.ThankYouAdapter
import com.project.help.model.PostHelpResponse
import com.project.help.model.UserModel

class AssignScoreActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recyclerFeed: RecyclerView
    private lateinit var iconLeft: ImageView
    private lateinit var toolbar: ImageView
    private lateinit var user: UserModel
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_score)

        recyclerFeed = findViewById(R.id.recycler_feed)
        shimmer = findViewById(R.id.shimmerFrameLayout)

        //region On init
        setFirebaseDatabase()
        setToolbar()
        setUser()
        getPosts(ConstValue.getById, "")
        //endregion On init
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iconLeft -> finish()
        }
    }

    private fun setUser() {
        if ((intent.getParcelableExtra("User") as? UserModel) != null) {
            user = (intent.getParcelableExtra("User") as? UserModel)!!
        }
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
        toolbar = findViewById(R.id.toolbar)
        toolbar.setImageResource(R.drawable.header_volunteer)
        iconLeft.setOnClickListener(this)
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("PostDetails")
    }

    override fun onResume() {
        getPosts(ConstValue.getById, "")
        super.onResume()
    }

    override fun onPause() {
        getPosts(ConstValue.getById, "")
        super.onPause()
    }

    private fun getPosts(getBy: String, value: String) {
        shimmer.startShimmerAnimation()
        when (getBy) {
            ConstValue.getById -> {
                reference.get().addOnSuccessListener { result ->
                    setPostDetails(result)
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }
        }
    }

    private fun setPostDetails(result: DataSnapshot) {
        var postDetails = ArrayList<PostDetailsResponse>()
        for (data in result.children) {
            var postDetail: PostDetailsResponse = data.getValue(PostDetailsResponse::class.java)!!
            postDetail.id = data.key.toString()
            if (postDetail.close) {
                postDetails.add(postDetail)
            }
        }

        var database = FirebaseDatabase.getInstance().getReference("PostHelp")
        database.orderByChild("userId").equalTo(user.userId).get().addOnSuccessListener { it ->
            var postHelps = java.util.ArrayList<PostHelpResponse>()
            for (data in it.children) {
                var postHelp: PostHelpResponse = data.getValue(PostHelpResponse::class.java)!!
                postHelps.add(postHelp)
            }
            var resultPostHelp = ArrayList<PostDetailsResponse>()
            if (postHelps.size > 0) {
                for (data in postHelps) {
                    var index = postDetails.indexOfFirst  { it.id == data.postDetailId }
                    if (index != -1) {
                        resultPostHelp.add(postDetails[index])
                    }
                }
                // Sort postDetails by date
                resultPostHelp.sortByDescending { it.createDate }

                recyclerFeed.adapter = ThankYouAdapter(resultPostHelp, user)
                recyclerFeed.layoutManager = LinearLayoutManager(this)
                recyclerFeed.setHasFixedSize(true)
                closeShimmer()
            }
        }
    }

    private fun closeShimmer() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }
}