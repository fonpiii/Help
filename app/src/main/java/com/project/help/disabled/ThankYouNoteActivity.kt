package com.project.help.disabled

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.project.help.model.UserDisabledModel

class ThankYouNoteActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recyclerFeed: RecyclerView
    private lateinit var iconLeft: ImageView
    private lateinit var userDisabled: UserDisabledModel
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutIsItem: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you_note)

        recyclerFeed = findViewById(R.id.recycler_feed)
        shimmer = findViewById(R.id.shimmerFrameLayout)
        layoutEmpty = findViewById(R.id.layout_Empty)
        layoutIsItem = findViewById(R.id.layout_IsItem)

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
        if ((intent.getParcelableExtra("User") as? UserDisabledModel) != null) {
            userDisabled = (intent.getParcelableExtra("User") as? UserDisabledModel)!!
        }
    }

    private fun setToolbar() {
        iconLeft = findViewById(R.id.iconLeft)
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
                reference.orderByChild("createBy").equalTo(userDisabled.userId).get().addOnSuccessListener { result ->
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
            postDetails.add(postDetail)
        }

        // Sort postDetails by date
        postDetails.sortByDescending { it.createDate }

        layoutEmpty.visibility = View.GONE
        layoutIsItem.visibility = View.VISIBLE

        if (postDetails.size == 0) {
            layoutEmpty.visibility = View.VISIBLE
            layoutIsItem.visibility = View.GONE
        }

        if (postDetails.size != 0) {
            recyclerFeed.adapter = ThankYouAdapter(postDetails, userDisabled)
            recyclerFeed.layoutManager = LinearLayoutManager(this)
            recyclerFeed.setHasFixedSize(true)
            closeShimmer()
        } else {
            closeShimmer()
        }
    }

    private fun closeShimmer() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }
}