package com.project.help.volunteer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.help.ConstValue
import com.project.help.R
import com.project.help.disabled.model.PostAdapter
import com.project.help.disabled.model.PostDetailsResponse
import com.project.help.model.PostHelpResponse
import com.project.help.model.UserDisabledModel

class PostHelpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recyclerFeed: RecyclerView
    private lateinit var iconLeft: ImageView
    private lateinit var toolbar: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var userDisabled: UserDisabledModel
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_help)

        recyclerFeed = findViewById(R.id.recycler_feed)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        shimmer = findViewById(R.id.shimmerFrameLayout)

        //region On init
        setFirebaseDatabase()
        setToolbar()
        setUser()
        setSpinnerCategory()
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
        toolbar = findViewById(R.id.toolbar)
        toolbar.setImageResource(R.drawable.header_volunteer)
        iconLeft.setOnClickListener(this)
    }

    private fun setFirebaseDatabase() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("PostDetails")
    }

    private fun getPosts(getBy: String, value: String) {
        shimmer.startShimmerAnimation()
        when (getBy) {
            ConstValue.getById -> {
                reference.get().addOnSuccessListener { result ->
                    setPostDetails(result, getBy, value)
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }
            ConstValue.getByCategory -> {
//                reference.orderByChild("createBy").equalTo(user.userId).get().addOnSuccessListener { result ->
//                    setPostDetails(result, getBy, value)
//                }.addOnFailureListener{
//                    Log.e("firebase", "Error getting data", it)
//                }
                reference.get().addOnSuccessListener { result ->
                    setPostDetails(result, getBy, value)
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }
        }
    }

    private fun setPostDetails(result: DataSnapshot, getBy: String, value: String) {
        var postDetails = ArrayList<PostDetailsResponse>()
        for (data in result.children) {
            var postDetail: PostDetailsResponse = data.getValue(PostDetailsResponse::class.java)!!
            postDetail.id = data.key.toString()
            postDetails.add(postDetail)
        }

        var database = FirebaseDatabase.getInstance().getReference("PostHelp")
        database.orderByChild("userId").equalTo(userDisabled.userId).get().addOnSuccessListener { it ->
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

                if (getBy == ConstValue.getByCategory) {
                    var filter = resultPostHelp.filter { it.categorys == value }
                    addPostAdapter(filter as ArrayList<PostDetailsResponse>)
                } else if (getBy == ConstValue.getById) {
                    addPostAdapter(resultPostHelp)
                }
            } else {
                closeShimmer()
            }
        }
    }

    private fun addPostAdapter(postDetails: ArrayList<PostDetailsResponse>) {
        recyclerFeed.adapter =
                PostAdapter(postDetails, userDisabled)
        recyclerFeed.layoutManager = LinearLayoutManager(this)
        recyclerFeed.setHasFixedSize(true)
        closeShimmer()
    }

    private fun closeShimmer() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = View.GONE
        recyclerFeed.visibility = View.VISIBLE
    }

    private fun setSpinnerCategory() {
        var categories = arrayOf("ทั้งหมด", "การมองเห็น", "การได้ยิน", "การเคลื่อนไหวร่างกาย", "สติปัญญา",
                "ออทิสติก", "ผู้สูงอายุ")

        var adapter = ArrayAdapter(this, R.layout.color_spinner_layout, categories)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    getPosts(ConstValue.getById, "")
                } else {
                    getPosts(ConstValue.getByCategory, categories[p2])
                }
            }
        }
    }
}