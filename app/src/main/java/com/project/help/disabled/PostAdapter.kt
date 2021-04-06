package com.project.help.disabled

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.project.help.R
import com.project.help.disabled.model.PostDetailsResponse
import com.squareup.picasso.Picasso


class PostAdapter(private val postList: List<PostDetailsResponse>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.content_feed,
            parent, false)

        return PostViewHolder(itemView)
    }

    override fun getItemCount() = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postList[position]

//        holder.imageProfileFeed.setImageResource(currentItem.imageProfile)
        holder.imageProfileFeed.setImageResource(R.drawable.helplogo)
        holder.txtUsernameFeed.text = currentItem.firstName + " " + currentItem.lastName
        holder.postDetailFeed.text = currentItem.postDesc
        holder.ratingUserFeed.rating = currentItem.rating.toFloat()
        holder.categoryFeed.text = currentItem.categorys

        if (currentItem.comments == "") {
            holder.countCommentFeed.text = "0"
        } else {
            holder.countCommentFeed.text = "0"
        }

        if (currentItem.imageUrl != "") {
            holder.imagePostFeed.visibility = View.VISIBLE
            Picasso.get().load(currentItem.imageUrl).into(holder.imagePostFeed)
        }

        if (currentItem.videoUrl != "") {
            var mediaController = MediaController(holder.itemView.context)
            holder.videoPostFeed.setMediaController(mediaController)
            mediaController.setAnchorView(holder.videoPostFeed)
            holder.videoPostFeed.start()
            holder.videoPostFeed.visibility = View.VISIBLE
            holder.videoPostFeed.setVideoPath(currentItem.videoUrl)
            holder.videoPostFeed.start()
        }

        if (currentItem.audioUrl != "") {

        }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfileFeed: ImageView = itemView.findViewById(R.id.imgProfile_Feed)
        val txtUsernameFeed: TextView = itemView.findViewById(R.id.txtUsername_Feed)
        val postDetailFeed: TextView = itemView.findViewById(R.id.postDetail_Feed)
        val countCommentFeed: TextView = itemView.findViewById(R.id.countComment_Feed)
        val ratingUserFeed: RatingBar = itemView.findViewById(R.id.ratingUser_Feed)
        val imagePostFeed: ImageView = itemView.findViewById(R.id.imgPostDetail_Feed)
        val videoPostFeed: VideoView = itemView.findViewById(R.id.videoPostDetail_Feed)
        val categoryFeed: TextView = itemView.findViewById(R.id.category_Feed)
    }
}