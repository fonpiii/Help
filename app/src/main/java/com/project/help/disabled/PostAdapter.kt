package com.project.help.disabled

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.help.R

class PostAdapter(private val postList: List<PostItem>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.content_feed,
            parent, false)

        return PostViewHolder(itemView)
    }

    override fun getItemCount() = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postList[position]

        holder.imageProfileFeed.setImageResource(currentItem.imageProfile)
        holder.txtUsernameFeed.text = currentItem.username
        holder.postDetailFeed.text = currentItem.postDetail
        holder.countCommentFeed.text = currentItem.count.toString()
        holder.ratingUserFeed.rating = currentItem.rating
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfileFeed: ImageView = itemView.findViewById(R.id.imgProfile_Feed)
        val txtUsernameFeed: TextView = itemView.findViewById(R.id.txtUsername_Feed)
        val postDetailFeed: TextView = itemView.findViewById(R.id.postDetail_Feed)
        val countCommentFeed: TextView = itemView.findViewById(R.id.countComment_Feed)
        val ratingUserFeed: RatingBar = itemView.findViewById(R.id.ratingUser_Feed)
    }
}